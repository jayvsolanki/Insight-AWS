package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.db;

/**
 * @author Abhishek Bakare
 */
public interface AWSS3Handler {

    /**
     * This is a definition function which puts stuff in s3 buckets.
     *
     * @param imageName
     * @param classificationResult
     */
    void putInDB(String imageName, String classificationResult);

}