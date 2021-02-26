package com.arogut.homex.data.auth;

import lombok.Value;

@Value
public class SubjectAuthTypePair {

    String subject;
    AuthType authType;
}
