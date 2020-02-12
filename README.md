# JKaf

JKaf is a simple CLI client for Kafka made to simplify the development.

The current version is 0.0.1

## Usage 

```sh
java -jar jkaf-0.0.1.jar <mode> [<option> <arg>] 
```

## Mode 

* -c,--consume : Consumer mode.
* -p,--produce : Producer mode.

## Options 

* -h,--help : Display this menu.
* -s,--server : Bootstrap server address. E.g -s 127.0.0.1:9027
* -g,--group-id : Group ID. E.g -g mygrp
* -C,--commit-interval : Commit interval.(optional) E.g -C 1000
* -t,--topics : topic list. E.g -t topic1,topic2,topic3
* -k,--key : key associate with your message.(optional) E.g -k key1
* -m,--message : Value of your message. E.g -m "{test:'test'}"
* -f,--file : Use a file instead. E.g -f /tmp/myfile.json

## Example

### Consumer

```sh
java -jar jkaf-0.0.1.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1 
java -jar jkaf-0.0.1.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1,topic2 -C 2000 
```

### Producer

```sh
java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -m {test:"value"} -k key1
java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -m {test:"value"}
java -jar jkaf-0.0.1.jar -p -s 127.0.0.1:9092 -t topic1 -f test.txt -k key1
```

## Maintainer

Anthony Paulin <paulin.anthony@gmail.com>

## Notes

* SSL is not yet implemented
* The options are currently limited, I will add them when needed. Feel free to request them.
