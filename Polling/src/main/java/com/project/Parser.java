package com.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Logger;

public class Parser {
    private static final Logger logger = Logger.getLogger(String.valueOf(Parser.class));
    private static Parser parser;

    private ObjectMapper mapper;

    public static Parser getParser(){
        if (parser == null)
            parser = new Parser();
        return parser;
    }
    private Parser(){
        mapper = new ObjectMapper();
    }

    public static <T> T readJson(String content, Class<T> valueType)  {
        try {
            return getParser().mapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            logger.info("error while parsing: " + e.getMessage() );
            throw new RuntimeException(e);
        }
    }

    public static String writeJson(DataResponse dataResponse){
        try {
            return getParser().mapper.writeValueAsString(dataResponse);
        } catch (JsonProcessingException e) {
            logger.info("error while writing: " + e.getMessage() );
            throw new RuntimeException(e);
        }
    }



}
