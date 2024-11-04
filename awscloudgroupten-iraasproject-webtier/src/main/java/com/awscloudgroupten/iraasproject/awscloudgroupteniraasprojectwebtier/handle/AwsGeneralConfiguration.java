package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.handle;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.WebAppConstant;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kenil Limbani | CSE 546 - Group 10
 * Reference: [https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/AWSStaticCredentialsProvider.html,
 *             https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3ClientBuilder.html,
 *             https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/AmazonEC2ClientBuilder.html,
 *             https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQSClientBuilder.html]
 */
@Configuration
public class AwsGeneralConfiguration {

    public AWSStaticCredentialsProvider getStaticCredentialProvider(){
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(WebAppConstant.AWSAccessKeyId_GTen, WebAppConstant.AWSSecretKey_GTen));
    }

    public AmazonS3 accessS3Bucket() {
        return AmazonS3ClientBuilder.standard().withCredentials(getStaticCredentialProvider()).withRegion(WebAppConstant.AWSRegion_GTen).build();
    }

    public AmazonEC2 accessEc2Instance() {
        return AmazonEC2ClientBuilder.standard().withCredentials(getStaticCredentialProvider()).withRegion(WebAppConstant.AWSRegion_GTen).build();
    }

    public AmazonSQS accessSqsQueue() {
        return AmazonSQSClientBuilder.standard().withCredentials(getStaticCredentialProvider()).withRegion(WebAppConstant.AWSRegion_GTen).build();
    }
}
