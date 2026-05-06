package net.dima.project.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chat")
    public String trit(Model model) {
        model.addAttribute("sessionId", UUID.randomUUID().toString());
        model.addAttribute("mode", "main");
        return "chat/trit";
    }
}
