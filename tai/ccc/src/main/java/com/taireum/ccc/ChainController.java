package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

@RestController
public class ChainController {

    @Autowired
    private RpcConfig mRpcConfig;

    private String mDataDir = "tai_data_dir";
    private DaemonExecutor mDaemonExecutor = null;
    @RequestMapping(value="/api/chain/addGenesis", method = RequestMethod.POST)
    public String addGenesis(@RequestBody String payload) {

        try {
            File path = new File("genesis.json");
            if (!path.exists()) {
                FileUtils.write(path, payload, "UTF-8");
                return "0";
            } else {
                return "1";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value="/api/chain/addGenesis", method = RequestMethod.GET)
    public String addGenesis() {
        return "";
    }

    @RequestMapping(value="/api/chain/delGenesis", method = RequestMethod.GET)
    public String delGenesis() {
        File path = new File("genesis.json");
        if (path.isFile() && path.exists()) {
            FileUtils.deleteQuietly(path);
            return "0";
        }

        return "1";
    }

    @RequestMapping(value="/api/chain/initTai", method = RequestMethod.GET)
    public String initTai() {
        String genesisJson = "genesis.json";

        //geth --datadir tai_data_dir init TaiTest.json
        String command = "geth --datadir " + mDataDir + " init " + genesisJson;
        try {
            File initLock = new File(".initLock");
            if (initLock.exists()) {
                return "1";
            }
            int result = Runtime.getRuntime().exec(command).waitFor();
            if (result == 0) {

                FileUtils.write(initLock, "1", "UTF-8");
            }
            return String.valueOf(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value="/api/chain/startTai", method = RequestMethod.POST)
    public String startTai(@RequestBody String body) {
        File pwdPath = new File("startTaiPassword");
        boolean isStartMine = true;
        String unlockAccount = "";
        String password = "";

        JSONObject map = JSON.parseObject(body);
        if (map.containsKey("unlockAccount")) {
            unlockAccount = map.get("unlockAccount").toString();
            if (!unlockAccount.startsWith("0x")) {
                unlockAccount = "0x" + unlockAccount;
            }

            if (!checkValidAccount(unlockAccount)) {
                return "-1";
            }

            if (map.containsKey("password")) {
                password = map.get("password").toString();
                if (password != null && !password.isEmpty()) {

                    try {
                        FileUtils.write(pwdPath, password, "UTF-8");
                    } catch (IOException e) {
                        isStartMine = false;
                    }
                }
            }
        } else {
            isStartMine = false;
        }
        String port = "30305";
        String rpcPort = "8555";
        if (map.containsKey("port")) {
            port = map.get("port").toString();
        }
        if (map.containsKey("rpcPort")) {
            rpcPort = map.get("rpcPort").toString();
        }
        mRpcConfig.setRpcPort(rpcPort);
        mRpcConfig.setRpcAddr("127.0.0.1");

        String networkid = "40602";
        String rpcapi = "\"db,debug,eth,net,web3,personal,shh,txpool,web3,admin\"";

        System.out.println(unlockAccount + " " + password);

        String startCmds = "geth --datadir " + mDataDir + " --nodiscover --networkid " + networkid + " --port "
                + port + " --rpc --rpccorsdomain \"*\" --rpcapi " + rpcapi + " --rpcport " + rpcPort + " ";
        if (isStartMine) {
            startCmds += " --mine --unlock " + unlockAccount + " --password startTaiPassword --etherbase " + unlockAccount + " --verbosity 0";
        } else {
            startCmds += " --verbosity 0";
        }
        String result = "1";
        try {

            System.out.println(startCmds);

            if (mDaemonExecutor == null) {
                CommandLine commandLine = CommandLine.parse(startCmds);
                mDaemonExecutor = new DaemonExecutor();
                mDaemonExecutor.setWatchdog(new ExecuteWatchdog(-1));
                mDaemonExecutor.execute(commandLine);
                result = "0";
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean checkValidAccount(String unlockAccount) {
        if (unlockAccount.toLowerCase().startsWith("0x")) {
            unlockAccount = unlockAccount.replaceAll("0x", "");
        }
        File keystore = new File(mDataDir + "/keystore");

        Collection<File> files = FileUtils.listFiles(keystore, null, false);
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(unlockAccount.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value="/api/chain/stopTai", method = RequestMethod.GET)
    public String stopTai() {
        if (mDaemonExecutor != null) {
            mDaemonExecutor.getWatchdog().destroyProcess();
            mDaemonExecutor = null;
            return "0";
        } else {
            return "-1";
        }
    }

    @RequestMapping(value="/api/chain/newAccount", method = RequestMethod.POST)
    public String newAccount(@RequestBody String body) {
        try {
            JSONObject map = JSON.parseObject(body);
            if (map.containsKey("password")) {
                String command = "geth --datadir " + mDataDir + " ";
                File pwdPath = new File("pwd00");
                String password = map.get("password").toString();
                if (password != null && !password.isEmpty()) {
                    FileUtils.write(pwdPath, password, "UTF-8");
                }
                System.out.println("Password:" + password);

                command += "account new --password pwd00";

                System.out.println("command:" + command);

                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command);

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(process.getInputStream()));

                String result = "";
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    result += s;
                }
                FileUtils.deleteQuietly(pwdPath);
                System.out.println(result);
                if (result.startsWith("Address:")) {
                    int start = result.indexOf("{");
                    int end = result.indexOf("}");
                    result = result.substring(start + 1, end);
                    return "{\"Address\":\"" + result + "\"}";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
