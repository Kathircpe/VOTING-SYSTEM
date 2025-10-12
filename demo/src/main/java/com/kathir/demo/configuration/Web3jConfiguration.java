package com.kathir.demo.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3jConfiguration {

     public static final String address="0x00000000219ab540356cBB839Cbe05303d7705Fa";

    // Config
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("https://mainnet.infura.io/v3/3a40f40937c64c64bb77fe936fee178c"));
    }

    @Bean
    public Credentials credentials() throws Exception {
        return WalletUtils.loadCredentials("<password>", "<path-to-keystore-or-pk>"); // or use RawTransactionManager
    }
    @Bean
    public DefaultGasProvider gasProvider(){
        return new DefaultGasProvider();
    }




}
