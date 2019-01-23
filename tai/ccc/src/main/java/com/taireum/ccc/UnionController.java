package com.taireum.ccc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taireum.CCC.CCCGasProvider;
import com.taireum.CCC.CCC_sol_CCC;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
public class UnionController {
    @Autowired
    private RpcConfig mRpcConfig;
    private static Web3j mWeb3j = null;
    private static Credentials mCredentials = null;
    private String mDataDir = "tai_data_dir";
    private CCC_sol_CCC ccc_sol_ccc = null;
    private EthFilter ethFilter = null;
    private Disposable disposable = null;

    private Web3j getWeb3jInstance() {
        if (mWeb3j == null) {
            String rpcAddr = "http://" + mRpcConfig.getRpcAddr() + ":" + mRpcConfig.getRpcPort();
            mWeb3j = Web3j.build(new HttpService(rpcAddr));
        }
        return mWeb3j;
    }


    private Credentials getCredentials() {
        if (mCredentials == null) {
            String accountPath = CredentialsUtil.getAccountFilePath(mDataDir, mRpcConfig.getAccount());
            mCredentials = CredentialsUtil.getCredentials(mRpcConfig.getPassword(), accountPath);
        }
        return  mCredentials;
    }

    private void subscribeCCCEvent(String contractAddress) {
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, contractAddress);

