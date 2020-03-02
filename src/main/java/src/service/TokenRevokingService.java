package src.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import src.model.RevokedToken;
import src.repository.RevokedTokenRepository;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@Service
@PropertySource(value = "application.properties")
public class TokenRevokingService {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    private String getJWTSignature(final String jwtInBase64) throws NoSuchAlgorithmException {
        byte[] cipheredToken = DatatypeConverter.parseBase64Binary(jwtInBase64);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] cipheredTokenDigest = digest.digest(cipheredToken);

        return DatatypeConverter.printHexBinary(cipheredTokenDigest);
    }


    public boolean isTokenRevoked(final String jwtInBase64) throws Exception {
        boolean tokenIsPresent = false;
        log.error("TOKEN : " + jwtInBase64 + "\n SIGN : " + getJWTSignature(jwtInBase64));
        if (jwtInBase64 != null && !jwtInBase64.trim().isEmpty()) {
            tokenIsPresent = revokedTokenRepository.existsById(getJWTSignature(jwtInBase64));
        }

        return tokenIsPresent;
    }

    public void revokeToken(final String jwtInBase64) throws Exception {
        if (jwtInBase64 != null && !jwtInBase64.trim().isEmpty()) {
            String signature = getJWTSignature(jwtInBase64);
            if (!this.isTokenRevoked(signature)) {
                RevokedToken revokedToken = new RevokedToken();
                revokedToken.setJwtTokenDigest(signature);
                revokedToken.setRevocationDate(LocalDateTime.now());

                revokedTokenRepository.save(revokedToken);
                if (!revokedTokenRepository.existsById(revokedToken.getJwtTokenDigest())) {
                    throw new IllegalStateException(String.format("Token digest %s wasn't saved!",
                            signature));
                }
            }
        }
    }

}