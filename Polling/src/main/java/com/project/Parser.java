package com.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parser {

    private static Parser parser;
    ObjectMapper mapper;
    public static Parser getParser(){
        if (parser == null)
            parser = new Parser();
        return parser;
    }
    private Parser(){
        mapper = new ObjectMapper();
    }

    public static <T> T readJson(String content, Class<T> valueType) throws JsonProcessingException {
        return getParser().mapper.readValue(content, valueType);
    }

}
