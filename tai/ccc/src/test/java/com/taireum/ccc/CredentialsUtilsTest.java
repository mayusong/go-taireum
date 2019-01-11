package com.taireum.ccc;

import org.junit.Test;


public class CredentialsUtilsTest {

    @Test
    public void checkValidAccount() {
    }

    @Test
    public void getAccountFilePath() {
//        String accountPath = CredentialsUtils.getAccountFilePath("tai_data_dir", "0xce439e8f2b733bcabf19f8cd2cd296f9a9b421e9");
//        System.out.println("accountPath:" + accountPath);
    }

    @Test
    public void getCredentials() {
//        String accountPath = CredentialsUtils.getAccountFilePath("tai_data_dir", "0xce439e8f2b733bcabf19f8cd2cd296f9a9b421e9");
//        Credentials credentials = CredentialsUtils.getCredentials("123", accountPath);
//        System.out.println(credentials.getAddress());
//        System.out.println(credentials.getEcKeyPair().getPrivateKey().toString());
//        System.out.println(credentials.getEcKeyPair().getPublicKey().toString());
    }

    @Test
    public void getAllAccountAddress() {
        String accounts = CredentialsUtils.getAllAccountAddress("tai_data_dir");
        System.out.println(accounts);
    }
}