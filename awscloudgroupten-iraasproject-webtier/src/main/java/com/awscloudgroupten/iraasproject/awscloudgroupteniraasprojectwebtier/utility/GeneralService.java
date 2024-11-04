package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.WebAppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.sqs.model.Message;

/**
 * @author Vinayak Kalunge | CSE 546 Group 10
 */
@Service
public class GeneralService implements IGeneralService, Runnable {

	@Autowired
	private IAwsConfigService iAwsConfigService;

	@Override
	public void run() {
		this.addOutputToMap();
	}

	@Override
	public void addOutputToMap() {
		while (true) {
			List<Message> list = null;
			try {
				list = iAwsConfigService.getMessage(WebAppConstant.AWSSQSOutputQueue_GTen, 20, WebAppConstant.MaxWaitTimeOut_GTen, 10);
				if (list != null) {
					try {
						for (Message message : list) {
							addMessageToList(message);
						}
					} catch (Exception e) {
						System.out.println("EXCEPTION - Mapping the Output Queue Messages: "+e.getMessage());
					}
				}
			} catch (Exception e) {
				System.out.println("EXCEPTION - No Messages: " + e.getMessage());

				System.out.println("INFO - Thread Sleeping for 10 seconds");
				try {
					Thread.sleep(10000);
				} catch (Exception p) {
					System.out.println("EXCEPTION - In Thread Sleep");
				}
			}
		}
	}

	private void addMessageToList(Message message){
		String[] classificationResult = null;
		classificationResult = message.getBody().split(WebAppConstant.Delimeter_GTen);
		result.put(classificationResult[0], classificationResult[1]);
		iAwsConfigService.removeMessage(message, WebAppConstant.AWSSQSOutputQueue_GTen);
	}
	
	@Override
	public String[] getResult(String s) {
		String[] queue = iAwsConfigService.accessResponseQueue(s);
		return queue;
	}

	public File changeFileFormat(MultipartFile multipartFile) {
		try {
			File f = new File(multipartFile.getOriginalFilename());
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(multipartFile.getBytes());
			fos.close();
			return f;
		} catch (Exception e) {
			return null;
		}
	}

}
