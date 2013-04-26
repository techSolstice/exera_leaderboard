package com.exera.leaderboard;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class Leaderboard 
{
    public static void main( String[] args ) throws Exception
    {
		AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		sqs.setRegion(usWest2);
		
		
		
		System.out.println("Successfully create new SQS client and set region!");
		

		try{
			System.out.println("Creating new SQS queue!");
			CreateQueueRequest createQueueRequest = new CreateQueueRequest("FooQueue");
			String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
			
			System.out.println("Listing queues!");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println(" QueueURL: " + queueUrl);
			}
			System.out.println();
			
			//	Send a message
			System.out.println("Sending a message to queue!");
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));
			
			//	Receive messages
			System.out.println("Receiving messages from queue.");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				System.out.println("  Message");
				System.out.println("    MessageId: " + message.getMessageId());
				System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
				System.out.println("    MD5OfBody: " + message.getMD5OfBody());
				System.out.println("    Body: " + message.getBody());
				
				for (Entry<String, String> entry : message.getAttributes().entrySet()) {
					System.out.println("  Attribute");
					System.out.println("    Name:  " + entry.getKey());
					System.out.println("    Value: " + entry.getValue());
				}
			}
			System.out.println();
			
			//Delete a message
			System.out.println("Deleting a message!");
			String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
			
			//Delete a queue
			System.out.println("Deleting the queue.");
			sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
			
		} catch (AmazonServiceException ase){
			System.out.println("Caught an AmazonServiceException");
			System.out.println("Error Message: " + ase.getMessage());
		} catch (AmazonClientException ace){
			System.out.println("Caught an AmazonServiceException");
			System.out.println("Error Message: " + ace.getMessage());
		}

    }
}
