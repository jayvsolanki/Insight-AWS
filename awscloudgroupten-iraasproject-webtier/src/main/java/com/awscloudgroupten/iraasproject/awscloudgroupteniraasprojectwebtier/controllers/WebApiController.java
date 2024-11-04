package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.controllers;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.WebAppConstant;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.IAwsConfigService;
import com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.utility.IGeneralService;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Vinayak Kalunge | CSE 546 - Group 10
 */
@RestController
public class WebApiController {
	
    @Autowired
    private IGeneralService iGeneralService;

    @Autowired
    private IAwsConfigService iAwsConfigService;

	/**
	 * API will run on Web Tier and take Images as an Input, will process image and give classification result as an output.
	 * @param multipartFiles
	 * @return Map Image Classification Result to API
	 * Reference: [https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html]
	 */
	@PostMapping("/getImageClassificationResult")
	public String accessImageLink(@RequestParam(name = "myfile", required = true) MultipartFile[] multipartFiles){
		Set<String> uniqueList = processImage(multipartFiles);
		iAwsConfigService.generateQueue(WebAppConstant.AWSSQSOutputQueue_GTen);
		Map<String,String> resultMap = new TreeMap<>();
		for(String imageFileName : uniqueList) {
			String[] response = iGeneralService.getResult(imageFileName);
			resultMap.put(response[0],response[1]);
		}

		StringBuilder mapAsString = new StringBuilder();
		for (String key : resultMap.keySet()) {
			mapAsString.append(resultMap.get(key));
		}

		return mapAsString.toString();
	}

	/**
	 * Method will process Image put the images into
	 * @param multipartFiles
	 * @return Set Value of Images
	 */
	private Set<String> processImage(MultipartFile[] multipartFiles){

		Set<String> list = new HashSet<>();

		for(MultipartFile multipartFile : multipartFiles) {
			String s = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");
			System.out.println("INFO - Input File: "+s);

			// Put Image in AWS S3
			iAwsConfigService.putImageS3(multipartFile,s);
			System.out.println("INFO - "+s+" Uploaded to S3");

			// Enqueue Image in AWS SQS
			iAwsConfigService.sqsInput(s, WebAppConstant.AWSSQSInputQueue_GTen, 0);
			System.out.println("INFO - "+s+" Enqueued in SQS");

			// Add Image to set
			list.add(s);
		}

		return list;
	}

	/*
	* Request: GET
	 */
	@GetMapping("/")
	public String getHome() {
		return "Upload Image to /getImageClassificationResult";
	}
}
