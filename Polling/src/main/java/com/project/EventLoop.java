package com.project;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoop implements Runnable{
    String socketAddress;
    Map<String, Boolean> pollingClientActive;
    Map<String, Handler> handlers;

    Map<String, LinkedList<String>> results;
    ExecutorService eventLoopExecutor;
    ExecutorService handlerExector;
    boolean running;



    public EventLoop(String socketAddress){
        this.socketAddress = socketAddress;
        this.pollingClientActive = new HashMap<>();
        this.handlers = new HashMap<>();
        this.results = new HashMap<>();
        eventLoopExecutor = Executors.newSingleThreadExecutor();
        handlerExector = Executors.newCachedThreadPool();
        running = false;
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
        if (pollingClientActive.containsKey(topic)){
            results.get(topic).add(data);
        }
    }

    public void start(){
        running = true;
        eventLoopExecutor.execute(this);

    }
    public void stop(){
        running = false;
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
                        handlerExector.submit(() ->
                                handlers.get(e.getKey()).handle(e.getKey(), data));
                    }
                }
            }

        }
    }

    public void reportException(Throwable exception)  {
        if (running)
            System.out.println("->> stoping: Event loop due exception: "+ exception.getMessage());
        stop();
    }
}
