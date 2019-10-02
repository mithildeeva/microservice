package com.thoughtmechanix.licenses.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "licenses")
public class License {
    @Id
    @Column(name = "license_id", nullable = false)
    String id;

    @Column(name = "organization_id", nullable = false)
    String orgId;

    @Column(name = "product_name", nullable = false)
    String productName;

    @Column(name = "license_type", nullable = false)
    private String type;

    @Column(name = "license_max", nullable = false)
    private Integer max;

    @Column(name = "license_allocated", nullable = false)
    private Integer allocated;

    @Column(name="comment")
    private String comment;

    @Transient
    private String organizationName ="";

    @Transient
    private String contactName ="";

    @Transient
    private String contactPhone ="";

    @Transient
    private String contactEmail ="";
}
