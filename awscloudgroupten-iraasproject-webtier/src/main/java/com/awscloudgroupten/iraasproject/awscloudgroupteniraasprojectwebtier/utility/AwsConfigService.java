package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility;
import java.io.File;
import java.util.*;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.handle.AwsGeneralConfiguration;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.WebAppConstant;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.db.IAwsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.Base64;

/**
 * @author Kenil Limbani | CSE 546 - Group 10
 * Reference: [https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3Client.html]
 */
@Service
public class AwsConfigService implements IAwsConfigService, Runnable {

	@Autowired
	private AwsGeneralConfiguration awsGeneralConfiguration;

	@Autowired
	private IGeneralService iGeneralService;

	@Autowired
	private IAwsRepository iAwsRepository;

	@Override
	public void run() {
		this.scaleOutInstances();
	}

	/**
	 *
	 * @param id
	 * @param instType
	 * @param min
	 * @param max
	 */
	@Override
	public void initializeAndStartInstance(String id, String instType, Integer min, Integer max) {
		try {
			Integer numOfInstances = countNumberOfInstances();
			if (numOfInstances + max > WebAppConstant.MaximumNumberOfAppInstances_GTen) {
				if (WebAppConstant.MaximumNumberOfAppInstances_GTen - numOfInstances > 0) {
					max = WebAppConstant.MaximumNumberOfAppInstances_GTen - numOfInstances;
					min = max == 1 ? 1 : max-1;
				} else {
					return;
				}
			}

			RunInstancesRequest instancesRequest = getAwsAllTagsAndRunInstances(id, instType, min, max);
			awsGeneralConfiguration.accessEc2Instance().runInstances(instancesRequest);
		} catch (Exception e) {
			System.out.println("EXCEPTION - In Initializing EC2 Instance");
			e.printStackTrace();
			return;
		}
	}

	private RunInstancesRequest getAwsAllTagsAndRunInstances(String id, String instType, Integer min, Integer max) throws Exception{
		try{
			Collection<Tag> tags = new ArrayList<>();
			TagSpecification tagSpecification = new TagSpecification();
			Tag t = new Tag();
			t.setKey(WebAppConstant.EC2TagKey_GTen);
			t.setValue(WebAppConstant.EC2TagValue_GTen);
			tags.add(t);
			tagSpecification.setResourceType(WebAppConstant.ResourceInstance_GTen);
			tagSpecification.setTags(tags);
			RunInstancesRequest instancesRequest = new RunInstancesRequest().withImageId(id).withInstanceType(instType).withMinCount(min).withMaxCount(max).withKeyName(WebAppConstant.EC2KeyPair_GTen).withTagSpecifications(tagSpecification)
					.withUserData(new String(Base64.encode(WebAppConstant.AppTierData_GTen.getBytes("UTF-8")), "UTF-8"));

			return instancesRequest;
		}
		catch(Exception e){
			throw e;
		}

	}

	@Override
	public Integer countNumberOfInstances() {
		DescribeInstanceStatusRequest instanceStatusRequest = new DescribeInstanceStatusRequest();
		instanceStatusRequest.setIncludeAllInstances(true);
		DescribeInstanceStatusResult describeInstances = awsGeneralConfiguration.accessEc2Instance().describeInstanceStatus(instanceStatusRequest);
		List<InstanceStatus> instanceStatusList = describeInstances.getInstanceStatuses();
		Integer num = getInstanceCount(instanceStatusList);
		return num - 1;
	}

	private Integer getInstanceCount(List<InstanceStatus> instanceStatusList){
		Integer num = 0;
		for (InstanceStatus is : instanceStatusList)
			if (is.getInstanceState().getName().equals(InstanceStateName.Running.toString())
					|| is.getInstanceState().getName().equals(InstanceStateName.Pending.toString()))
				num++;

		return num;
	}

