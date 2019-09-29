package com.thoughtmechanix.licenses.service;

import com.thoughtmechanix.licenses.model.License;
import com.thoughtmechanix.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseService {

    private LicenseRepository licenseRepo;
    private ServiceConfig config;


    @Autowired
    public LicenseService(LicenseRepository licenseRepo, ServiceConfig config) {
        this.licenseRepo = licenseRepo;
        this.config = config;
    }

    public License getLicense(String orgId, String id) {
        License license = licenseRepo.findByOrOrgIdAndId(orgId, id);
        license.setComment(config.getExampleProperty());
        return license;
    }

    public List<License> getLicensesByOrg(String orgId){
        return licenseRepo.findByOrgId(orgId);
    }

    public void saveLicense(License license){
        license.setId( UUID.randomUUID().toString());
        licenseRepo.save(license);
    }
}
