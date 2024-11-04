package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.handle;

import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.db.IAwsRepository;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.db.AwsRepository;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.IAwsConfigService;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.AwsConfigService;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.IGeneralService;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.GeneralService;
import org.springframework.context.annotation.Bean;

/**
 * @author Vinayak Kalunge | CSE 546 Group 10
 */
public class WebAppConfiguration {

    @Bean
    public AwsGeneralConfiguration accessAwsConfig() {
        return new AwsGeneralConfiguration();
    }

    @Bean
    public IAwsConfigService accessAwsService() {
        return new AwsConfigService();
    }

    @Bean
    public IAwsRepository accessAwsRepository() {
        return new AwsRepository();
    }

    @Bean
    public IGeneralService accessGeneralService() {
        return new GeneralService();
    }

}
