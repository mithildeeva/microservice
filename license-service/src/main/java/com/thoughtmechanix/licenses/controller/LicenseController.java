package com.thoughtmechanix.licenses.controller;

import com.thoughtmechanix.licenses.model.License;
import com.thoughtmechanix.licenses.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/organizations/{orgId}/licenses")
public class LicenseController {

    @Autowired
    LicenseService service;

    @GetMapping(value="/{licenseId}")
    public License getLicense(
            @PathVariable("orgId") String orgId,
            @PathVariable("licenseId") String licenseId) {
        License license = new License();
        license.setId(licenseId);
        license.setProductName("Teleco");
        license.setType("Seat");
        license.setOrgId(orgId);
        return license;
    }

    // 3 eureka clients

    // 1st is Spring Discovery Client (with ordinary RestTemplate, minimum abstraction)
    @GetMapping(value = "/{licenseId}/discoveryclient")
    public License getLicensesWithDiscoveryClient(
            @PathVariable("orgId") String orgId,
            @PathVariable("licenseId") String licenseId
    ) {
        return service.getLicense(orgId, licenseId, "discoveryclient");
    }

    // 2nd is enhanced RestTemplate
    @GetMapping(value = "/{licenseId}/resttemplate")
    public License getLicensesWithRestTemplate(
            @PathVariable("orgId") String orgId,
            @PathVariable("licenseId") String licenseId
    ) {
        return service.getLicense(orgId, licenseId, "resttemplate");
    }

    // 3rd is Netflix Feign Client (maximum abstraction)
    @GetMapping(value = "/{licenseId}/neflixfeignclient")
    public License getLicensesWithFeignClient(
            @PathVariable("orgId") String orgId,
            @PathVariable("licenseId") String licenseId
    ) {
        return service.getLicense(orgId, licenseId, "neflixfeignclient");
    }
}
