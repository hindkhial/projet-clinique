package com.ehei.patient.config;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Converter JPA pour chiffrer/déchiffrer automatiquement les données sensibles
 * Utilise AES pour le chiffrement
 */
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    // ATTENTION: À remplacer par une clé stockée en variable d'environnement
    // La clé doit faire exactement 16 bytes pour AES-128
    private static final byte[] KEY = "VotreCle16Bytes!".getBytes();

    /**
     * Chiffre la donnée avant de la stocker en base
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Erreur de chiffrement", e);
        }
    }

    /**
     * Déchiffre la donnée lors de la lecture depuis la base
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Erreur de déchiffrement", e);
        }
    }
}

