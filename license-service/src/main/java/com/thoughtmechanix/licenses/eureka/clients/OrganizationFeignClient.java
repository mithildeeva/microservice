package com.thoughtmechanix.licenses.eureka.clients;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
* One interface per service.
* This interface will be used to create a class that'll make the calls
* */
@FeignClient("organizationservice")
public interface OrganizationFeignClient {
    @GetMapping(value = "/v1/organizations/{organizationId}", consumes = "application/json")
    Organization getOrganization(@PathVariable("organizationId") String organizationId);
}