	@Override
	public void sqsInput(String s, String sqsName, int time) {
		String qLink = null;

		try {
			qLink = awsGeneralConfiguration.accessSqsQueue().getQueueUrl(sqsName).getQueueUrl();
		} catch (Exception e) {
			generateQueue(sqsName);
		}

		qLink = awsGeneralConfiguration.accessSqsQueue().getQueueUrl(sqsName).getQueueUrl();
		awsGeneralConfiguration.accessSqsQueue().sendMessage(new SendMessageRequest().withQueueUrl(qLink).withMessageGroupId(UUID.randomUUID().toString()).withMessageBody(s).withDelaySeconds(0));
	}

	@Override
	public String[] accessResponseQueue(String s) {
		while (true) {
			try {
				if (IGeneralService.result.containsKey(s)) {
					String output = IGeneralService.result.get(s);
					IGeneralService.result.remove(s);
					return new String[] { s.substring(s.indexOf('-') + 1, s.lastIndexOf('.')), output };
				} else {
					try {
						Thread.sleep(9000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println("EXCEPTION - In Accessing Response Queue: " + e.getMessage());
				try {
					Thread.sleep(9000);
				} catch (Exception o) {
					o.printStackTrace();
				}
			}
		}
	}

	@Override
	public void generateQueue(String s) {
		try {
			awsGeneralConfiguration.accessSqsQueue().createQueue(new CreateQueueRequest().withQueueName(s)
					.addAttributesEntry(QueueAttributeName.FifoQueue.toString(), Boolean.TRUE.toString())
					.addAttributesEntry(QueueAttributeName.ContentBasedDeduplication.toString(),
							Boolean.TRUE.toString()));
		} catch (Exception e) {
			System.out.println("EXCEPTION - In Generating Queue: " + e.getMessage());
		}
	}

	@Override
	public void putImageS3(MultipartFile multipartFiles, String s) {
		try {
			File f = iGeneralService.changeFileFormat(multipartFiles);
			iAwsRepository.putFileOnInstance(f, s);
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void scaleOutInstances() {
		while (true) {
			Integer numberOfMessagesFromQueue = countNumberOfMessagesFromQueue(WebAppConstant.AWSSQSInputQueue_GTen);
			Integer numberOfInstances = countNumberOfInstances();
			Integer countToRun = getCountToRun(numberOfInstances, numberOfMessagesFromQueue);
			System.out.println("INFO - New Instances To Run: " + countToRun);
			if (countToRun == 1) {
				initializeAndStartInstance(WebAppConstant.AmazonMachineImageId_GTen, WebAppConstant.EC2InstanceType_GTen, 1, 1);
			} else if (countToRun > 1) {
				initializeAndStartInstance(WebAppConstant.AmazonMachineImageId_GTen, WebAppConstant.EC2InstanceType_GTen, countToRun - 1, countToRun);
			}
			try {
				Thread.sleep(2500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Integer getCountToRun(Integer numberOfInstances, Integer numberOfMessagesFromQueue){
		Integer countToRun = 0;
		if (numberOfInstances < numberOfMessagesFromQueue) {
			if (numberOfMessagesFromQueue - numberOfInstances < WebAppConstant.MaximumNumberOfAppInstances_GTen) {
				countToRun = numberOfMessagesFromQueue - numberOfInstances;
			} else {
				countToRun = WebAppConstant.MaximumNumberOfAppInstances_GTen - numberOfInstances;
			}
		}

		return countToRun;
	}

	@Override
	public Integer countNumberOfMessagesFromQueue(String s) {
		String qLink = null;

		try {
			qLink = awsGeneralConfiguration.accessSqsQueue().getQueueUrl(s).getQueueUrl();
		} catch (Exception e) {
			generateQueue(s);
		}
		qLink = awsGeneralConfiguration.accessSqsQueue().getQueueUrl(s).getQueueUrl();
		Map<String, String> map = awsGeneralConfiguration.accessSqsQueue().getQueueAttributes(new GetQueueAttributesRequest(qLink, WebAppConstant.SQSMetrics_GTen)).getAttributes();
		return Integer.parseInt((String) map.get(WebAppConstant.TotalMsgInQueue_GTen));
	}

	@Override
	public List<Message> getMessage(String s, Integer timeout, Integer wait, Integer numMsg) {
		try {
			ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(awsGeneralConfiguration.accessSqsQueue().getQueueUrl(s).getQueueUrl());
			messageRequest.setMaxNumberOfMessages(numMsg);
			messageRequest.setVisibilityTimeout(timeout);
			messageRequest.setWaitTimeSeconds(wait);
			ReceiveMessageResult receiveMessageResult = awsGeneralConfiguration.accessSqsQueue()
					.receiveMessage(messageRequest);
			List<Message> messageList = receiveMessageResult.getMessages();
			if (messageList.isEmpty()) {
				System.out.println("INFO - Message List Empty");
				return null;
			}
			return messageList;
		} catch (Exception e) {
			System.out.println("EXCEPTION - Getting Messages: " + e.getMessage());

			System.out.println("INFO - Thread Sleeping for 10 seconds");
			try {
				Thread.sleep(9000);
			} catch (Exception p) {
				System.out.println("EXCEPTION - In Thread Sleep");
			}
			return null;
		}
	}

	@Override
	public void removeMessage(Message m, String s) {
		try {
			DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(awsGeneralConfiguration.accessSqsQueue().getQueueUrl(s).getQueueUrl(), m.getReceiptHandle());
			awsGeneralConfiguration.accessSqsQueue().deleteMessage(deleteMessageRequest);
		} catch (Exception e) {
			System.out.println("EXCEPTION - In Removing Messages: " + e.getMessage());
		}
	}

	@Override
	public void initializeAwsServices(String iq, String oq, String ib, String ob) {
		try {
			try {
				awsGeneralConfiguration.accessSqsQueue().getQueueUrl(iq).getQueueUrl();
			} catch (Exception e) {
				generateQueue(iq);
			}

			try {
				awsGeneralConfiguration.accessSqsQueue().getQueueUrl(oq).getQueueUrl();
			} catch (Exception e) {
				generateQueue(oq);
			}

			try {
				if (!awsGeneralConfiguration.accessS3Bucket().doesBucketExistV2(ib))
					awsGeneralConfiguration.accessS3Bucket().createBucket(ib);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (!awsGeneralConfiguration.accessS3Bucket().doesBucketExistV2(ob))
					awsGeneralConfiguration.accessS3Bucket().createBucket(ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeBucket(String s) {
		try {
			ObjectListing listObjects = awsGeneralConfiguration.accessS3Bucket().listObjects(s);
			while (true) {
				Iterator<S3ObjectSummary> iterator = listObjects.getObjectSummaries().iterator();
				while (iterator.hasNext()) {
					awsGeneralConfiguration.accessS3Bucket().deleteObject(s, iterator.next().getKey());
				}

				if (listObjects.isTruncated()) {
					listObjects = awsGeneralConfiguration.accessS3Bucket().listNextBatchOfObjects(listObjects);
				} else {
					break;
				}
			}
			VersionListing listing = awsGeneralConfiguration.accessS3Bucket().listVersions(new ListVersionsRequest().withBucketName(s));
			while (true) {
				Iterator<S3VersionSummary> vIterator = listing.getVersionSummaries().iterator();
				while (vIterator.hasNext()) {
					S3VersionSummary vs = vIterator.next();
					awsGeneralConfiguration.accessS3Bucket().deleteVersion(s, vs.getKey(), vs.getVersionId());
				}

				if (listing.isTruncated()) {
					listing = awsGeneralConfiguration.accessS3Bucket().listNextBatchOfVersions(listing);
				} else {
					break;
				}
			}
			awsGeneralConfiguration.accessS3Bucket().deleteBucket(s);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
