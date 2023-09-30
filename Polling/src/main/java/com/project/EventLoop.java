package com.project;


import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EventLoop implements Runnable{

    private static final Logger logger = Logger.getLogger(String.valueOf(EventLoop.class));
    String socketAddress;
    ConcurrentHashMap<String, Boolean> pollingClientActive;
    ConcurrentHashMap<String, Handler> handlers;

    ConcurrentHashMap<String, LinkedList<String>> results;
    ExecutorService eventLoopExecutor;
    ExecutorService handlerExector;
    boolean running;



    public EventLoop(String socketAddress){
        this.socketAddress = socketAddress;
        this.pollingClientActive = new ConcurrentHashMap<>();
        this.handlers = new ConcurrentHashMap<>();
        this.results = new ConcurrentHashMap<>();
        eventLoopExecutor = Executors.newSingleThreadExecutor();
        handlerExector = Executors.newCachedThreadPool();
        running = false;
        logger.setLevel(Level.ALL);
    }

    public void subscribe(String eventIdentifier, Handler handler){
        if(!pollingClientActive.containsKey(eventIdentifier)){
            pollingClientActive.put(eventIdentifier, false);
            handlers.put(eventIdentifier, handler);
            results.put(eventIdentifier, new LinkedList<>());
        }
    }
    public void unsubscribe(String eventIdentifier){
        if (pollingClientActive.containsKey(eventIdentifier)){
            pollingClientActive.remove(eventIdentifier);
            handlers.remove(eventIdentifier);
            results.remove(eventIdentifier);
        }
    }

    public void setPollingClientStatus(String eventIdentifier, boolean status){
        if (pollingClientActive.containsKey(eventIdentifier)){
            pollingClientActive.put(eventIdentifier, status);
        }
    }

    public void submitResponse(String topic, String data){
        logger.info("got data on: " + topic + " data: " + data);
        if (pollingClientActive.containsKey(topic)){
            results.get(topic).add(data);
        }
    }

    public void start(){
        if (!running ){
            running = true;
            eventLoopExecutor.execute(this);
        }


    }
    public void stop(){
        running = false;
        eventLoopExecutor.shutdownNow();
        handlerExector.shutdownNow();
        eventLoopExecutor = Executors.newSingleThreadExecutor();
        handlerExector = Executors.newCachedThreadPool();
    }

    public boolean getStatus(){
        return running;
    }

    @Override
    public void run() {
        while (running){
            for(Map.Entry<String, Boolean> e: pollingClientActive.entrySet()){
                if (!e.getValue()){
                    PollingConnection.connect(socketAddress, e.getKey(), this);
                    setPollingClientStatus(e.getKey(), true);
                }
            }
            for (Map.Entry<String, LinkedList<String>> e: results.entrySet()){
                if (pollingClientActive.containsKey(e.getKey())){
                    while (!e.getValue().isEmpty()){
                        String data = e.getValue().removeFirst();
                        logger.info("about to submit: for " + data);
                        handlerExector.submit(() ->
                                handlers.get(e.getKey()).handle(Parser.readJson(data, DataResponse.class)));
                    }
                }
            }

        }
    }

    public void reportException(Throwable exception)  {
        if (running)
            logger.info("->> stoping: Event loop due exception: "+ exception.getMessage());
        logger.info("stopping event loop");
        stop();
    }
}
