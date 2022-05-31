# Springboot + Spring cloud Vault + Vault

## Install Vault
Please go to this link to install Vault: https://learn.hashicorp.com/tutorials/vault/getting-started-install?in=vault/getting-started

## Starting the Vault Server
**Start Vault Server**

To start the Vault dev server, run:
```
 vault server --dev --dev-root-token-id="00000000-0000-0000-0000-000000000000"
```

Copy and run the
```
export VAULT_ADDR="http://127.0.0.1:8200"
```

This will configure the Vault client to talk to the dev server.

**Verify server**

Verify the Server is Running:
```
vault status
```

**Adding Key-Value into Vault**
```
vault kv put secret/demo demo.username=demouser demo.password=demopassword
```

**Check the secret**
```
vault kv get secret/demo
```

## Using a Springboot Application

**Starting the spring app:**

```
mvn spring-boot:run
```

**Get the KV**

```
curl locahost:3099/env
```

**Testing when update KV**

Update the KV by command above.

See the log and recall: **locahost:3099/env** to know how Vault work.

