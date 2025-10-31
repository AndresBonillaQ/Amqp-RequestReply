package it.nova.logger.controller;

import it.nova.logger.listener.ErrorsListener;
import it.nova.logger.model.FailedMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/errors")
public class ErrorViewerController {

    @GetMapping
    public String viewFailedMessages(Model model) {
        List<FailedMessage> failedMessages = ErrorsListener.FAILED_MESSAGES_LOG;
        model.addAttribute("errors", failedMessages);
        return "errors";
    }
}