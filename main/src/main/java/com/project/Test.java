package com.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        String json = "{\"who\":\"John\",\"what\":30}";

        ObjectMapper objectMapper = new ObjectMapper();
        DataResponse myObject = objectMapper.readValue(json, DataResponse.class);

        System.out.println("Name: " + myObject.getWho());
        System.out.println("Age: " + myObject.getWhat());
        System.out.println("daf: " + myObject.getWhen());

        System.out.println(objectMapper.writeValueAsString(myObject));

    }
}
