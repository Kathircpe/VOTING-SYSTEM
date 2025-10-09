package com.kathir.demo.service;

import com.kathir.demo.configuration.Web3jConfiguration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import org.web3j.protocol.core.DefaultBlockParameter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;


public class BlockChainService {


    private final Web3jConfiguration config=new Web3jConfiguration();

    final Web3j web3j = config.client();

    public BigDecimal getBalance() throws Exception{
        EthGetBalance balanceResponse = web3j.ethGetBalance(Web3jConfiguration.address,
                        DefaultBlockParameter.valueOf("latest"))
                .sendAsync()
                .get(10, TimeUnit.SECONDS);

        BigInteger unScaledBalance = balanceResponse.getBalance();
        return  new BigDecimal(unScaledBalance)
                .divide(new BigDecimal(10000000000000000L), 18, RoundingMode.HALF_UP);
    }


}
