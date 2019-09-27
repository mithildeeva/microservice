package com.thoughtmechanix.licenses.model;

import lombok.Data;

@Data
public class License {
    String id;
    String productName;
    String type;
    String orgId;
}
