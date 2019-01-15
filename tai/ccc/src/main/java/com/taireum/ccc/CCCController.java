package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
//        if (!StringUtils.isEmpty(minerJson)) {
//            return minerJson;
//        }
        minerJson = CCCJsonUtil.getJson(CCCJsonUtil.minerJsonPath);
        return minerJson;
    }

    @RequestMapping("/api/getEnode")
    public String getEnode() {
//        if (!StringUtils.isEmpty(enodeJson)) {
//            return enodeJson;
//        }
        enodeJson = CCCJsonUtil.getJson(CCCJsonUtil.enodeJsonPath);
        return enodeJson;
    }

    @RequestMapping("/api/getContract")
    public String getContract() {

//        if (!StringUtils.isEmpty(contractJson)) {
//            return contractJson;
//        }
        contractJson = CCCJsonUtil.getJson(CCCJsonUtil.contractJsonPath);
        return contractJson;
    }


    @RequestMapping(value = "/api/addMiner", method = RequestMethod.POST)
    public String addMiner(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }
        minerJson = CCCJsonUtil.AppendToJsonArray(CCCJsonUtil.minerJsonPath, body);
        return minerJson;
    }

    @RequestMapping(value = "/api/addEnode", method = RequestMethod.POST)
    public String addEnode(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }
        enodeJson = CCCJsonUtil.AppendToJsonArray(CCCJsonUtil.enodeJsonPath, body);
        return enodeJson;
    }

    @RequestMapping(value = "/api/addContract", method = RequestMethod.POST)
    public String addContract(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }
        contractJson = CCCJsonUtil.AppendToJsonArray(CCCJsonUtil.contractJsonPath, body);
        return contractJson;
    }

    @RequestMapping("/api/delMiner")
    public String delMiner() {
        minerJson = "";
        return CCCJsonUtil.delFile(CCCJsonUtil.minerJsonPath);
    }

    @RequestMapping("/api/delEnode")
    public String delEnode() {
        enodeJson = "";
        return CCCJsonUtil.delFile(CCCJsonUtil.enodeJsonPath);
    }

    @RequestMapping("/api/delContract")
    public String delContract() {
        contractJson = "";
        return CCCJsonUtil.delFile(CCCJsonUtil.contractJsonPath);
    }

    public static void initConfigJson(String local_host, String local_host_port) {
        String configName = "config.json";
        String host = "http://" + local_host + ":" + local_host_port;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("EnodeUrl", host + "/api/getEnode");
        jsonObject.put("RpcUrl", host + "/api/getRpc");
        jsonObject.put("MinerUrl", host + "/api/getMiner");
        jsonObject.put("ContractUrl", host + "/api/getContract");

        try {
            FileUtils.write(new File(configName), jsonObject.toJSONString(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
