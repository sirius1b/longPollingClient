package com.project;


import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    public static void main(String[] args) {

        Main m = new Main();
        m.exec();
        System.out.println("=--end");
    }

    void exec(){
        EventLoop l = new EventLoop("127.0.0.1:6060");
        l.subscribe("top", new Handler() {
                    @Override
                    public void handle(DataResponse dataResponse) {
                        System.out.println("--> " + dataResponse.getData());
                    }
                });
        l.start();

        long t1 = System.currentTimeMillis();
        System.out.println(System.currentTimeMillis() - t1);
        publish();

        System.out.println(System.currentTimeMillis() - t1+ ": " + "asdfasdf");
        while (System.currentTimeMillis() - t1< 1000){
            continue;
        }
        publish();
        t1 = System.currentTimeMillis();
        while (System.currentTimeMillis() - t1< 5000){
            continue;
        }
        l.unsubscribe("top");
        l.stop();
        System.out.println("main ended");
    }

    private void publish() {
        DataResponse d = new DataResponse();
        d.setData("as");
        d.setSrc("asdfas");
        d.setEvent("top");
        Publisher.getPublisher("127.0.0.1:6060").publish(d);
    }
}