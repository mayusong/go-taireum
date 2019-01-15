package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class GenesisUtil {
    /*
        {
          "config": {
            "chainId": 40602,
            "homesteadBlock": 1,
            "eip150Block": 2,
            "eip150Hash": "0x0000000000000000000000000000000000000000000000000000000000000000",
            "eip155Block": 3,
            "eip158Block": 3,
            "byzantiumBlock": 4,
            "tai": {
              "period": 15
            }
          },
          "nonce": "0x0",
          "timestamp": "0x5bf3b3ef",
          "extraData": "0x",
          "gasLimit": "0xffffffff",
          "difficulty": "0x1",
          "mixHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
          "coinbase": "0x0000000000000000000000000000000000000000",
          "alloc": {
            "ce439e8f2b733bcabf19f8cd2cd296f9a9b421e9": {
              "balance": "0x200000000000000000000000000000000000000000000000000000000000000"
            }
          },
          "number": "0x0",
          "gasUsed": "0x0",
          "parentHash": "0x0000000000000000000000000000000000000000000000000000000000000000"
        }
     */
    public static String initTaiGenesis(int chainId, int period, String[] allocAccounts) {

        JSONObject jsonGenesis = new JSONObject(true);
        JSONObject jsonConfig = new JSONObject(true);
        JSONObject jsonTai = new JSONObject(true);
        JSONObject jsonAlloc = new JSONObject(true);
        JSONObject jsonBalance = new JSONObject(true);
        jsonBalance.put("balance", "0x200000000000000000000000000000000000000000000000000000000000000");


        jsonTai.put("period", period);

        jsonConfig.put("chainId", chainId);
        jsonConfig.put("homesteadBlock", 1);
        jsonConfig.put("eip150Block", 2);
        jsonConfig.put("eip150Hash", "0x0000000000000000000000000000000000000000000000000000000000000000");
        jsonConfig.put("eip155Block", 3);
        jsonConfig.put("eip158Block", 3);
        jsonConfig.put("byzantiumBlock", 4);
        jsonConfig.put("tai", jsonTai);

        jsonGenesis.put("config", jsonConfig);
        jsonGenesis.put("nonce", "0x0");
        String timestamp = "0x" + Integer.toHexString((int)(System.currentTimeMillis() / 1000)).toLowerCase();
        jsonGenesis.put("timestamp", timestamp);
        jsonGenesis.put("extraData", "0x");
        jsonGenesis.put("gasLimit", "0xffffffff");
        jsonGenesis.put("difficulty", "0x1");
        jsonGenesis.put("mixHash", "0x0000000000000000000000000000000000000000000000000000000000000000");
        jsonGenesis.put("coinbase", "0x0000000000000000000000000000000000000000");

        for (String account : allocAccounts) {
            jsonAlloc.put(account, jsonBalance);
        }
        jsonGenesis.put("alloc", jsonAlloc);
        jsonGenesis.put("number", "0x0");
        jsonGenesis.put("gasUsed", "0x0");
        jsonGenesis.put("parentHash", "0x0000000000000000000000000000000000000000000000000000000000000000");
        return jsonGenesis.toJSONString();
    }
    public static String getChainId() {
        return getChainId("genesis.json");
    }

    public static String getChainId(String genesisFile) {
        if (StringUtils.isEmpty(genesisFile)) {
            return "-1";
        }
        try {
            String genesisJsonString = FileUtils.readFileToString(new File(genesisFile), "UTF-8");
            JSONObject jsonObject = JSON.parseObject(genesisJsonString);
            return String.valueOf(jsonObject.getJSONObject("config").getIntValue("chainId"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }
}
