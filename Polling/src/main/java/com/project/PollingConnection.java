package com.project;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PollingConnection {
     public static void connect(String socketAddress, String topic,  EventLoop main) {
         HttpClient client = HttpClient.newBuilder()
                 .version(HttpClient.Version.HTTP_1_1)
                 .followRedirects(HttpClient.Redirect.NEVER)
                 .connectTimeout(Duration.ofSeconds(2))
                 .build();
         DataResponse dataResponse = new DataResponse();
         dataResponse.setEvent(topic);
         HttpRequest request = HttpRequest.newBuilder()
                 .uri(URI.create("http://" + socketAddress + "/updates"))
                 .header("Content-Type","application/json")
                 .POST(HttpRequest.BodyPublishers.ofString(Parser.writeJson(dataResponse)))
                 .build();

         client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                 .whenComplete((response, exception) -> {
                     if (exception != null)
                         main.reportException(exception);
                     main.setPollingClientStatus(topic, false);
                     main.submitResponse(topic, response.body());
                 });
     }

}
