package com.taireum.ccc;

import org.junit.Test;

import static org.junit.Assert.*;

public class GenesisUtilsTest {

    @Test
    public void initTaiGenesis() {
        String genesisJson = GenesisUtils.initTaiGenesis(40602, 15, new String[]{"ce439e8f2b733bcabf19f8cd2cd296f9a9b421e9"});
        System.out.println(genesisJson);
    }
}