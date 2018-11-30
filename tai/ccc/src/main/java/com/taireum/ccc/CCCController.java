package com.taireum.ccc;

import org.apache.commons.io.FileUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
public class CCCController {


    @RequestMapping("/api/getMiner")
    public String getMiner() {
        File file = new File("miner.json");
        try {
            String minerJson = FileUtils.readFileToString(file);
            return minerJson;
        } catch (IOException ignored) {

        }
        return "";
    }

    @RequestMapping("/api/getEnode")
    public String getEnode() {
        File file = new File("permissin.json");
        try {
            String minerJson = FileUtils.readFileToString(file);
            return minerJson;
        } catch (IOException ignored) {

        }
        return "";
    }

    @RequestMapping("/api/getContract")
    public String getContract() {
        File file = new File("contract.json");
        try {
            String minerJson = FileUtils.readFileToString(file);
            return minerJson;
        } catch (IOException ignored) {

        }
        return "";
    }

}
