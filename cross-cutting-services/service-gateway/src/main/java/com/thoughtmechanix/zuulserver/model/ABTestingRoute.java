package com.thoughtmechanix.zuulserver.model;

import lombok.Data;

@Data
public class ABTestingRoute {
    String serviceName;
    String active;
    String endpoint;
    Integer weight;
}
