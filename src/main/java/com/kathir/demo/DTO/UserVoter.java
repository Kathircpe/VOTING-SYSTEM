package com.kathir.demo.DTO;

import com.kathir.demo.models.Voter;

public class UserVoter {
    public UserVoter(){

    }
    public UserVoter(Voter voter){
        this.id= voter.getId();
        this.name= voter.getName();
        this.age= voter.getAge();
        this.hasVoted= voter.isHasVoted();
        this.isEnabled= voter.isEnabled();
        this.email= voter.getEmail();
        this.phoneNumber= voter.getPhoneNumber();
        this.voterAddress= voter.getVoterAddress();


    }
    public Long id;
  
    public String name;

    public Integer age;

    public boolean hasVoted;

    public boolean isEnabled;
   
    public String email;
   
    public String phoneNumber;
   
    public String voterAddress; // Blockchain wallet address
}