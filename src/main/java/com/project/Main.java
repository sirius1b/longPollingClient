package com.project;

public class Main implements Handler{
    public static void main(String[] args) {

        Main m = new Main();
        m.exec();

    }

    void exec(){
        EventLoop l = new EventLoop("127.0.0.1:6060");
        l.subscribe("top", this);
        l.subscribe("bot", this);
        l.start();
        long t1 = System.currentTimeMillis();
        while (System.currentTimeMillis() - t1 < 50000){
//            System.out.println(System.currentTimeMillis() - t1);
            continue;
        }System.out.println(System.currentTimeMillis() - t1);
        l.unsubscribe("top");
        System.out.println(System.currentTimeMillis() - t1+ ": " + "asdfasdf");
    }
    @Override
    public void handle(String event, String data) {
        System.out.println(event);
    }
}