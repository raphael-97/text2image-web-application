package com.noCompany.BackendStableDiffusionWebApp.jwtutils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
@Getter
public class RSAKeys {
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RSAKeys() throws NoSuchAlgorithmException {
        KeyPair keyPair;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }
}
