package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.taireum.ccc.CCCController.initConfigJson;

@RestController
public class ChainController {

    @Autowired
    private RpcConfig mRpcConfig;

    @Autowired
    Environment environment;

    private String mDataDir = "tai_data_dir";

    private DaemonExecutor mDaemonExecutor = null;

    @RequestMapping(value = "/api/chain/addGenesis", method = RequestMethod.POST)
    public String addGenesis(@RequestBody String payload) {
        if (StringUtils.isEmpty(payload)) {
            return "-1";
        }
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

    @RequestMapping(value = "/api/chain/initGenesis", method = RequestMethod.POST)
    public String initGenesis(@RequestBody String payload) {
        if (StringUtils.isEmpty(payload)) {
            return "-1";
        }

        JSONObject postData = JSON.parseObject(payload);
        int chainId = postData.getIntValue("chainId");
        int period = postData.getIntValue("period");
        String genesisAllocAccount = postData.getString("genesisAllocAccount");

        String genesisJson = GenesisUtil.initTaiGenesis(chainId, period, new String[]{genesisAllocAccount});

        return addGenesis(genesisJson);
    }

    @RequestMapping(value = "/api/chain/delGenesis", method = RequestMethod.GET)
    public String delGenesis() {
        File path = new File("genesis.json");
        if (path.isFile() && path.exists()) {
            FileUtils.deleteQuietly(path);
            return "0";
        }

        return "1";
    }

    @RequestMapping(value = "/api/chain/initTai", method = RequestMethod.GET)
    public String initTai() {

        String local_host = InetAddress.getLoopbackAddress().getHostAddress();
        String local_host_port = environment.getProperty("local.server.port");
        initConfigJson(local_host, local_host_port);

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

    @RequestMapping(value = "/api/chain/getAccounts", method = RequestMethod.GET)
    public String getAccounts() {
        return CredentialsUtil.getAllAccountAddress("tai_data_dir");
    }

    @RequestMapping(value = "/api/chain/startTai", method = RequestMethod.POST)
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

            if (!CredentialsUtil.checkValidAccount(mDataDir, unlockAccount)) {
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
        String verbosity = "0";
        if (map.containsKey("verbosity")) {
            verbosity = map.get("verbosity").toString();
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
        mRpcConfig.setAccount(unlockAccount);
        mRpcConfig.setPassword(password);

        String networkid = GenesisUtil.getChainId();
        if (networkid.equals("-1")) {
            return "-1";
        }

        String rpcapi = "\"db,debug,eth,net,web3,personal,shh,txpool,web3,admin\"";

        System.out.println(unlockAccount + " " + password);

        String startCmds = "geth --debug --verbosity " + verbosity + " --datadir " + mDataDir + " --nodiscover --ipcdisable --networkid " + networkid + " --port "
                + port + " --rpc --rpccorsdomain \"*\" --rpcapi " + rpcapi + " --rpcport " + rpcPort + " --miner.gastarget 4294967295";
        if (isStartMine) {
            startCmds += " --mine --unlock " + unlockAccount + " --password startTaiPassword --etherbase " + unlockAccount;
        }
        String result = "-1";
        try {

            System.out.println(startCmds);

            if (mDaemonExecutor == null) {
                CommandLine commandLine = CommandLine.parse(startCmds);
                mDaemonExecutor = new DaemonExecutor();
                mDaemonExecutor.setWatchdog(new ExecuteWatchdog(-1));
                mDaemonExecutor.execute(commandLine, new ExecuteResultHandler() {

                    @Override
                    public void onProcessFailed(ExecuteException e) {
                        System.out.println("onProcessFailed");
                    }

                    @Override
                    public void onProcessComplete(int exitValue) {
                        System.out.println("onProcessComplete");
                    }
                });
                result = "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping(value = "/api/chain/stopTai", method = RequestMethod.GET)
    public String stopTai() {
        if (mDaemonExecutor != null) {
            String local_host = InetAddress.getLoopbackAddress().getHostAddress();
            String local_host_port = environment.getProperty("local.server.port");
            String host = "http://" + local_host + ":" + local_host_port;
            String url = host + "/api/Union/disposableEvent";
            RestTemplate restTemplate=new RestTemplate();
            String resultGet = restTemplate.getForObject(url, String.class);

            mDaemonExecutor.getWatchdog().destroyProcess();
            mDaemonExecutor = null;
            return "0";
        } else {
            return "-1";
        }
    }

    @RequestMapping(value = "/api/chain/newAccount", method = RequestMethod.POST)
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
                    return result;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping(value = "/api/chain/initAllInOne", method = RequestMethod.POST)
    public String initAllInOne(@RequestBody String body) {

        //newAccount
        //initGenesis
        //initTai
        //addMiner
        //startTai
        //getOwnenode
        //addEnode
        //deployCCCContract
        //addContract
        //return

        Map<String, String> resultMap = new HashMap<>();

        try {
            if (StringUtils.isEmpty(body)) {
                resultMap.put("status", "-1");
                throw new Exception("body is empty");
            }
            JSONObject mapBody = JSON.parseObject(body);
            if (! (mapBody.containsKey("companyName") && mapBody.containsKey("email"))) {
                resultMap.put("status", "-1");
                throw new Exception("company info is empty");
            }

            String companyName = mapBody.getString("companyName");
            String email = mapBody.getString("email");
            String remark = "";
            if (mapBody.containsKey("remark")) {
                remark = mapBody.getString("remark");
            }

            String local_host = InetAddress.getLoopbackAddress().getHostAddress();
            String local_host_port = environment.getProperty("local.server.port");
            String host = "http://" + local_host + ":" + local_host_port;

            String url = host + "/api/chain/newAccount";
            String password = RandomStringUtils.randomAlphabetic(16);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("password", password);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(map),headers);

            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String account = result.getBody();
            if (StringUtils.isEmpty(account)) {
                resultMap.put("status", "-1");
                throw new Exception("newAccount error");
            }

            resultMap.put("status", "1");
            resultMap.put("account", "0x" +account);
            resultMap.put("password", password);


            url = host + "/api/chain/initGenesis";
            map.clear();
            //chainId period genesisAllocAccount
            int chainId = new Random().nextInt(10000) + 20000;
            map.put("chainId", chainId);
            map.put("period", 15);
            map.put("genesisAllocAccount", account);

            entity = new HttpEntity<String>(JSON.toJSONString(map),headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "2");

            url = host + "/api/chain/initTai";
            String resultGet = restTemplate.getForObject(url, String.class);
            System.out.println(resultGet);
            resultMap.put("status", "3");

            url = host + "/api/addMiner";
            entity = new HttpEntity<String>("0x" + account, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "4");


            url = host + "/api/chain/startTai";
            map.clear();
            map.put("unlockAccount", "0x" + account);
            map.put("password", password);
            map.put("port", "30345");
            map.put("rpcPort", "8567");
            map.put("verbosity", "0");
            entity = new HttpEntity<String>(JSON.toJSONString(map),headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "5");

            Thread.sleep(1000);

            url = host + "/api/admin/getOwnenode";
            resultGet = restTemplate.getForObject(url, String.class);
            if (StringUtils.isEmpty(resultGet)) {
                throw new Exception("getOwnenode error");
            }
            String enode = resultGet;
            resultMap.put("enode", enode);
            resultMap.put("status", "6");

            url = host + "/api/addEnode";
            entity = new HttpEntity<String>(enode, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "7");

            url = host + "/api/Union/deployCCC";
            map.clear();
            map.put("companyName", companyName);
            map.put("email", email);
            map.put("remark", remark);
            map.put("enode", enode);

            entity = new HttpEntity<String>(JSON.toJSONString(map), headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String contractAddress = result.getBody();
            System.out.println(contractAddress);
            resultMap.put("status", "8");
            resultMap.put("contractAddress", contractAddress);

            url = host + "/api/addContract";
            entity = new HttpEntity<String>(contractAddress, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            resultMap.put("status", "0");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return JSON.toJSONString(resultMap);
        }
    }
    @RequestMapping(value = "/api/chain/downConfig")
    public String initAllInOne() {
        //jar cvf config.jar enode.json contract.json genesis.json miner.json
        String local_host = InetAddress.getLoopbackAddress().getHostAddress();
        String local_host_port = environment.getProperty("local.server.port");
        String host = "http://" + local_host + ":" + local_host_port;

        {
            File files = new File("files");
            if (!files.exists()) {
                files.mkdir();
            }
        }

        CommandLine commandLine = CommandLine.parse("jar cvf ./files/config.zip enode.json contract.json genesis.json miner.json");
        DaemonExecutor daemonExecutor = new DaemonExecutor();

        try {
            int result = daemonExecutor.execute(commandLine);
            if (result == 0) {
                return host + "/files/config.zip";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/files/config.zip")
    public void getConfigJar(HttpServletRequest request, HttpServletResponse response) {
        File file = new File("./files/config.zip");
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=config.zip");// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fileInputStream = null;
            BufferedInputStream bufferedInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                OutputStream os = response.getOutputStream();
                int i = bufferedInputStream.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bufferedInputStream.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequestMapping(value = "/api/chain/initMemberAllInOne", method = RequestMethod.POST)
    public String initMemberAllInOne(@RequestBody String body) {
        //newAccount
        //addGenesis
        //initTai
        //addMiner
        //addEnode
        //addContract

        //startTai
        //getOwnenode
        //addEnode
        //addPeer
        //loadCCC
        //return
        Map<String, String> resultMap = new HashMap<>();
        try {
            if (StringUtils.isEmpty(body)) {
                resultMap.put("status", "-1");
                throw new Exception("body is empty");
            }
            JSONObject mapBody = JSON.parseObject(body);
            if (! (mapBody.containsKey("genesisJson") && mapBody.containsKey("creator_account")
                    && mapBody.containsKey("creator_enode") && mapBody.containsKey("contract_address"))) {
                resultMap.put("status", "-1");
                throw new Exception("some parameter is empty");
            }

            String genesisJson = mapBody.getString("genesisJson");
            String creator_account = mapBody.getString("creator_account");
            String creator_enode = mapBody.getString("creator_enode");
            String contract_address = mapBody.getString("contract_address");

            String local_host = InetAddress.getLoopbackAddress().getHostAddress();
            String local_host_port = environment.getProperty("local.server.port");
            String host = "http://" + local_host + ":" + local_host_port;

            String url = host + "/api/chain/newAccount";
            String password = RandomStringUtils.randomAlphabetic(16);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("password", password);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(map),headers);

            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String account = result.getBody();
            if (StringUtils.isEmpty(account)) {
                resultMap.put("status", "-1");
                throw new Exception("newAccount error");
            }

            resultMap.put("status", "1");
            resultMap.put("account", account);
            resultMap.put("password", "0x" + password);

            url = host + "/api/chain/addGenesis";
            entity = new HttpEntity<String>(genesisJson,headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "2");

            url = host + "/api/chain/initTai";
            String resultGet = restTemplate.getForObject(url, String.class);
            System.out.println(resultGet);
            resultMap.put("status", "3");

            url = host + "/api/addMiner";
            entity = new HttpEntity<String>(creator_account, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "4");

            url = host + "/api/addEnode";
            entity = new HttpEntity<String>(creator_enode, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "5");

            url = host + "/api/addContract";
            entity = new HttpEntity<String>(contract_address, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "6");

            url = host + "/api/chain/startTai";
            map.clear();
            map.put("unlockAccount", "0x" + account);
            map.put("password", password);
            map.put("port", "30345");
            map.put("rpcPort", "8567");
            map.put("verbosity", "0");
            entity = new HttpEntity<String>(JSON.toJSONString(map),headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "7");

            url = host + "/api/admin/getOwnenode";
            resultGet = restTemplate.getForObject(url, String.class);
            if (StringUtils.isEmpty(resultGet)) {
                throw new Exception("getOwnenode error");
            }
            String enode = resultGet;
            resultMap.put("enode", enode);
            resultMap.put("status", "8");

            url = host + "/api/addEnode";
            entity = new HttpEntity<String>(enode, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "9");

            url = host + "/api/admin/addPeer";
            entity = new HttpEntity<String>(creator_enode, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(result.getBody());
            resultMap.put("status", "10");

            map.clear();
            map.put("contractAddress", contract_address);
            url = host + "/api/Union/loadCCC";
            entity = new HttpEntity<String>(JSON.toJSONString(map), headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String contractAddress = result.getBody();
            System.out.println(contractAddress);
            resultMap.put("status", "0");
            resultMap.put("contractAddress", contractAddress);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return JSON.toJSONString(resultMap);
        }
    }
}
