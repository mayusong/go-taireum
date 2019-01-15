package com.taireum.ccc;

import org.junit.Test;

public class GenesisUtilTest {

    @Test
    public void initTaiGenesis() {
        String genesisJson = GenesisUtil.initTaiGenesis(40602, 15, new String[]{"ce439e8f2b733bcabf19f8cd2cd296f9a9b421e9"});
        System.out.println(genesisJson);
    }
}