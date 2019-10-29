package com.thoughtmechanix.organizations.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizationChange {
    private String type;
    private String action;
    private String orgId;
    private String correlationId;
}
