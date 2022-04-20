package com.example.demo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration()
public class Env {
    @Value("${cre.user}")
    private String username;
    @Value("${cre.pass}")
    private String password;
}
