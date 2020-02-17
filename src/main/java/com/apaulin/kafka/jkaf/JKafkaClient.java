package com.apaulin.kafka.jkaf;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * @author Anthony Paulin
 * @version 0.2
 * @since 12/02/2020
 */
public class JKafkaClient {

	private static Properties props = new Properties();

	/**
	 * consume with a keystore
	 * 
	 * @param server
	 *            Server address host:port
	 * @param groupId
	 *            Group ID
	 * @param commitInterval
	 *            Commit interval
	 * @param topics
	 *            Topics list separated by a comma
	 * @param keystoreLocation
	 *            Keystore location (Path)
	 * @param keystorePassword
	 *            Password of the keystore
	 */
	public static void consume(String server, String groupId, int commitInterval, ArrayList<String> topics,
			String trustoreLocation, String trustorePassword, String keystoreLocation, String keystorePassword) {
		setSslEncryption(trustoreLocation, trustorePassword);
		setKeyStore(keystoreLocation, keystorePassword);
		consume(server, groupId, commitInterval, topics);
	}

	/**
	 * consume with a keystore and a password authentication
	 * 
	 * @param server
	 * @param groupId
	 * @param commitInterval
	 * @param topics
	 * @param keystoreLocation
	 * @param keystorePassword
	 * @param keyPasswordConfig
	 */
	public static void consume(String server, String groupId, int commitInterval, ArrayList<String> topics,
			String truststoreLocation, String trustorePassword, String keystoreLocation, String keystorePassword,
			String keyPasswordConfig) {
		setSslEncryption(truststoreLocation, trustorePassword);
		setKeyStore(keystoreLocation, keystorePassword);
		setSslAuth(keyPasswordConfig);
		consume(server, groupId, commitInterval, topics);
	}

	/**
	 * Consumer
	 * 
	 * @param server
	 *            Server address host:port
	 * @param groupId
	 *            Group ID
	 * @param commitInterval
	 *            Commit interval
	 * @param topics
	 *            Topics list separated by a comma
	 */
	public static void consume(String server, String groupId, int commitInterval, ArrayList<String> topics) {
		if (server == null) {
			System.out.println("server (-s) cannot be null");
		}
		if (groupId == null) {
			System.out.println("Groupe id (-g) cannot be null");
		}
		if (topics == null) {
			System.out.println("Topics (-t) cannot be null");
		}
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
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("***");
				System.out.println("Receive message at " + getDate() + " From " + record.topic());
				System.out.println("offset = " + record.offset() + ", key = " + record.key());
				System.out.println(record.value());
			}
		}
	}

	/**
	 * Produce with SSL with a auth password
	 * 
	 * @param server
	 *            Server address host:port
	 * @param topics
	 *            Topic list separated by comma
	 * @param key
	 *            Key of the message
	 * @param value
	 *            Value of the message
	 * @param truststoreLocation
	 *            Path of the Java trustore
	 * @param trustorePassword
	 *            Password of the trustore
	 * @param keystoreLocation
	 *            Path of the Java keystore
	 * @param keystorePassword
	 *            Password of the keystore
	 * @param keyPasswordConfig
	 *            Password config
	 */
	public static void produce(String server, ArrayList<String> topics, String key, String value,
			String truststoreLocation, String trustorePassword, String keystoreLocation, String keystorePassword,
			String keyPasswordConfig) {
		setSslEncryption(truststoreLocation, trustorePassword);
		setKeyStore(keystoreLocation, keystorePassword);
		setSslAuth(keyPasswordConfig);
		produce(server, topics, key, value);
	}

	/**
	 * Produce with SSL
	 * 
	 * @param server
	 *            Server address host:port
	 * @param topics
	 *            Topic list separated by comma
	 * @param key
	 *            Key of the message
	 * @param value
	 *            Value of the message
	 * @param truststoreLocation
	 *            Path of the Java trustore
	 * @param trustorePassword
	 *            Password of the trustore
	 * @param keystoreLocation
	 *            Path of the Java keystore
	 * @param keystorePassword
	 *            Password of the keystore
	 */
	public static void produce(String server, ArrayList<String> topics, String key, String value,
			String trustoreLocation, String trustorePassword, String keystoreLocation, String keystorePassword) {
		setSslEncryption(trustoreLocation, trustorePassword);
		setKeyStore(keystoreLocation, keystorePassword);
		produce(server, topics, key, value);
	}

	/**
	 * Producer
	 * 
	 * @param server
	 *            Server address host:port
	 * @param topics
	 *            Topics list separated by a comma
	 * @param key
	 *            Message's key
	 * @param value
	 *            Message's value
	 */
	public static void produce(String server, ArrayList<String> topics, String key, String value) {
		props.put("bootstrap.servers", server);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		Producer<String, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < topics.size(); i++) {
			System.out.println("[+] Producing to topic " + topics.get(i));
			producer.send(new ProducerRecord<String, String>(topics.get(i), key, value));
			System.out.println("[+] Done");
		}
		producer.close();
		System.out.println("[+] Producer closed");
	}

	/**
	 * Set the SSL encryption
	 * 
	 * @param keystoreLocation
	 * @param keystorePassword
	 */
	private static void setSslEncryption(String truststoreLocation, String truststorePassword) {
		// configure the following three settings for SSL Encryption
		System.out.println("[+] Set SSL encryption...");
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
		System.out.println("[+] Set SSL Trustore...");
		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
	}

	/**
	 * Set the keystore properties
	 * 
	 * @param keystoreLocation Path to the keystore
	 * @param keystorePassword Password of the keystore
	 */
	private static void setKeyStore(String keystoreLocation, String keystorePassword) {
		System.out.println("[+] Set SSL KeyStore...");
		props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
		props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
	}

	/**
	 * Set the SSL authentication
	 * 
	 * @param keyPasswordConfig
	 */
	private static void setSslAuth(String keyPasswordConfig) {
		System.out.println("[+] Set SSL Authentication...");
		// configure the following three settings for SSL Authentication
		props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPasswordConfig);
	}

	/**
	 * Get the date
	 * 
	 * @return
	 */
	private static String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
}