        disposable = ccc_sol_ccc.eventCCCEventFlowable(ethFilter).subscribe(eventCCCEventResponse -> {
            System.out.println("type:" + eventCCCEventResponse.EventType + " enode:" + eventCCCEventResponse._toEnode + " addr:" + eventCCCEventResponse._toAddress);
            switch (eventCCCEventResponse.EventType.intValue()) {
                case 1: //VoteMember
                {
                    //eventCCCEventResponse._toEnode
                    CCCJsonUtil.AppendToJsonArray(CCCJsonUtil.enodeJsonPath, eventCCCEventResponse._toEnode);
                    break;
                }
                case 2:{ //VoteMine
                    CCCJsonUtil.AppendToJsonArray(CCCJsonUtil.minerJsonPath, eventCCCEventResponse._toAddress);
                    break;
                }
            }
        });
    }
    @RequestMapping(value = "/api/Union/disposableEvent", method = RequestMethod.GET)
    private String disposableEvent() {
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
                System.out.println("disposable.dispose()");
                return "0";
            }
        }
        return "1";
    }

    @RequestMapping(value = "/api/Union/deployCCC", method = RequestMethod.POST)
    private String deployCCCContract(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }

        JSONObject jsonObject = JSON.parseObject(body);
        String companyName = jsonObject.getString("companyName");
        String email = jsonObject.getString("email");
        String remark = jsonObject.getString("remark");
        String enode = jsonObject.getString("enode");

        try {
            ccc_sol_ccc = CCC_sol_CCC.deploy(getWeb3jInstance(), getCredentials(), new CCCGasProvider(), companyName, email, remark, enode).send();
            String contractAddress = ccc_sol_ccc.getContractAddress();

            subscribeCCCEvent(contractAddress);

            return contractAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/loadCCC", method = RequestMethod.POST)
    private String loadCCCContract(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return "-1";
        }
        JSONObject jsonObject = JSON.parseObject(body);
        String contractAddress = jsonObject.getString("contractAddress");
        ccc_sol_ccc = CCC_sol_CCC.load(contractAddress, getWeb3jInstance(), getCredentials(), new CCCGasProvider());
        subscribeCCCEvent(contractAddress);
        return ccc_sol_ccc.getContractAddress();
    }

    @RequestMapping(value = "/api/Union/applyMember", method = RequestMethod.POST)
    private String applyMember(@RequestBody String body) {
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        JSONObject jsonObject = JSON.parseObject(body);
        String companyName = jsonObject.getString("companyName");
        String email = jsonObject.getString("email");
        String remark = jsonObject.getString("remark");
        String enode = jsonObject.getString("enode");
        String account = jsonObject.getString("account");
        try {
            TransactionReceipt transactionReceipt = ccc_sol_ccc.applyMember(companyName, email, remark, enode, account).send();
            if (transactionReceipt.isStatusOK()) {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @RequestMapping(value = "/api/Union/showCompanyNum", method = RequestMethod.GET)
    private String showCompanyNum() {
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            BigInteger sum = ccc_sol_ccc.ShowSum().send();
            return String.valueOf(sum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping(value = "/api/Union/showAllCompany", method = RequestMethod.GET)
    private String showAllCompany() {
        if (ccc_sol_ccc == null) {
            return "[]";
        }
        try {
            JSONArray jsonArray = new JSONArray();
            BigInteger sum = ccc_sol_ccc.ShowSum().send();
            //Company ID从1 开始
            for (int i = 1; i <= sum.intValue(); i++) {
                String companyDetail = showCompany(String.valueOf(i));
                jsonArray.add(companyDetail);
            }
            return jsonArray.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }

    @RequestMapping(value = "/api/Union/showCompany", method = RequestMethod.GET)
    private String showCompany(String id) {
        if (ccc_sol_ccc == null) {
            return "{}";
        }
        if (StringUtils.isEmpty(id)) {
            return "{}";
        }
        try {
            BigInteger _id = new BigInteger(id);

            Tuple6<String, String, String, String, String, BigInteger> tuple6 = ccc_sol_ccc.ShowCompany(_id).send();
            String companyName = tuple6.getValue1();
            String email = tuple6.getValue2();
            String remark = tuple6.getValue3();
            String owner = tuple6.getValue4();
            String enode = tuple6.getValue5();
            BigInteger stat = tuple6.getValue6();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("companyName", companyName);
            jsonObject.put("email", email);
            jsonObject.put("remark", remark);
            jsonObject.put("owner", owner);
            jsonObject.put("enode", enode);
            jsonObject.put("stat", stat);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @RequestMapping(value = "/api/Union/VoteMember", method = RequestMethod.GET)
    private String voteMember(String fromid, String toid) {
        if(StringUtils.isEmpty(fromid) || StringUtils.isEmpty(toid)) {
            return "-1";
        }
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            TransactionReceipt transactionReceipt = ccc_sol_ccc.VoteMember(new BigInteger(fromid), new BigInteger(toid)).send();
            if (transactionReceipt.isStatusOK()) {
                return  "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/VoteMine", method = RequestMethod.GET)
    private String voteMine(String fromid, String toid) {
        if(StringUtils.isEmpty(fromid) || StringUtils.isEmpty(toid)) {
            return "-1";
        }
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            TransactionReceipt transactionReceipt = ccc_sol_ccc.VoteMine(new BigInteger(fromid), new BigInteger(toid)).send();
            if (transactionReceipt.isStatusOK()) {
                return  "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/UpdateCompany", method = RequestMethod.POST)
    private String updateCompany(@RequestBody String body) {
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        JSONObject jsonObject = JSON.parseObject(body);
        String companyId = jsonObject.getString("companyId");
        String email = jsonObject.getString("email");
        String remark = jsonObject.getString("remark");
        String enode = jsonObject.getString("enode");
        String stat = jsonObject.getString("stat");
        try {
            TransactionReceipt transactionReceipt = ccc_sol_ccc.UpdateCompany(new BigInteger(companyId), email, remark, enode, new BigInteger(stat)).send();
            if (transactionReceipt.isStatusOK()) {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }
    @RequestMapping(value = "/api/Union/ShowBallot", method = RequestMethod.GET)
    private String showBallot(String id) {
        if(StringUtils.isEmpty(id) ) {
            return "-1";
        }
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            BigInteger num = ccc_sol_ccc.ShowBallot(new BigInteger(id)).send();
            return String.valueOf(num);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/ShowBallotMine", method = RequestMethod.GET)
    private String showBallotMine(String id) {
        if(StringUtils.isEmpty(id) ) {
            return "-1";
        }
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            BigInteger num = ccc_sol_ccc.ShowBallotMine(new BigInteger(id)).send();
            return String.valueOf(num);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/isMember", method = RequestMethod.GET)
    private String isMember(String id) {
        if(StringUtils.isEmpty(id) ) {
            return "-1";
        }
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            Boolean result = ccc_sol_ccc.isMember(new BigInteger(id)).send();
            if (result) {
                return "1";
            } else {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/isMemberMine", method = RequestMethod.GET)
    private String isMemberMine(String id) {
        if(StringUtils.isEmpty(id) ) {
            return "-1";
        }
        if (ccc_sol_ccc == null) {
            return "-1";
        }
        try {
            Boolean result = ccc_sol_ccc.isMemberMine(new BigInteger(id)).send();
            if (result) {
                return "1";
            } else {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @RequestMapping(value = "/api/Union/transfer", method = RequestMethod.GET)
    private String transfer(String toAccount, String amount) {
        if (StringUtils.isEmpty(toAccount) || StringUtils.isEmpty(amount)) {
            return "-1";
        }

        try {
            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    getWeb3jInstance(), getCredentials(), toAccount,
                    BigDecimal.valueOf(Long.parseLong(amount)), Convert.Unit.ETHER).send();
            return String.valueOf(transactionReceipt.getTransactionHash());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }
}
