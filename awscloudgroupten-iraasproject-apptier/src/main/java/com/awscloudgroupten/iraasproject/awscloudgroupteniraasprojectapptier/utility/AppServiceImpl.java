package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.utility;

import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.GlobalConstant;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.db.AWSS3Handler;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.handle.AWSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Abhishek Bakare
 *
 * This class take care of all the major functions related to the APP-TIER.
 */
public class AppServiceImpl implements AppService, Runnable{

    @Autowired
    private AWSConfig awsConfig;

    @Autowired
    private AWSS3Handler awss3Handler;

    // Logger to log the processes happening in the class
    private static final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

    /**
     * Creates a SQS queue if it does not exists
     *
     * Ref: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-message-queues.html
     * @param queue
     */
    @Override
    public void createAWSQueue(String queue) {
        logger.info("Trying to create new queue " + queue);
        CreateQueueRequest rq = new CreateQueueRequest().withQueueName(queue);
        rq.addAttributesEntry(QueueAttributeName.FifoQueue.toString(), Boolean.TRUE.toString());
        rq.addAttributesEntry(QueueAttributeName.ContentBasedDeduplication.toString(), Boolean.TRUE.toString());
        try {
            awsConfig.getSQS().createQueue(rq);
        } catch (QueueDeletedRecentlyException queueDeletedRecentlyException) {
            logger.info("Wait to create queue with name " + queue + " for 60 secs");
        } catch (Exception e) {
            logger.info("Queue already exists");
        }
    }

    /**
     * Get number of messages from the queue.
     *
     * Ref: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_GetQueueAttributes.html
     * @param queue
     * @return number of messages in the sqs queue
     */
    @Override
    public int getQuantityOfMessagesInQueue(String queue) {
        logger.info("Trying to fetch no of messages in queue " + queue);
        String locator = getQueueURL(queue);
        GetQueueAttributesRequest request = new GetQueueAttributesRequest(locator, GlobalConstant.AWSSQSList_GTen);
        int noOfMsgs = Integer.parseInt(awsConfig.getSQS().getQueueAttributes(request).getAttributes().get(GlobalConstant.AWSSQSMsg_GTen));
        logger.info("No of msgs in queue " + queue + " are " + noOfMsgs);
        return noOfMsgs;
    }

    /**
     * Append message in the sqs queue.
     *
     * Ref: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-messages.html
     * @param queue
     * @param msg
     */
    @Override
    public void addMessageToQueue(String queue, String msg) {
        logger.info("Adding msg " + msg + " to queue " + queue);
        String locator = getQueueURL(queue);

        SendMessageRequest request = new SendMessageRequest();
        request.withQueueUrl(locator);
        request.withMessageGroupId(UUID.randomUUID().toString());
        request.withMessageBody(msg);
        request.withDelaySeconds(0);

        awsConfig.getSQS().sendMessage(request);

        logger.info("Successfully added msg " + msg + " to queue " + queue);
    }

    /**
     * Delete the message from the given queue
     *
     * Ref: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-messages.html
     * @param queue
     * @param msg
     */
    @Override
    public void removeMessageFromQueue(String queue, Message msg) {
        logger.info("Deleting msg " + msg + " from queue " + queue);
        String locator = awsConfig.getSQS().getQueueUrl(queue).getQueueUrl();
        DeleteMessageRequest request = new DeleteMessageRequest(locator, msg.getReceiptHandle());
        awsConfig.getSQS().deleteMessage(request);
        logger.info("Successfully deleted msg " + msg + " from queue " + queue);
    }

    /**
     * Terminate the EC2 Instance
     *
     * Ref: https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/Ec2Client.html#terminateInstances-software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest-
     */
    @Override
    public void terminateEC2Instance() {
        logger.info("Trying to terminate instance.");
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.withInstanceIds(EC2MetadataUtils.getInstanceId());
        awsConfig.getEC2().terminateInstances(request);
        logger.info("Successfully terminated the instance.");
    }

    /**
     * Get first message from SQS queue
     *
     * Ref: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-messages.html
     * @param queue
     * @return
     */
    @Override
    public Message getMsg(String queue) {
        logger.info("Trying to get msg from queue " + queue);
        String locator = awsConfig.getSQS().getQueueUrl(queue).getQueueUrl();
        ReceiveMessageRequest request = new ReceiveMessageRequest(locator);
        request.setVisibilityTimeout(GlobalConstant.AWSSQSVisibilityTimeout_GTen);
        request.setMaxNumberOfMessages(1);
        request.setWaitTimeSeconds(GlobalConstant.AWSWait_GTen);

        ReceiveMessageResult result = awsConfig.getSQS().receiveMessage(request);
        Message msg = result.getMessages().isEmpty() ? null : result.getMessages().get(0);
        logger.info("Message retrieved " + (msg == null ? "No message" : msg.toString()));
        return msg;
    }

