package com.thoughtmechanix.licenses.controller;

import com.thoughtmechanix.licenses.model.License;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/organizations/{orgId}/licenses")
public class LicenseController {

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
}
