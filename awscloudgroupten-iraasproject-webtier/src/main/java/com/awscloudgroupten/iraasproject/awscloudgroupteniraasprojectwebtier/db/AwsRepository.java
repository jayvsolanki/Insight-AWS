package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.db;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.handle.AwsGeneralConfiguration;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.WebAppConstant;
import java.io.File;

/**
 * @author Kenil Limbani | CSE 546 - Group 10
 */
@Repository
public class AwsRepository implements IAwsRepository {

    @Autowired
    private AwsGeneralConfiguration awsGeneralConfiguration;

    /**
     * Method to put image files on S3 Buckets
     * @param f
     * @param fName
     * Reference: [https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3Client.html]
     */
    public void putFileOnInstance(File f, String fName) {
        try {
            if (!checkInstanceExists())
                awsGeneralConfiguration.accessS3Bucket().createBucket(WebAppConstant.AWSS3InputBucket_GTen);

            awsGeneralConfiguration.accessS3Bucket().putObject(new PutObjectRequest(WebAppConstant.AWSS3InputBucket_GTen, fName, f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkInstanceExists() throws Exception {
        try{
            return awsGeneralConfiguration.accessS3Bucket().doesBucketExistV2(WebAppConstant.AWSS3InputBucket_GTen);
        }
        catch (Exception e){
            throw e;
        }
    }
}
