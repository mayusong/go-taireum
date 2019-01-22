package com.taireum.ccc;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/creator.html")
    public String creator(){
        return "creator.html";
    }

    @RequestMapping("/")
    public String index(){
        return "index.html";
    }

    @RequestMapping("/joiner.html")
    public String joiner(){
        return "joiner.html";
    }
}
