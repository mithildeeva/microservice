package com.thoughtmechanix.organizations.service;

import com.thoughtmechanix.organizations.event.source.SimpleSourceBean;
import com.thoughtmechanix.organizations.model.Organization;
import com.thoughtmechanix.organizations.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private SimpleSourceBean sourceBean;

    public Organization getOrg(String organizationId) {
        return orgRepository.findByOrgId(organizationId);
    }

    public void saveOrg(Organization org){
        org.setOrgId( UUID.randomUUID().toString());

        orgRepository.save(org);

        sourceBean.publishOrChange("SAVE", org.getOrgId());
    }

    public void updateOrg(Organization org){
//        orgRepository.save(org);

        sourceBean.publishOrChange("UPDATE", org.getOrgId());
    }

    public void deleteOrg(String orgId){
        Organization org = new Organization();
        org.setOrgId(orgId);
        orgRepository.delete(org);

        sourceBean.publishOrChange("DELETE", orgId);
    }

}
