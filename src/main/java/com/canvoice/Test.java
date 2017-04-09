package com.canvoice;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * @author mthwate
 */
public class Test {

	public static void main(String[] args) {

		String id = "abc123";

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration("https://sns.us-east-1.amazonaws.com", "us-east-1"))
				.build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("Users");

		Item item = table.getItem(new PrimaryKey().addComponent("UserId", id));

		if (item == null) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Enter token.");
			SimpleCard card = new SimpleCard();
			card.setContent(id);
			System.out.println("A");
		} else {
			System.out.println("B");
		}
	}

}
