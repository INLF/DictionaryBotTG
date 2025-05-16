package org.example.dictionarybot.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TelegramWebAppController {
    @GetMapping("/app")
    public String getWebApp(Model model) {
        model.addAttribute("title", "Демо WebApp");
        model.addAttribute("message", "Привет из Thymeleaf WebApp внутри Telegram!");
        return "webapp";
    }
}
