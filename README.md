# spring-couchbase-kafka
## What is Spring?
This Spring Boot reactive demo project exposes a RESTful web API to apply CRUD (create, read, update, delete) operations on a simple Product object, which is persisted in a couchbase database. It also produces the message using reactive kafka and consuming and marking it as acknowledged. The implementation was done in the reactive Webflux stack, which is an alternative to the more commonly known Spring Web MVC stack.

For more information about Webflux, visit https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html

## Project Structure
The project is structured as follows
- Web Layer
  - `MinimumPriceController` handles all incoming request using `PriceRepository` and `kafkaReactor`(package)
- Repository Layer
  - `PriceRepository` it uses Spring JPA for couchbase which is `ReactiveCouchbaseRepository` 
  - `Prices` it contains what type of document we are sending
- Kafka Layer
  - `KafkaProducerConfig` to configure the kafka producer
  - `KafkaMessageProducer` to send message to kafka topic
  - `KafkaConsumerConfig` to configure the kafka consumer
  - `KafkaMessageReciever` to consume the kafka message and mark it as acknowledged
  
 
## How to configure Couchbase?
- Download and install couchbase(or you can use docker image of couchbase I have added Dockerfile in couchbase folder with the configure file which will configure the couchbase for you)
- Create a cluster in couchbase(remember by default couchbase is connected to port 8091 and that is what I have configured in this app if you want to change the port you can change it in Application.properties in the resource folder in main)
- In `buckets` create bucket with name `minimumprice`
- In `security` create a new user with name `minimumprice` (or the bucket which you want to create bucket name should be as same as the username) with password `123456` and give full admin access

You can put any password and bucket name but remember the bucket name should be same as the user name which you will create. You can change all this in `application.properties`

For kafka you just need to download kafka and run it. By default, kafka runs on port 9092. If you are changing the kafka port you can change in the `ApplicationConstants` java file in application/constant

For building image in the docker. I have added the Dockerfile for the app as well. If you need to make the image of the app you can build the app and then using Dockerfile you can make image of the app

Docker-compose file is there to run the all images(kafka, couchbase and our app) in one. So, that we won't be needing to use Networks to communicate between the image.
