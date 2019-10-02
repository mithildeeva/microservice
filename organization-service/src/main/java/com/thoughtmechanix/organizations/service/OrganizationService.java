package com.thoughtmechanix.organizations.service;

import com.thoughtmechanix.organizations.model.Organization;
import com.thoughtmechanix.organizations.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    public Organization getOrg(String organizationId) {
        return orgRepository.findByOrgId(organizationId);
    }

    public void saveOrg(Organization org){
        org.setOrgId( UUID.randomUUID().toString());

        orgRepository.save(org);

    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
    }

    public void deleteOrg(Organization org){
        orgRepository.delete(org);
    }

}
