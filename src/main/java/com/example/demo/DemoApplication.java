package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@RefreshScope
@Component("MyVaultConfiguration")
public class DemoApplication {

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RequestMapping("/env")
    public Env getEnv() {
        System.out.println("/env");
        var username = environment.getProperty("demo.username");
        var password = environment.getProperty("demo.password");
        Env env = new Env();
        env.setUsername(username);
        env.setPassword(password);
        return env;
    }
}
