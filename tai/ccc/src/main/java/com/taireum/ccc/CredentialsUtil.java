package com.taireum.ccc;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;
import org.thymeleaf.util.StringUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class CredentialsUtil {

    public static boolean checkValidAccount(String dataDir, String unlockAccount) {
        if (unlockAccount.toLowerCase().startsWith("0x")) {
            unlockAccount = unlockAccount.replaceAll("0x", "");
        }
        File keystore = new File(dataDir + "/keystore");

        Collection<File> files = FileUtils.listFiles(keystore, null, false);
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(unlockAccount.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String getAccountFilePath(String dataDir, String account) {
        if (StringUtils.isEmpty(dataDir) || StringUtils.isEmpty(account)) {
            return null;
        }
        if (account.toLowerCase().startsWith("0x")) {
            account = account.replaceAll("0x", "");
        }

        File keystorePath = new File(dataDir + "/keystore");
        Collection<File> files = FileUtils.listFiles(keystorePath, null, false);
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(account.toLowerCase())) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
    public static String getAllAccountAddress(String dataDir) {
        if (StringUtils.isEmpty(dataDir)) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        File keystorePath = new File(dataDir + "/keystore");
        Collection<File> files = FileUtils.listFiles(keystorePath, null, false);
        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            int index = fileName.lastIndexOf("--");
            String accountAddress = fileName.substring(index + 2);
            jsonArray.add(accountAddress);
        }
        return jsonArray.toJSONString();
    }
    public static Credentials getCredentials(String password, String accountPath) {
        try {
            if (StringUtils.isEmpty(password) || StringUtils.isEmpty(accountPath)) {
                return null;
            }
            Credentials credentials = WalletUtils.loadCredentials(password, accountPath);
            return credentials;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
        return null;
    }
}
