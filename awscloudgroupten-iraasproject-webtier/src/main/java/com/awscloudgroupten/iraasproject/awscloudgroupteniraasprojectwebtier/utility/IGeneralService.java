package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Vinayak Kalunge | CSE 546 Group 10
 */
public interface IGeneralService {
	Map<String, String> result = new HashMap<>();
	
    String[] getResult(String s);

    File changeFileFormat(MultipartFile multipartFile);
    
    void addOutputToMap();
}
