package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

import java.util.Optional;

@Service

public class OrderService {

    private final AmazonSQS sqsClient;

    @Autowired
    public OrderService(
            @Value("${amazon.aws.accesskey}") String accessKey,
            @Value("${amazon.aws.secretkey}") String secretKey,
            @Value("${amazon.aws.region}") String region
    ) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        this.sqsClient = AmazonSQSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
    @Autowired
    private OrderRepository repository;

    @Value("${aws.sqs.orderQueueUrl}")
    private String orderQueueUrl;


    public Order save(Order order) {
        Order savedOrder = repository.save(order);
        try {
            String messageBody = new ObjectMapper().writeValueAsString(savedOrder);
            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(orderQueueUrl)
                    .withMessageBody(messageBody);

            sqsClient.sendMessage(request);
            System.out.println("Mensaje enviado a SQS: " + messageBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedOrder;
    }
    public Iterable<Order> findAll() {
        return repository.findAll();
    }

    public Optional<Order> findById(String id) {
        return repository.findById(Long.valueOf(id));
    }


    public Order update(Order order) {
        return repository.save(order);
    }

    public void delete(String id) {
        repository.deleteById(Long.valueOf(id));
    }
}
