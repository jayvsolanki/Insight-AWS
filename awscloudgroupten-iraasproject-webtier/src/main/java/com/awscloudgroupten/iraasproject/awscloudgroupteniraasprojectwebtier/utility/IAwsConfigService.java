package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.sqs.model.Message;


/**
 * @author Kenil Limbani | CSE 546 - Group 10
 */

public interface IAwsConfigService {

	void initializeAndStartInstance(String id, String instType, Integer min, Integer max);

	void sqsInput(String s, String sqsName, int time);
	
	List<Message> getMessage(String s, Integer timeout, Integer wait, Integer numMsg);

	String[] accessResponseQueue(String s);

	void generateQueue(String s);

	void putImageS3(MultipartFile multipartFiles, String s);

	void scaleOutInstances();

	Integer countNumberOfMessagesFromQueue(String s);

	Integer countNumberOfInstances();

	void removeMessage(Message m, String s);

	void initializeAwsServices(String iq, String oq, String ib,String ob);
	
	void removeBucket(String s);
}
