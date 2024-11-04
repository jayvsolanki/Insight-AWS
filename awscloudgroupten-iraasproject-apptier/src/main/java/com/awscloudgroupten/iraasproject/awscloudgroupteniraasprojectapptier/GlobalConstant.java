package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier;

import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Abhishek Bakare
 * This class holds all the constants needed for the APP-TIER.
 */
public class GlobalConstant {

    // Access key for the AWS EC2 Instances
    public static final String AWSAccessKeyId_GTen = "";

    // Secret key for the AWS EC2 Instances
    public static final String AWSSecretKey_GTen = "";

    // INPUT queue (sqs fifo)
    public static final String AWSSQSInputQueue_GTen = "incoming-queue-v2.fifo";

    // Output queue (sqs fifo)
    public static final String AWSSQSOutputQueue_GTen = "outgoing-queue-v2.fifo";

    // INPUT S3 bucket
    public static final String AWSS3InputBucket_GTen = "cc-group10-input-queue-v198";

    // OUTPUT S3 bucket
    public static final String AWSS3OutputBucket_GTen = "cc-group10-output-queue-v198";

    // File path to the code base
    public static final String FilePath_GTen = "/home/ec2-user/";

    // Python version in use
    public static final String PythonVersion_GTen = "python3";

    // Face classification file
    public static final String ClassifierPythonFile_GTen = "face_recognition.py";

    // Delimiter in image file name and its classification
    public static final String Delimeter_GTen = "##";

    // AWS Region
    public static final Regions AWSRegion_GTen = Regions.US_EAST_1;

    // AWS SQS Queue visibility time out
    public static final Integer AWSSQSVisibilityTimeout_GTen = 40;

    // AWS Wait
    public static final Integer AWSWait_GTen = 20;

    // AWS service max instance(app tier) to spawn
    public static final int MaximumNumberOfAppInstances_GTen = 19;

    // If any error in classification the following string is returned as default
    public static final String ClassificationError_GTen = "ERROR in classification!";

    // SQS Message
    public static final String AWSSQSMsg_GTen = "ApproximateNumberOfMessages";

    public static final List<String> AWSSQSList_GTen = new ArrayList<String>(Arrays.asList(AWSSQSMsg_GTen));

    // MAX Thread to spawn in the app-tier
    public static final int MaxNumberOfThreadAccepted_GTen = 20;

    // MAX Thread to spawn in the app-tier
    public static final int MaxThreads_GTen = 250;
}
