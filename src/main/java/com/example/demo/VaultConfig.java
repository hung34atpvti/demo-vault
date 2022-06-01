package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
public class VaultConfig implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(VaultConfig.class);

    ApplicationContext context;

    @Autowired
    SecretLeaseContainer container;

    @Autowired
    ConfigurationPropertiesRebinder configurationPropertiesRebinder;

    @Autowired
    Environment env;

    @Autowired
    ConfigurableEnvironment configurableEnvironment;

    @PostConstruct
    private void postConstruct() {
        String vaultCredsPath = "secret/demo";
        logger.info("Register lease listener");
        container.addLeaseListener(leaseEvent -> {
            RequestedSecret requestedSecret = leaseEvent.getSource();

            if (!requestedSecret.getPath().equals(vaultCredsPath)) {
                return;
            }

            if (leaseEvent instanceof SecretLeaseExpiredEvent && requestedSecret.getMode().equals(RequestedSecret.Mode.RENEW)) {
                container.requestRotatingSecret(vaultCredsPath);
            }

            if (leaseEvent instanceof SecretLeaseCreatedEvent && requestedSecret.getMode().equals(RequestedSecret.Mode.ROTATE)) {
                SecretLeaseCreatedEvent event = (SecretLeaseCreatedEvent) leaseEvent;
                Env credentials = getCredentials(event);
                if (credentials == null) {
                    return;
                }
                logger.info("Updated Secret");
                updateEnv(credentials);
            }
        });
    }

    private Env getCredentials(SecretLeaseCreatedEvent createdEvent) {
        Map<String, Object> secrets = createdEvent.getSecrets();
        if (secrets.isEmpty()) {
            return null;
        }

        String username = (String) createdEvent.getSecrets().get("demo.username");
        String password = (String) createdEvent.getSecrets().get("demo.password");
        String port = (String) createdEvent.getSecrets().get("demo.port");

        return new Env(username, password, port);
    }

    private void updateEnv(Env credentials) {
        System.setProperty("demo.username", credentials.getUsername());
        System.setProperty("demo.password", credentials.getPassword());
        System.setProperty("demo.port", credentials.getPort());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
