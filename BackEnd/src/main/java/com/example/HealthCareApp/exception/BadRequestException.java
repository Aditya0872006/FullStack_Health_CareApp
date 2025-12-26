package com.example.HealthCareApp.exception;

public class BadRequestException extends RuntimeException
{
    public BadRequestException(String ex)
    {
        super(ex);
    }
}
