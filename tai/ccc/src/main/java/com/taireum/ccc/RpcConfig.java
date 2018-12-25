package com.taireum.ccc;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpcconfig")
public class RpcConfig {
    private String rpcPort;
    private String rpcAddr;

    public String getRpcAddr() {
        return rpcAddr;
    }

    public void setRpcAddr(String rpcAddr) {
        this.rpcAddr = rpcAddr;
    }


    public String getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }


}
