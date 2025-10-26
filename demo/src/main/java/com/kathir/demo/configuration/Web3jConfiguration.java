package com.kathir.demo.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3jConfiguration {



    // Config
    @Bean
    public Web3j web3j(@Value("${web3.rpc-url}") String rpcUrl) {
        return Web3j.build(new HttpService(rpcUrl));
    }

    @Bean
    public Credentials credentials(@Value("${web3.private-key}") String privateKey) throws Exception {
        return Credentials.create(privateKey);
    }

    @Bean
    public DefaultGasProvider gasProvider() {
        return new DefaultGasProvider();
    }


}
