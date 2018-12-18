package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;

@RestController
public class CCCController {
    private String minerJson = "";
    private String enodeJson = "";
    private String contractJson = "";
    private final static String minerJsonPath = "miner.json";
    private final static String enodeJsonPath = "enode.json";
    private final static String contractJsonPath = "contract.json";


    @RequestMapping("/api/getMiner")
    public String getMiner() {
        try {
            if (!StringUtils.isEmpty(minerJson)) {
                return minerJson;
            }
            File minerJsonFile = new File(minerJsonPath);
            if (minerJsonFile.exists()) {
                minerJson = FileUtils.readFileToString(minerJsonFile, "UTF-8");
            }
            return minerJson;
        } catch (IOException ignored) {

        }
        return "";
    }

    @RequestMapping("/api/getEnode")
    public String getEnode() {

        try {

            if (!StringUtils.isEmpty(enodeJson)) {
                return enodeJson;
            }
            File enodeJsonFile = new File(enodeJsonPath);
            if (enodeJsonFile.exists()) {
                enodeJson = FileUtils.readFileToString(enodeJsonFile, "UTF-8");
            }
            return enodeJson;
        } catch (IOException ignored) {

        }
        return "";
    }

    @RequestMapping("/api/getContract")
    public String getContract() {
        try {
            if (!StringUtils.isEmpty(contractJson)) {
                return contractJson;
            }
            File contractJsonFile = new File(contractJsonPath);
            if (contractJsonFile.exists()) {
                contractJson = FileUtils.readFileToString(contractJsonFile, "UTF-8");
            }
            return contractJson;
        } catch (IOException ignored) {

        }
        return "";
    }


    @RequestMapping(value="/api/addMiner", method = RequestMethod.POST)
    public String addMiner(@RequestBody String body) {
            if (StringUtils.isEmpty(body)) {
                return "-1";
            }

            try {
                File file = new File(minerJsonPath);
                if (file.exists()) {
                    if (StringUtils.isEmpty(minerJson)) {
                        minerJson = FileUtils.readFileToString(file, "UTF-8");
                    }
                }
                JSONArray jsonArray = JSON.parseArray(minerJson);
                if (jsonArray != null && jsonArray.contains(body)) {
                    return "1";
                }
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                }
                jsonArray.add(body);
                System.out.println(jsonArray.toString());
                minerJson = jsonArray.toJSONString();
                FileUtils.write(file, minerJson, "UTF-8");
                return "0";

            } catch (IOException e) {
                e.printStackTrace();
            }

        return "-1";
    }

    @RequestMapping(value="/api/addEnode", method = RequestMethod.POST)
    public String addEnode(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }

        try {
            File file = new File(enodeJsonPath);
            if (file.exists()) {
                if (StringUtils.isEmpty(enodeJson)) {
                    enodeJson = FileUtils.readFileToString(file, "UTF-8");
                }
            }
            JSONArray jsonArray = JSON.parseArray(enodeJson);
            if (jsonArray != null && jsonArray.contains(body)) {
                return "1";
            }
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            jsonArray.add(body);
            System.out.println(jsonArray.toString());
            enodeJson = jsonArray.toJSONString();
            FileUtils.write(file, enodeJson, "UTF-8");
            return "0";

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value="/api/addContract", method = RequestMethod.POST)
    public String addContract(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }

        try {
            File file = new File(contractJsonPath);
            if (file.exists()) {
                if (StringUtils.isEmpty(contractJson)) {
                    contractJson = FileUtils.readFileToString(file, "UTF-8");
                }
            }
            JSONArray jsonArray = JSON.parseArray(contractJson);
            if (jsonArray != null && jsonArray.contains(body)) {
                return "1";
            }
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            jsonArray.add(body);
            System.out.println(jsonArray.toString());
            contractJson = jsonArray.toJSONString();
            FileUtils.write(file, contractJson, "UTF-8");
            return "0";

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping("/api/delMiner")
    public String delMiner() {
        minerJson = "";
        File file = new File(minerJsonPath);
        if (FileUtils.deleteQuietly(file)) {
            return "0";
        }
        return "1";
    }
    @RequestMapping("/api/delEnode")
    public String delEnode() {
        enodeJson = "";
        File file = new File(enodeJsonPath);
        if (FileUtils.deleteQuietly(file)) {
            return "0";
        }
        return "1";
    }
    @RequestMapping("/api/delContract")
    public String delContract() {
        contractJson = "";
        File file = new File(contractJsonPath);
        if (FileUtils.deleteQuietly(file)) {
            return "0";
        }
        return "1";
    }
}
