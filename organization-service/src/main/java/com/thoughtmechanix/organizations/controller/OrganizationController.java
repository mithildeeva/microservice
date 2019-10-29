package com.thoughtmechanix.organizations.controller;

import com.thoughtmechanix.organizations.model.Organization;
import com.thoughtmechanix.organizations.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="v1/organizations")
public class OrganizationController {
    @Autowired
    private OrganizationService orgService;


    @GetMapping(value="/{organizationId}")
    public Organization getOrganization(@PathVariable("organizationId") String organizationId) {
        return orgService.getOrg(organizationId);
    }

    @PutMapping(value="/{organizationId}")
    public void updateOrganization(@PathVariable("organizationId") String orgId, @RequestBody Organization org) {
        orgService.updateOrg( org );
    }

    @PostMapping(value="/{organizationId}")
    public void saveOrganization(@RequestBody Organization org) {
        orgService.saveOrg( org );
    }

    @DeleteMapping(value="/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganization( @PathVariable("orgId") String orgId) {
        orgService.deleteOrg( orgId );
    }
}
