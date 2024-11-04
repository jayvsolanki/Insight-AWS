package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier;

import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.handle.WebAppConfiguration;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.IAwsConfigService;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.IGeneralService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Vinayak Kalunge | CSE 546 Group 10
 */
@SpringBootApplication
@EnableAutoConfiguration
public class AwscloudgrouptenIraasprojectWebtierApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwscloudgrouptenIraasprojectWebtierApplication.class, args);

		AnnotationConfigApplicationContext application = new AnnotationConfigApplicationContext(WebAppConfiguration.class);
		IAwsConfigService iAwsConfigService = application.getBean(IAwsConfigService.class);
		IGeneralService iGeneralService = application.getBean(IGeneralService.class);

		System.out.println("INFO - AWS Services Initializing");
		iAwsConfigService.initializeAwsServices(WebAppConstant.AWSSQSInputQueue_GTen, WebAppConstant.AWSSQSOutputQueue_GTen,
				WebAppConstant.AWSS3InputBucket_GTen, WebAppConstant.AWSS3OutputBucket_GTen);

		Thread thread1 = new Thread((Runnable) iAwsConfigService);
		System.out.println("INFO - Starting AWS Configuration Service on Thread 1");
		thread1.start();
		Thread thread2 = new Thread((Runnable) iGeneralService);
		System.out.println("INFO - Starting General Services on Thread 2");
		thread2.start();
		System.out.println("INFO - Processed");
		application.close();
	}
}