package com.kathir.demo.configuration;


import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfiguration {

     public static final String address="0x00000000219ab540356cBB839Cbe05303d7705Fa";

    public Web3j client() {
        return  Web3j.build(
                new HttpService(
                        "https://mainnet.infura.io/v3/3a40f40937c64c64bb77fe936fee178c"
                )
        );
    }



}
