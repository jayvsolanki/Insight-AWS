package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier;

import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vinayak Kalunge | CSE 546 Group 10
 */
public class WebAppConstant {

	// Users Access ID
	public static final String AWSAccessKeyId_GTen = "";

	// Users Access Key
	public static final String AWSSecretKey_GTen = "";

	// App-Tiers AMI ID
	public static final String AmazonMachineImageId_GTen = "";

	// EC2 Key-Pair
	public static final String EC2KeyPair_GTen = "";

	// S3 Input Bucket
	public static final String AWSS3InputBucket_GTen = "cc-group10-input-queue-v198";

	// S3 Output Bucket
	public static final String AWSS3OutputBucket_GTen = "cc-group10-output-queue-v198";

	// SQS Input Queue
	public static final String AWSSQSInputQueue_GTen = "incoming-queue-v2.fifo";

	// SQS Output Queue
	public static final String AWSSQSOutputQueue_GTen = "outgoing-queue-v2.fifo";

	public static final String Delimeter_GTen = "##";

	public static final String EC2InstanceType_GTen = "t2.micro";

	public static final Regions AWSRegion_GTen = Regions.US_EAST_1;

	public static final String TotalMsgInQueue_GTen = "ApproximateNumberOfMessages";

	public static final String TotalMsgInSQSNotVisible_GTen = "ApproximateNumberOfMessagesNotVisible";

	public static final Integer MaximumNumberOfAppInstances_GTen = 19;

	public static final List<String> SQSMetrics_GTen = new ArrayList<String>(Arrays.asList(TotalMsgInQueue_GTen, TotalMsgInSQSNotVisible_GTen));

	public static final String EC2TagKey_GTen = "Name";

	public static final String EC2TagValue_GTen = "App Instance";

	public static final String ResourceInstance_GTen = "instance";

	public static final String AppTierData_GTen = "#!/bin/bash" + "\n" + "cd /home/ec2-user" + "\n"
			+ "chmod +x awscloudgroupten-iraasproject-apptier-0.0.1-SNAPSHOT.jar" + "\n"
			+ "java -jar awscloudgroupten-iraasproject-apptier-0.0.1-SNAPSHOT.jar";

	public static final Integer MaxWaitTimeOut_GTen = 20;

}

