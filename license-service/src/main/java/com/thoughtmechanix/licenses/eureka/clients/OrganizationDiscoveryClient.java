package com.thoughtmechanix.licenses.eureka.clients;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrganizationDiscoveryClient {
    @Autowired
    private DiscoveryClient discoveryClient;

    // least abstracted version (never used, just for understanding)
    public Organization getOrganization(String orgId) {
        /*
        * Creating instance and not injecting cz the
        * RestTemplates in Spring container are enhanced
        * (when Spring Boot sees Discovery Client enabled, it enhances them)
        * */
        RestTemplate restTemplate = new RestTemplate();
        // getting all organizationservices using eureka client
        List<ServiceInstance> instances = discoveryClient.getInstances("organizationservice");

        if (instances.isEmpty()) return null;
        String serviceUri = String.format("%s/v1/organizations/%s", instances.get(0).getUri().toString(), orgId);

        // making the request
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null,
                        Organization.class, orgId);
        return restExchange.getBody();
    }
}
