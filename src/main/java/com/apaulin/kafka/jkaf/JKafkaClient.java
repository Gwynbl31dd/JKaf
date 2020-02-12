/**
 * 
 */
package com.apaulin.kafka.jkaf;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @author Anthony Paulin
 * @version 0.1
 * @since 12/02/2020
 * 
 */
public class JKafkaClient {
	//TODO https://stackoverflow.com/questions/43027146/how-to-send-message-to-kafka-topic-protected-with-ssl
	/**
	 * Consumer
	 * @param server Server address host:port
	 * @param groupId Group ID
	 * @param commitInterval Commit interval
	 * @param topics Topics list separated by a comma
	 */
	public static void consume(String server, String groupId, int commitInterval,ArrayList<String> topics) {
		if (server == null) {
			System.out.println("server (-s) cannot be null");
		}
		if (groupId == null) {
			System.out.println("Groupe id (-g) cannot be null");
		}
		if (topics == null) {
			System.out.println("Topics (-t) cannot be null");
		}
		Properties props = new Properties();
		props.put("bootstrap.servers", server);
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", commitInterval);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		System.out.println("[+] Done");
		@SuppressWarnings("resource")
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		System.out.println("[+] Start reading data...");
		System.out.println("[+] CTRL + C to exit");
		while (true) {
			@SuppressWarnings("deprecation")
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("***");
				System.out.println("offset = " + record.offset() + ", key = " + record.key());
				System.out.println(record.value());
			}
		}
	}
	
	/**
	 * Producer
	 * @param server Server address host:port
	 * @param topics Topics list separated by a comma
	 * @param key Message's key
	 * @param value Message's value
	 */
	public static void produce(String server, ArrayList<String> topics,String key,String value) {
		 Properties props = new Properties();
		 props.put("bootstrap.servers",server);
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 Producer<String, String> producer = new KafkaProducer<>(props);
		 for (int i = 0; i < topics.size(); i++) {
			 producer.send(new ProducerRecord<String, String>(topics.get(i), key,value));
		 }
		 producer.close();
	}
}
