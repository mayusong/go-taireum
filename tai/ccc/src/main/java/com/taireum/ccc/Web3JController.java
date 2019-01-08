package com.taireum.ccc;

import com.taireum.AdminAddPeer;
import com.taireum.AdminNodeInfo;
import com.taireum.JsonRpc2_0Tai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

@RestController
public class Web3JController {
    @Autowired
    private RpcConfig mRpcConfig;
    private static JsonRpc2_0Tai tai = null;
    private JsonRpc2_0Tai getTaiInstance() {
        if (tai == null) {
            String mRpcAddr = "http://" + mRpcConfig.getRpcAddr() + ":" + mRpcConfig.getRpcPort();
            tai = new JsonRpc2_0Tai(new HttpService(mRpcAddr));
        }
        return tai;
    }

    @RequestMapping(value="/api/admin/addPeer", method=RequestMethod.POST)
    public String addPeer(@RequestBody String body) {
        try {
            AdminAddPeer adminAddPeer = getTaiInstance().addPeer(body).send();
            return adminAddPeer.addPeer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value="/api/admin/getOwnenode", method = RequestMethod.GET)
    public String getOwnenode() {
        try {
            AdminNodeInfo adminNodeInfo = getTaiInstance().getNodeInfo().send();
            return adminNodeInfo.getNodeInfo().getEnode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
