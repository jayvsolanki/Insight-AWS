package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectwebtier.db;

import java.io.File;

/**
 * @author Kenil Limbani | CSE 546 - Group 10
 */
public interface IAwsRepository {

    void putFileOnInstance(File f, String fName);

}
