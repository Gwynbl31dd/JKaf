package com.apaulin.kafka.jkaf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JKaf main class
 * @author Anthony Paulin
 * @since 12/02/2020
 * @version 0.1
 */
public class App 
{
	private static boolean help = false;
	private static boolean consume = false;
	private static boolean produce = false;
	private static String server = null;
	private static String groupId = null;
	private static int commitInterval = 1000;
	private static ArrayList<String> topics = null;
	private static String key = null;
	private static String value = null;
	
	/**
	 * Main method
	 * @param args List of options/values
	 */
    public static void main( String[] args ) {
    	try {
    		readArgs(args);
    	}
    	catch(ArrayIndexOutOfBoundsException e) {
    		System.out.println("[-] Wrong arguments...");
    		displayHelp();
    	}
    	System.out.println(server + " " + groupId + " " + commitInterval);
    	if(help == true) {
    		displayHelp();
    	}
    	else if(consume == true) {
    		System.out.println("[+] Consumer mode...");
    		try {
    			JKafkaClient.consume(server,groupId,commitInterval,topics);
    		}
    		catch(NullPointerException e) {
    			System.out.println("[-] Wrong arguments...");
    			displayHelp();
    		}
    	}
    	else if(produce == true) {
    		System.out.println("[+] Producer mode...");
    		JKafkaClient.produce(server,topics,key,value);//TODO Implement producer
    	}
    	else {
    		displayHelp();
    	}
    }
    
    /**
     * Read the arguments
     * @param args List of arguments as a table
     */
    private static void readArgs(String[] args) {
    	if(args[0].compareTo("-c") == 0) {
			consume = true;//Consumer mode
		}
    	else if(args[0].compareTo("-p") == 0) {
    		produce = true;//Produce mode
    	}
    	else if(args[0].compareTo("-h") == 0 || args[0].compareTo("--help") == 0) {
    		help = true;//Help mode
    	}
    	else {
    		help = true;//Help mode
    	}
    	//If no help mode, gets the data
    	if(!help) {
    		for(int i=1;i<args.length;i++) {
    			if(args[i].compareTo("-s") == 0 || args[i].compareTo("--server") == 0) {
    				i++;
    				server = args[i];
    			}
    			else if(args[i].compareTo("-g") == 0 || args[i].compareTo("--group-id") == 0) {
    				i++;
    				groupId = args[i];
    			}
    			else if(args[i].compareTo("-C") == 0 || args[i].compareTo("--commit-interval") == 0) {
    				i++;
    				commitInterval = Integer.valueOf(args[i]);
    			}
    			else if(args[i].compareTo("-t") == 0 || args[i].compareTo("--topics") == 0) {
    				i++;
    				//Split the list
    				topics = new ArrayList<String>();
    				String[] list = args[i].split(",");
    				System.out.println(list.length);
    				for(int ind = 0;ind < list.length;ind++) {
    					topics.add(list[ind]);
    				}
    				System.out.println(topics.size());
    			}
    			else if(args[i].compareTo("-m") == 0 || args[i].compareTo("--message") == 0) {
    				i++;
    				value = args[i];
    			}
    			else if(args[i].compareTo("-k") == 0 || args[i].compareTo("--key") == 0) {
    				i++;
    				key = args[i];
    			}
    			else if(args[i].compareTo("-f") == 0 || args[i].compareTo("--file") == 0) {
    				i++;
    				try {
						value = getData(args[i]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
        	}
    	}
    }
    
    /**
     * Display the help message
     */
    private static void displayHelp() {
    	System.out.println("*************** JKAF v0.0.1 *********************************************************");
    	System.out.println("Kafka client CLI");
    	System.out.println("*************** USAGE ***************************************************************");
    	System.out.println("java -jar jkaf-0.0.1-full.jar <mode> [<option> <arg>] ");
    	System.out.println("*************** MODES ***************************************************************");
    	System.out.println("-c,--consume : Consumer mode.");
    	System.out.println("-p,--produce : Producer mode.");
    	System.out.println("*************** OPTIONS *************************************************************");
    	System.out.println("-h,--help : Display this menu.");
    	System.out.println("-s,--server : Bootstrap server address. E.g -s 127.0.0.1:9027");
    	System.out.println("-g,--group-id : Group ID. E.g -g mygrp");
    	System.out.println("-C,--commit-interval : Commit interval.(optional) E.g -C 1000");
    	System.out.println("-t,--topics : topic list. E.g -t topic1,topic2,topic3");
    	System.out.println("-k,--key : key associate with your message.(optional) E.g -k key1");
    	System.out.println("-m,--message : Value of your message. E.g -m \"{test:'test'}\"");
    	System.out.println("-f,--file : Use a file instead. E.g -f /tmp/myfile.json");
    	System.out.println("*************** EXAMPLE *************************************************************");
    	System.out.println("*** Consumer");
    	System.out.println("java -jar jkaf-0.0.1.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1 ");
    	System.out.println("java -jar jkaf-0.0.1.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1,topic2 -C 2000 ");
    	System.out.println("*** Producer");
    	System.out.println("java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -m {test:\"value\"} -k key1");
    	System.out.println("java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -m {test:\"value\"}");
    	System.out.println("java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -f test.txt -k key1");
    	System.out.println("*************** MAINTAINER **********************************************************");
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