    /**
     * Get and process a message from queue
     */
    @Override
    public void processMessage() {
        while (true) {
            Message msg = getMsg(GlobalConstant.AWSSQSInputQueue_GTen);
            if (msg == null)    break;
            try {
                logger.info("Processing message: " + msg.toString());
                String fileName = msg.getBody();
                InputStream stream = getStream(fileName);
                File file = getFile(fileName, stream);
                String classification = getClassification(fileName);
                this.addMessageToQueue(GlobalConstant.AWSSQSOutputQueue_GTen, fileName + GlobalConstant.Delimeter_GTen + classification);
                awss3Handler.putInDB(formatInputRequest(fileName), classification);
                file.delete();
                removeMessageFromQueue(GlobalConstant.AWSSQSInputQueue_GTen, msg);
            } catch (Exception e) {
                logger.error("Issue occurred while processing message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get file object from reading the image input stream
     *
     * @param fileName
     * @param stream
     * @return
     * @throws IOException
     */
    private File getFile(String fileName, InputStream stream) throws IOException {
        File imageFile = new File(GlobalConstant.FilePath_GTen + fileName);
        // Read file stream to file object
        FileOutputStream image = new FileOutputStream(imageFile);
        byte[] imageByteArr = new byte[8192];
        int length;
        while ((length = stream.read(imageByteArr)) > 0)  image.write(imageByteArr, 0, length);
        image.close();
        return imageFile;
    }

    /**
     * S3 image object to file input stream
     *
     * Ref: https://docs.aws.amazon.com/code-samples/latest/catalog/java-s3-src-main-java-aws-example-s3-GetObject.java.html
     * @param fileName
     * @return
     */
    private InputStream getStream(String fileName) {
        S3Object s3Object = awsConfig.getS3().getObject(new GetObjectRequest(GlobalConstant.AWSS3InputBucket_GTen, fileName));
        return s3Object.getObjectContent();
    }

    /**
     * Invoke the python script and get the classification result. It is done through ProcessBuilder.
     *
     * @param fileName
     * @return
     */
    public String getClassification(String fileName) {
        List<String> result = null;
        try {
            logger.info("Trying to get classification result");
            ProcessBuilder pythonProcess = new ProcessBuilder(GlobalConstant.PythonVersion_GTen,
                    getPath(GlobalConstant.ClassifierPythonFile_GTen), getPath(fileName));
            pythonProcess.redirectErrorStream(true);

            Process process = pythonProcess.start();
            result = fileStreamToList(process.getInputStream());
            logger.info("Retried classification result");
        } catch (Exception e) {
            logger.error("Issue occurred while retrieving classification" + e.getMessage());
            e.printStackTrace();
        }

        return (result == null || result.isEmpty()) ? GlobalConstant.ClassificationError_GTen : result.get(result.size() - 1);
    }

    /**
     * Get the output buffer
     *
     * @param inputStream
     * @return
     */
    private static List<String> fileStreamToList(InputStream inputStream) {
        BufferedReader output = null;
        try {
            output = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return output == null ? Collections.singletonList("Nothing found") : output.lines().collect(Collectors.toList());
    }

    /**
     * Get file path
     *
     * @param filename
     * @return
     */
    private static String getPath(String filename) {
        return new File(GlobalConstant.FilePath_GTen + filename).getAbsolutePath();
    }

    /**
     * Format the input file (image) name
     *
     * @param fileName
     * @return
     */
    public String formatInputRequest(String fileName) {
        return fileName.substring(fileName.indexOf("-") + 1, fileName.lastIndexOf("."));
    }

    /**
     * Get the queue url (if not present then first create queue)
     *
     * Ref: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQSClient.html#getQueueUrl-java.lang.String
     *
     * @param queue
     * @return
     */
    private String getQueueURL(String queue) {
        logger.info("Getting queue URL");
        String queueURL = null;
        try{
            queueURL = awsConfig.getSQS().getQueueUrl(queue).getQueueUrl();
        } catch (Exception e) {
            // If queue not exists create queue
            createAWSQueue(queue);
        }
        logger.info("Got the queue url for " + queue + " as " + queueURL);
        return queueURL;
    }

    @Override
    public void run() {
        logger.info("Running thread");
        processMessage();
    }
}
