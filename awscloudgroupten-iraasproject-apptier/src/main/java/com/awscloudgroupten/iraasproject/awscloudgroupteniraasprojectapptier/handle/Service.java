package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.handle;

import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.db.AWSS3Handler;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.db.AWSS3HandlerImpl;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.utility.AppService;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.utility.AppServiceImpl;
import org.springframework.context.annotation.Bean;

/**
 * @author Abhishek Bakare
 * This is the class providing various listed MBeans for SpringBoot run.
 */
public class Service {

    /**
     * MBean service for AWS App tier functionalities
     *
     * @return
     */
    @Bean
    public AppService getAWSService() {
        return new AppServiceImpl();
    }

    /**
     * MBean service for AWS configs
     *
     * @return
     */
    @Bean
    public AWSConfig getAWSConfig() {
        return new AWSConfig();
    }

    /**
     * MBean service for AWS S3
     *
     * @return
     */
    @Bean
    public AWSS3Handler getS3Instance() {
        return new AWSS3HandlerImpl();
    }
}
