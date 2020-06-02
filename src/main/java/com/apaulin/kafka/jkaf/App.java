package com.apaulin.kafka.jkaf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.apaulin.kafka.jkaf.util.InstallCert;

/**
 * JKaf main class
 * 
 * @author Anthony Paulin
 * @since 12/02/2020
 * @version 0.2
 */
public class App {
	private static boolean help = false;
	private static boolean consume = false;
	private static boolean produce = false;
	private static boolean sslEnc = false;
	private static boolean sslAuth = false;
	private static boolean install = false;
	private static boolean earliest = false;
	private static String server = null;
	private static String groupId = null;
	private static int commitInterval = 1000;
	private static ArrayList<String> topics = null;
	private static String key = null;
	private static String value = null;
	private static String thruststore = null;
	private static String thruststorePassword = "";
	private static String keystore = null;
	private static String keystorePassword = "";
	private static String password = null;
	private static int numberMessages = -1;

	public static boolean header = false;
	public static ArrayList<HeaderCli> headers = new ArrayList<HeaderCli>();

	/**
	 * Main method
	 * 
	 * @param args
	 *            List of options/values
	 */
	public static void main(String[] args) {
		try {
			readArgs(args);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("[-] Wrong arguments...");
			displayHelp();
		}
		if (help == true) {
			displayHelp();
		} else if (install == true) {
			try {
				// rebuild the arg
				String[] args2 = new String[args.length - 1];
				for (int i = 0; i < args.length - 1; i++) {
					args2[i] = args[i + 1];
				}
				InstallCert.Install(args2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (consume == true) {
			System.out.println("[+] Consumer mode...");
			try {
				if (sslAuth == true) {
					JKafkaClient.consume(server, groupId, commitInterval, topics, thruststore, thruststorePassword,
							keystore, keystorePassword, password, numberMessages,earliest);
				} else if (sslEnc == true) {
					JKafkaClient.consume(server, groupId, commitInterval, topics, thruststore, thruststorePassword,
							keystore, keystorePassword, numberMessages,earliest);
				} else {
					JKafkaClient.consume(server, groupId, commitInterval, topics, numberMessages,earliest);
				}
			} catch (NullPointerException e) {
				System.out.println("[-] Wrong arguments...");
				displayHelp();
			}
		} else if (produce == true) {
			System.out.println("[+] Producer mode...");
			if (sslAuth == true) {
				JKafkaClient.produce(server, topics, key, value, thruststore, thruststorePassword, keystore,
						keystorePassword, password);
			} else if (sslEnc == true) {
				JKafkaClient.produce(server, topics, key, value, thruststore, thruststorePassword, keystore,
						keystorePassword);
			} else {
				JKafkaClient.produce(server, topics, key, value);
			}
		} else {
			displayHelp();
		}
	}

	/**
	 * Read the arguments
	 * 
	 * @param args
	 *            List of arguments as a table
	 */
	private static void readArgs(String[] args) {
		if (args[0].compareTo("-c") == 0) {
			consume = true;// Consumer mode
		} else if (args[0].compareTo("-p") == 0) {
			produce = true;// Produce mode
		} else if (args[0].compareTo("-h") == 0 || args[0].compareTo("--help") == 0) {
			help = true;// Help mode
		} else if (args[0].compareTo("-i") == 0 || args[0].compareTo("--install") == 0) {
			install = true;
		} else {
			help = true;// Help mode
		}
		// If no help mode, gets the data
		if (!help) {
			getArgs(args);
		}
	}

	/**
	 * Get the list of options/values and build the access
	 * 
	 * @param args
	 */
	private static void getArgs(String[] args) {
		for (int i = 1; i < args.length; i++) {
			if (args[i].compareTo("-s") == 0 || args[i].compareTo("--server") == 0) {
				server = args[++i];
			} else if (args[i].compareTo("-g") == 0 || args[i].compareTo("--group-id") == 0) {
				groupId = args[++i];
			} else if (args[i].compareTo("-C") == 0 || args[i].compareTo("--commit-interval") == 0) {
				commitInterval = Integer.valueOf(args[++i]);
			} else if (args[i].compareTo("-t") == 0 || args[i].compareTo("--topics") == 0) {
				// Split the list
				topics = new ArrayList<String>();
				String[] list = args[++i].split(",");
				for (int ind = 0; ind < list.length; ind++) {
					topics.add(list[ind]);
				}
			} else if (args[i].compareTo("-m") == 0 || args[i].compareTo("--message") == 0) {
				value = args[++i];
			} else if (args[i].compareTo("-k") == 0 || args[i].compareTo("--key") == 0) {
				key = args[++i];
			} else if (args[i].compareTo("-f") == 0 || args[i].compareTo("--file") == 0) {
				try {
					value = getData(args[++i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (args[i].compareTo("-T") == 0 || args[i].compareTo("--truststore") == 0) {
				sslEnc = true;
				thruststore = args[++i];
			} else if (args[i].compareTo("-tp") == 0 || args[i].compareTo("--truststore-password") == 0) {
				thruststorePassword = args[++i];
			} else if (args[i].compareTo("-K") == 0 || args[i].compareTo("--keystore") == 0) {
				keystore = args[++i];
			} else if (args[i].compareTo("-kp") == 0 || args[i].compareTo("--keystore-password") == 0) {
				keystorePassword = args[++i];
			} else if (args[i].compareTo("-a") == 0 || args[i].compareTo("--authentication") == 0) {
				sslAuth = true;
				password = args[++i];
			}
			else if (args[i].compareTo("-x") == 0 || args[i].compareTo("--number") == 0) {
				try {
					numberMessages = Integer.valueOf(args[++i]);
				}
				catch(Exception e) {
					System.out.println("[-] -x must be an integer...");
				}
			}
			else if (args[i].compareTo("-e") == 0 || args[i].compareTo("--earliest") == 0) {
				earliest = true;
			}
			else if(args[i].compareTo("-H") == 0 || args[i].compareTo("--header") == 0) {
				header = true;
				String[] pair =  args[++i].split(",");
				for(int ind=0;ind< pair.length;ind++) {
					String[] toSplit = pair[ind].split(":");
					headers.add(new HeaderCli(toSplit[0],toSplit[1]));
				}
			}
		}
	}

	/**
	 * Display the help message
	 */
	private static void displayHelp() {
		System.out.println("*************** JKAF v0.3.0");
		System.out.println("Kafka client CLI");
		System.out.println("*************** USAGE");
		System.out.println("java -jar jkaf-0.3.0.jar <mode> [<option> <arg>] ");
		System.out.println("*************** MODES");
		System.out.println("-c,--consume : Consumer mode.");
		System.out.println("-p,--produce : Producer mode.");
		System.out.println("*************** OPTIONS");
		System.out.println("-h,--help : Display this menu.");
		System.out.println("-s,--server : Bootstrap server address. E.g -s 127.0.0.1:9027");
		System.out.println("-g,--group-id : Group ID. E.g -g mygrp");
		System.out.println("-C,--commit-interval : Commit interval.(optional) E.g -C 1000");
		System.out.println("-t,--topics : topic list. E.g -t topic1,topic2,topic3");
		System.out.println("-k,--key : key associate with your message.(optional) E.g -k key1");
		System.out.println("-m,--message : Value of your message. E.g -m \"{\\\"test\\\":\\\"test\\\"}\"");
		System.out.println("-f,--file : Use a file instead. E.g -f /tmp/myfile.json");
		System.out.println("-x,--number : For the consumer only, stop after X message. E.g -x 5");
		System.out.println("-e,--earliest : Read from the earliest message not committed");
		System.out.println("-H,--header : Set a custom header. E.g -H key1:value2,key2:value2");
		System.out.println("*************** SECURITY OPTIONS");
		System.out.println("-T,--truststore : truststore location.");
		System.out.println("-tp,--truststore-password : Use SSL encryption. E.g -P mypassword1234");
		System.out.println("-K,--keystore : keystore location.");
		System.out.println("-kp,--keystore-password : keystore password.");
		System.out.println("-a,--authentication : Use SSL password authentication. E.g -a mysslpasword ");
		System.out.println("*************** UTILITIES");
		System.out.println("-i,--install : Install a certificate .Usage: <host>[:port] [passphrase] ");
		System.out.println("*************** EXAMPLE");
		System.out.println("*** Consumer");
		System.out.println("java -jar jkaf-0.0.1.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1 ");
		System.out.println("java -jar jkaf-0.0.1.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1,topic2 -C 2000 ");
		System.out.println("*** Producer");
		System.out.println("java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -m MyMessage -k key1");
		System.out.println("java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -m MyMessage");
		System.out.println("java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -f test.txt -k key1");
		System.out.println("*************** MAINTAINER");
		System.out.println("Anthony Paulin <paulin.anthony@gmail.com>");
	}

	/**
	 * Read the file
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static String getData(String path) throws FileNotFoundException, IOException {
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(path));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null) {
			sb.append(line.trim());
			line = br.readLine();
		}
		return sb.toString();
	}
}
