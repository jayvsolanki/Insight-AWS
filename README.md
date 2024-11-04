# Title: Auto scalable face recognition system based on AWS IAAS.


### AWS Resources
* Web Tier url: http://#EC2 Instance public ip#:8080/getImageClassificationResult
* SQS Input Queue name: incoming-queue-v2.fifo
* SQS Output Queue name: outgoing-queue-v2.fifo
* S3 Input bucket name: cc-group10-input-queue-v198
* S3 Output bucket name: cc-group10-output-queue-v198

### Steps to run the project
* Clone the code base with the following command
git clone the project.
* Import the project in your preferred IDE like eclipse.
* In GlobalConstant.java and WebAppConstant update the variables AWSAccessKeyId_GTen, AWSSecretKey_GTen and EC2KeyPair_GTen with the AWS account Access Key, Secret Key and KeyPair respectively.
* Change directory(cd) to "awscloudgroupten-iraasproject-apptier" dir (APP TIER).
* Run the following command to build the APP TIER 
  * mvn clean && mvn build
* Now, move to the AWS web console and create an AMI from ami-0a55e620aa5c79a24.
* Copy the APP TIER JAR to this newly created machine using the following command format
  * scp -i #Key Pair path# #APP TIER JAR PATH# ec2-user@#AWS Machine Public IPv4 DNS#:~
* Perform SSH to this machine using following command
  * ssh -i #Key Pair path# ec2-user@#AWS Machine Public IPv4 DNS#
* Verify if APP TIER JAR is present in the machine. Then give permission to this jar using the following command
  * chmod +x #PATH TO APP TIER JAR#
* Now install JAVA 1.8 on this machine using the following command and then exit the machine 
  * yum list java* sudo yum install java-1.8.0
* Create a new AMI of this machine.
* Copy the AMI ID of this machine to AmazonMachineImageId_GTen variable in WebAppConstant.java and now perform the command at the 'awscloudgroupten-iraasproject-webtier' dir (WEB TIER).
  * mvn clean && mvn build
* Now, Copy the WEB TIER JAR to the above newly created machine using the following command format
  * scp -i #Key Pair path# #WEB TIER JAR PATH# ec2-user@#AWS Machine Public IPv4 DNS#:~
* Create a security group with inbound rules with Type as "TCP",  port range 0-65535, and source as "custom" with 0.0.0.0/0 rule. Add this security group to the created machine so that it is open for HTTP requests.
* Perform SSH to this machine as shown in step 8 and start the server using the following command
  * java -jar awscloudgroupten-iraasproject-webtier-0.0.1-SNAPSHOT.jar
