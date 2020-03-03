package src.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import src.service.TokenRevokingService;

@Slf4j
@Controller
@RequestMapping(value = "/revoke-token")
public class TokenRevokeController {

    @Autowired
    TokenRevokingService tokenRevokingService;

    @GetMapping(value = "/{jwtInHex}")
    public ResponseEntity<?> isRevoked(@PathVariable final String jwtInHex, OAuth2Authentication auth2Authentication) {
        try {
            return ResponseEntity.ok(tokenRevokingService.isTokenRevoked(jwtInHex));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Revoke token == logout on client side
    @PostMapping(value = "")
    public ResponseEntity<?> revokeToken(@RequestBody TokenV tokenV) {
        log.info("revokeToken" + tokenV.token);
        try {
            tokenRevokingService.revokeToken(tokenV.token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @Data
    public static class TokenV {
        String token;
    }
}

