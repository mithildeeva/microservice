package com.thoughtmechanix.licenses.service;

import com.thoughtmechanix.licenses.eureka.clients.OrganizationDiscoveryClient;
import com.thoughtmechanix.licenses.eureka.clients.OrganizationFeignClient;
import com.thoughtmechanix.licenses.eureka.clients.OrganizationRestTemplateClient;
import com.thoughtmechanix.licenses.model.License;
import com.thoughtmechanix.licenses.model.Organization;
import com.thoughtmechanix.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseService {

    private LicenseRepository licenseRepo;
    private ServiceConfig config;
    private OrganizationDiscoveryClient discoveryClient;
    private OrganizationRestTemplateClient restTemplateClient;
    private OrganizationFeignClient feignClient;


    @Autowired
    public LicenseService(
            LicenseRepository licenseRepo,
            ServiceConfig config,
            OrganizationDiscoveryClient discoveryClient,
            OrganizationRestTemplateClient restTemplateClient,
            OrganizationFeignClient feignClient
    ) {
        this.licenseRepo = licenseRepo;
        this.config = config;
        this.discoveryClient = discoveryClient;
        this.restTemplateClient = restTemplateClient;
        this.feignClient = feignClient;
    }

    public License getLicense(String orgId, String id) {
        License license = licenseRepo.findByOrOrgIdAndId(orgId, id);
        license.setComment(config.getExampleProperty());
        return license;
    }

    public License getLicense(String orgId, String id, String clientType) {
        License license = licenseRepo.findByOrOrgIdAndId(orgId, id);
        license.setComment(config.getExampleProperty());

        Organization org = getOrg(orgId, clientType);
        if (null == org) return license;
        license.setOrganizationName(org.getName());
        license.setContactName(org.getContactName());
        license.setContactEmail(org.getContactEmail());
        license.setContactPhone(org.getContactPhone());

        return license;
    }

    private Organization getOrg(String orgId, String clientType) {
        switch (clientType) {
            case "discoveryclient":
                return discoveryClient.getOrganization(orgId);
            case "resttemplate":
                return restTemplateClient.getOrganization(orgId);
            case "neflixfeignclient":
                return feignClient.getOrganization(orgId);
            default:
                return null;
        }
    }

    public List<License> getLicensesByOrg(String orgId){
        return licenseRepo.findByOrgId(orgId);
    }

    public void saveLicense(License license){
        license.setId( UUID.randomUUID().toString());
        licenseRepo.save(license);
    }
}
