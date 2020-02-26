# JKaf

JKaf is a simple CLI client for Kafka made to simplify development and testing.

## Usage 

```sh
java -jar jkaf-0.1.0.jar <mode> [<option> <arg>] 
```

## Mode 

* -c,--consume : Consumer mode.
* -p,--produce : Producer mode.

## Options

### General Options 

* -h,--help : Display this menu.
* -s,--server <host>: Bootstrap server address.E.g -s 127.0.0.1:9027
* -g,--group-id <group_id> : Group ID. E.g -g mygrp
* -C,--commit-interval <commit_interval> : Commit interval.(optional) E.g -C 1000
* -t,--topics <topic1>[,topic2...]: topic list. E.g -t topic1,topic2,topic3
* -k,--key <key>: key associate with your message.(optional) E.g -k key1
* -m,--message <message> : Value of your message. E.g -m "{test:'test'}"
* -f,--file <file_path> : Use a file instead. E.g -f /tmp/myfile.json
* -x,--number <number> : For the consumer only, stop after X message. E.g -x 5")
* -e,--earliest : Read from the earliest message not committed

### Security options

* -T,--truststore <path>: truststore location.
* -tp,--truststore-password <password>: Use SSL encryption. E.g -P mypassword1234
* -K,--keystore <path> : keystore location.
* -kp,--keystore-password <password>: keystore password.
* -a,--authentication <password> : Use SSL password authentication. E.g -a mysslpasword 

### Extra 

* -i,--install : Install a certificate .Usage: <host>[:port] [passphrase] 

## Examples

### Consumer

```sh
java -jar jkaf-0.1.0.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1 
java -jar jkaf-0.1.0.jar -c -s 127.0.0.1:9092 -g mygroupe -t topic1,topic2 -C 2000 
```

### Producer

```sh
java -jar jkaf-0.1.0.jar -p -s 127.0.0.1:9092 -t topic1 -m {test:"value"} -k key1
java -jar jkaf-0.1.0.jar -p -s 127.0.0.1:9092 -t topic1 -m {test:"value"}
java -jar jkaf-0.1.0.jar -p -s 127.0.0.1:9092 -t topic1 -f test.txt -k key1
```

### SSL 

```sh
java -jar jkaf-0.1.0.jar -p -s 127.0.0.1:9092 -g mygroupe -t topic1 -m {test:"value"} -T /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.131-11.b12.el7.x86_64/jre/lib/security/cacerts -K cert/server.keystore -tp changeit -kp changeit

```

## JAVA Keystore

Java uses its own keystore and trustore to manage the certificate

To import a key to the keystore :

* Step 1

```sh
openssl pkcs12 -export -in server.crt -inkey server.key   -out server.p12 -name [some-alias]  -CAfile ca.crt -caname root
```

* Step 2

```sh
keytool -importkeystore -deststorepass [changeit] -destkeypass [changeit] -destkeystore server.keystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass some-password  -alias [some-alias]
```

* To import a certificate to the truststore

```sh
keytool -import -alias myalias -file public.cert -storetype JKS -keystore server.truststore
```

## Maintainer

Anthony Paulin <paulin.anthony@gmail.com>

## Notes

* I build the requirement on demands. Feel free to raise any issues.(Preferably directly from the issue tab on github)
