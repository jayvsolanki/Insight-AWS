package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier;

import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.handle.Service;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.utility.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Abhishek Bakare
 * This class is the main code block which starts execution of App block code on App tier instance when it starts.
 */

@SpringBootApplication
@EnableAutoConfiguration
public class AWSAppTierApplication {

	private static final Logger logger = LoggerFactory.getLogger(AWSAppTierApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AWSAppTierApplication.class, args);
		logger.trace("Running App tier");
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Service.class)) {
			AppService awsAppUtil = context.getBean(AppService.class);
			int noOfThreads = getNumberOfThreads(awsAppUtil);
			logger.info("No of threads to be spawned: " +  noOfThreads);
			spawnThreads(noOfThreads, awsAppUtil);
			logger.info("Terminating instance");
			awsAppUtil.terminateEC2Instance();
		} catch (Exception e) {
			logger.info("ISSUE occurred " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Spawning threads for image processing
	 *
	 * @param noOfThreads
	 * @param awsAppUtil
	 */
	private static void spawnThreads(int noOfThreads, AppService awsAppUtil) {
		try {
			spawn(noOfThreads, awsAppUtil);
		} catch (Exception e) {
			logger.info("Exception in spawn threads block 1");
			e.printStackTrace();
			try {
				spawn(GlobalConstant.MaxNumberOfThreadAccepted_GTen, awsAppUtil);
			} catch (Exception ee) {
				logger.info("Exception in spawn threads block 2");
				ee.printStackTrace();
				awsAppUtil.processMessage();
			}
		}
	}


	/**
	 * Actually spawning of the threads happen here
	 *
	 * @param noOfThreads
	 * @param awsAppUtil
	 * @throws InterruptedException
	 */
	private static void spawn(int noOfThreads, AppService awsAppUtil) throws InterruptedException {
		logger.info("Spawning " +  noOfThreads + " threads");
		while (noOfThreads != 0) {
			Thread util = new Thread((Runnable) awsAppUtil);
			util.start();
			util.join();
			noOfThreads--;
		}
		logger.info("Done spawning");
	}

	/**
	 * Get allowed/max number of threads to spawn without degradation of performance due to context switching
	 *
	 * @param awsAppUtil
	 * @return
	 */
	private static int getNumberOfThreads(AppService awsAppUtil) {
		int noOfMsgInQueue = awsAppUtil.getQuantityOfMessagesInQueue(GlobalConstant.AWSSQSInputQueue_GTen);
		int noOfThreads = (GlobalConstant.MaximumNumberOfAppInstances_GTen < noOfMsgInQueue ? (noOfMsgInQueue / GlobalConstant.MaximumNumberOfAppInstances_GTen) : 1);
		noOfThreads = Math.min(noOfThreads, GlobalConstant.MaxThreads_GTen);
		return noOfThreads;
	}

}
