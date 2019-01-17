package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CCCJsonUtil {
    public final static String minerJsonPath = "miner.json";
    public final static String enodeJsonPath = "enode.json";
    public final static String contractJsonPath = "contract.json";

    public static String AppendToJsonArray(String fileName, String value) {
        try {
            File file = new File(fileName);
            String minerJson = "";
            if (file.exists()) {
                minerJson = FileUtils.readFileToString(file, "UTF-8");
            }
            JSONArray jsonArray = JSON.parseArray(minerJson);
            if (jsonArray != null && jsonArray.contains(value)) {
                return minerJson;
            }
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            jsonArray.add(value);
            minerJson = jsonArray.toJSONString();
            FileUtils.write(file, minerJson, "UTF-8");
            return minerJson;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-1";
    }

    public static String delFile(String fileName) {
        File file = new File(fileName);
        if (FileUtils.deleteQuietly(file)) {
            return "0";
        }
        return "1";
    }

    public static String getJson(String fileName) {
        try {
            String contractJson = "";
            File contractJsonFile = new File(fileName);
            if (contractJsonFile.exists()) {
                contractJson = FileUtils.readFileToString(contractJsonFile, "UTF-8");
            }
            return contractJson;
        } catch (IOException ignored) {

        }
        return "";
    }
}
