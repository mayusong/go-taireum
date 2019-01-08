package com.taireum.ccc;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpcconfig")
public class RpcConfig {
    private String rpcPort;
    private String rpcAddr;
    private String account;
    private String password;
    private String dataDir;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
}

