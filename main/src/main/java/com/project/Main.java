package com.project;


import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    public static void main(String[] args) {

        Main m = new Main();
        m.exec();

    }

    void exec(){
        EventLoop l = new EventLoop("127.0.0.1:6060");
        l.subscribe("top", new Handler() {
            @Override
            public void handle(String event, String data) {
                System.out.println("event: " + data);
                try {
                    System.out.println(Parser.readJson(data, DataResponse.class));
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        l.subscribe("bot", new Handler() {
            @Override
            public void handle(String event, String data) {
                System.out.println("event: " + data);
                try {
                    System.out.println(Parser.readJson(data, DataResponse.class));



                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        l.start();
        long t1 = System.currentTimeMillis();
        while (System.currentTimeMillis() - t1 < 50000){
//            System.out.println(System.currentTimeMillis() - t1);
            continue;
        }System.out.println(System.currentTimeMillis() - t1);
        l.unsubscribe("top");
        System.out.println(System.currentTimeMillis() - t1+ ": " + "asdfasdf");
    }
}