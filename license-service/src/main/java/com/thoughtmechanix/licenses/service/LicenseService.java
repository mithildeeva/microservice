package com.thoughtmechanix.licenses.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.thoughtmechanix.licenses.eureka.clients.OrganizationDiscoveryClient;
import com.thoughtmechanix.licenses.eureka.clients.OrganizationFeignClient;
import com.thoughtmechanix.licenses.eureka.clients.OrganizationRestTemplateClient;
import com.thoughtmechanix.licenses.model.License;
import com.thoughtmechanix.licenses.model.Organization;
import com.thoughtmechanix.licenses.repository.LicenseRepository;
import com.thoughtmechanix.licenses.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /*
    * When the Spring framework sees the @HystrixCommand,
    * it will dynamically generate a proxy that will wrapper the method
    * and manage all calls to that method through a thread pool of threads
    * specifically set aside to handle remote calls WITH circuit breakers.
    * */
    @HystrixCommand(
            /*
            * customize the behavior of the circuit breaker
            * */
            commandProperties = {
                    @HystrixProperty(
                            /*
                            * Circuit-breaker timeout limit
                            * */
                            name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "2000" // milliseconds
                    )
            },
            /*
            * defines a single function in your
            * class that will be called if the call from Hystrix fails.
            * */
            fallbackMethod = "fallbackForGetLicensesByOrg",
            /*
            * Bulkhead
            * By default hysterix uses a common connection pool (of size 10)
            * to wrap methods. This can starve services if a single one is called alot,
            * and takes alot of time.
            * To prevent it we can assing threadpool at class/method level
            * uniquely identified by threadPoolKey
            * */
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "20"),
                    /*
                    * Threadpool's queue (if all threads are busy)
                    * */
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
            }
    )
    public List<License> getLicensesByOrg(String orgId) {
        Utility.randomlySleep(11, 3);
        return licenseRepo.findByOrgId(orgId);
    }

    /**
     * 1. fallback method must have same definition as the original method
     * 2. If making call to another external entity here,
     * it should be wrapped in another HystrixCommand
     */
    public List<License> fallbackForGetLicensesByOrg(String orgId) {
        List<License> licenses = new ArrayList<>();

        License license = new License();
        license.setId("0000-00000-00000");
        license.setOrgId(orgId);
        license.setProductName("No info available (message from fallback)");

        licenses.add(license);
        return licenses;
    }

    public void saveLicense(License license){
        license.setId( UUID.randomUUID().toString());
        licenseRepo.save(license);
    }
}
