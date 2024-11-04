package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.db;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.GlobalConstant;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.handle.AWSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Abhishek Bakare
 */
@Repository
public class AWSS3HandlerImpl implements AWSS3Handler {

    @Autowired
    private AWSConfig awsConfig;

    // Logger to log class stuff to find if something is bad.
    private static final Logger logger = LoggerFactory.getLogger(AWSS3HandlerImpl.class);

    /**
     * Put the object in S3 output bucket
     *
     * @param imageName
     * @param classificationResult
     */
    @Override
    public void putInDB(String imageName, String classificationResult) {
        createBucketIfNotExists();
        putObjInBucket(imageName, classificationResult);
    }

    /**
     * Put the object in S3 output bucket
     *
     * @param imageName
     * @param classificationResult
     */
    private void putObjInBucket(String imageName, String classificationResult) {
        logger.info("Inserting in S3 OP " + imageName + " -> " + classificationResult);
        byte[] resultInBytes = classificationResult.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream stuff = new ByteArrayInputStream(resultInBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(resultInBytes.length);

        awsConfig.getS3()
                .putObject(new PutObjectRequest(GlobalConstant.AWSS3OutputBucket_GTen, imageName, stuff, metadata));
        logger.info("Successfully inserted in S3 OP " + imageName + " -> " + classificationResult);
    }

    /**
     * Create the output bucket if not exists
     */
    private void createBucketIfNotExists() {
        if (isBucketExists(GlobalConstant.AWSS3OutputBucket_GTen))   return;
        logger.info("Creating bucket: " + GlobalConstant.AWSS3OutputBucket_GTen);
        awsConfig.getS3().createBucket(GlobalConstant.AWSS3OutputBucket_GTen);
    }

    /**
     * Check if Bucket exist in the AWS Cluster
     *
     * @param bucket
     * @return
     */
    public boolean isBucketExists(String bucket) {
        return awsConfig.getS3().doesBucketExistV2(bucket);
    }
}
