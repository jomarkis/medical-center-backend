package com.medicalcenter.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdminLoginRequest.class, name = "ADMIN"),
        @JsonSubTypes.Type(value = DoctorLoginRequest.class, name = "DOCTOR"),
        @JsonSubTypes.Type(value = PatientLoginRequest.class, name = "PATIENT")
})
public abstract class LoginRequest {
}