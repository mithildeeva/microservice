package com.thoughtmechanix.organizations.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "organizations")
@Data
public class Organization {
    @Id
    @Column(name = "organization_id", nullable = false)
    String orgId;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "contact_name", nullable = false)
    String contactName;

    @Column(name = "contact_email", nullable = false)
    String contactEmail;

    @Column(name = "contact_phone", nullable = false)
    String contactPhone;
}
