package com.awscloudgroupten.iraasproject.awscloudgroupteniraasprojectapptier.utility;

import com.amazonaws.services.sqs.model.Message;

/**
 * Declaration of the main services the APP tier is going to provide
 */
public interface AppService {

    // Method to create queue
    void createAWSQueue(String queue);

    // Method to add message to the queue
    void addMessageToQueue(String queue, String msg);

    // Method to remove a message from the queue
    void removeMessageFromQueue(String queue, Message msg);

    // Terminate the EC2 instance
    void terminateEC2Instance();

    // Method to get number of messages from the queue
    int getQuantityOfMessagesInQueue(String queue);

    // Get a message from the queue
    Message getMsg(String queue);

    // Get and process a message from queue
    void processMessage();
}
