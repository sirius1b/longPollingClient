package com.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Publisher {

    private  final Logger logger = Logger.getLogger(String.valueOf(this.getClass()));
    private String socketAddress;


    private static Publisher publisher;

    public static  Publisher getPublisher(String socketAddress){
        if (publisher == null){
            publisher = new Publisher();
        }
        publisher.socketAddress = socketAddress;
        return  publisher;
    }
    private Publisher(){
        logger.setLevel(Level.ALL);
    }

    public void publish(  DataResponse dataResponse)  {
        push( Parser.writeJson(dataResponse));
    }

    private void push( String data) {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type","application/json")
                .uri(URI.create("http://" + socketAddress + "/publish" ))
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((resp, e) -> {if (e != null) logger.info("received response");});
    }
}
