package it.nova.publisher.controller;

import it.nova.publisher.publisher.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message, @RequestParam String topic) {
        try {
            return publisherService.sendRequestAndReceivedReply(message, topic);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in communication: " + e.getMessage();
        }
    }
}