package com.kathir.demo.controller;

import lombok.Data;
import com.kathir.demo.modules.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.kathir.demo.service.SuperAdminService;

@Data
@RestController
@RequestMapping("/api/v1/superAdmin")
public class SuperAdminController {
    private final  int pageSize =10;
    @Autowired
    private final SuperAdminService superAdminService;


    @GetMapping("/getAdmins")
    private Page<Admin> getAdmins(@RequestParam int page){
        return superAdminService.getAllAdmins(page,pageSize);
    }

    @GetMapping("/admin{id}")
    private Admin getAdminById(@PathVariable long id){

        return superAdminService.getAdminById(id);
    }

    @PostMapping("/addAdmin")
    public void addNewAdmin(@RequestBody Admin admin){
        superAdminService.addNewAdmin(admin);

    }

    @PutMapping("/updateAdmin")
    public void updateAdmin(@RequestBody Admin admin){
        superAdminService.updateAdmin(admin);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteAdmin(@PathVariable long id){
        superAdminService.deleteAdmin(id);
    }
}
