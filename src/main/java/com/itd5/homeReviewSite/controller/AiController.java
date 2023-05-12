package com.itd5.homeReviewSite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("ai")
public class AiController {
    @GetMapping("list")
    public String list(){
        return "ai/list";
    }
}
