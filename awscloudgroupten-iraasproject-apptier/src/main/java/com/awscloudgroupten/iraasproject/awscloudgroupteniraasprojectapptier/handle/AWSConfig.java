package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.handle;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.GlobalConstant;
import org.springframework.context.annotation.Configuration;

/**
 * @author Abhishek Bakare
 */
@Configuration
public class AWSConfig {

    /**
     * This service gets you AWS creds
     *
     * @return
     */
    private BasicAWSCredentials getAWSCred() {
        return new BasicAWSCredentials(GlobalConstant.AWSAccessKeyId_GTen, GlobalConstant.AWSSecretKey_GTen);
    }

    /**
     * Service gets you a new EC2 instance
     *
     * @return
     */
    public AmazonEC2 getEC2() {
        return AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getAWSCred()))
                .withRegion(GlobalConstant.AWSRegion_GTen).build();
    }

    /**
     * Service gets you a new SQS queue
     *
     * @return
     */
    public AmazonSQS getSQS() {
        return AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getAWSCred()))
                .withRegion(GlobalConstant.AWSRegion_GTen).build();
    }

    /**
     * Service gets you a new S3 bucket instance
     *
     * @return
     */
    public AmazonS3 getS3() {
        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getAWSCred()))
                .withRegion(GlobalConstant.AWSRegion_GTen).build();
    }

}
