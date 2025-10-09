package com.kathir.demo.service;

import lombok.Data;
import com.kathir.demo.modules.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.kathir.demo.repository.AdminRepository;

@Data
@Service
public class SuperAdminService {
    @Autowired
    private final AdminRepository adminRepository;

    public Page<Admin> getAllAdmins(int page, int size){
        Pageable pageable= PageRequest.of(page,size);
        return adminRepository.findAll(pageable);
    }



    public Admin getAdminById(long id) {
        return adminRepository.findById(id).orElseThrow(()-> new IllegalStateException(id+" not found"));
    }


    public void addNewAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public void updateAdmin(Admin admin) {
        adminRepository.save(admin);

    }

    public void deleteAdmin(long id) {
        adminRepository.delete(getAdminById(id));
    }
}
