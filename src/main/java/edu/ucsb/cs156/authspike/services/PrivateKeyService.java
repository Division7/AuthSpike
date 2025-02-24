package edu.ucsb.cs156.authspike.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class PrivateKeyService {
    @Value("${app.private.key}")
    private String privateKey;

    @Value("${app.client.id}")
    private String clientId;

    public RSAPrivateKey getPrivateKey()  {
        try {
            String key = new String(Files.readAllBytes(Path.of("./pkcs8.key")), Charset.defaultCharset());
            key = key.replace("-----BEGIN PRIVATE KEY-----", "");
            key = key.replace("-----END PRIVATE KEY-----", "");
            key = key.replaceAll(System.lineSeparator(), "");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String getClientId(){
        System.out.println(clientId);
        return clientId;
    }
}
