package com.example.HealthCareApp.res;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.security.cert.CertPathBuilder;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private int statusCode;
    private String message;
    private T data;


}