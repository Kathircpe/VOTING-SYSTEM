package com.kathir.demo.controller;

import com.kathir.demo.modules.Admin;
import com.kathir.demo.modules.Voter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.kathir.demo.service.AdminService;

@Data
@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
public  class AdminController {
    private final  int pageSize =10;
        @Autowired
        private final AdminService adminService;

        @GetMapping("/getVoters")
        private Page<Voter> getVoters(@RequestParam int page){
            return adminService.getAllVoters(page,pageSize);
        }

        @GetMapping("/getAdmins")
        private Page<Admin> getAdmins(@RequestParam int page){
            return adminService.getAllAdmins(page,pageSize);
        }

        @GetMapping("/admin{id}")
        private Admin getAdminById(@PathVariable long id){

            return adminService.getAdminById(id);
        }
        @GetMapping("{id}")
        private Voter getVoterById(@PathVariable long id){
            return adminService.getVoterById(id);
        }

        @PostMapping("/addVoter")
        public void addNewVoter(@RequestBody Voter voter){
            adminService.addNewVoter(voter);

        }

        @PutMapping("/updateVoter/{id}")
        public void updateVoter(@PathVariable long id,@RequestBody Voter voter){
            adminService.updateVoter(id,voter);
        }
        @DeleteMapping("/delete/{id}")
        public void deleteVoter(@PathVariable long id){
            adminService.deleteVoter(id);
        }


}
