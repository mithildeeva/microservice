package com.thoughtmechanix.licenses.eureka.clients;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate discoveryTemplate;

    public Organization getOrganization(String orgId) {
        ResponseEntity<Organization> restExchange =
                discoveryTemplate.exchange(
                        "http://organizationservice/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null,
                        Organization.class, orgId);
        return restExchange.getBody();
    }
}
