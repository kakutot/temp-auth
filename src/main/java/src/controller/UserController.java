package src.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import src.repository.UserRoleRepository;
import src.repository.UserSecuredRepository;
import src.model.UserRole;
import src.model.UserSecured;

import javax.validation.Valid;
import javax.ws.rs.core.Application;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {

    //Could be extracted into separate service
    @Autowired private UserSecuredRepository userSecuredRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserSecured>> findAllUsers() {
        ResponseEntity<List<UserSecured>> responseEntity;

        try {
            Iterable<UserSecured> iterable = userSecuredRepository.findAll();
            responseEntity = ResponseEntity.ok(StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                    iterable.iterator(),
                    Spliterator.ORDERED),
                    false)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserSecured> findByUsername(@PathVariable(name = "username") final String username) {
        ResponseEntity<UserSecured> responseEntity;

        try {
            Optional<UserSecured> userSecured = userSecuredRepository.findFirstByUsername(username);
            responseEntity = userSecured.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
        } catch (Exception e) {
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }
    /*
    @RequestMapping(produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user.getUserAuthentication().getPrincipal());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        return userInfo;
    }*/

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserSecured userSecured, Errors errors) throws URISyntaxException {
        if (errors.hasErrors()) {
            String errorsMsg = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("/"));

            return ResponseEntity.badRequest().body(errorsMsg);
        }

        if (userSecuredRepository.findFirstByUsername(userSecured.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Duplicate username");
        }
        UserSecured saved;
        Set<UserRole> userRoleSet = new HashSet<>(userSecured.getUserRoles());
        userSecured.getUserRoles().clear();
        try {
            Set<UserRole> finalUserRoles = new HashSet<>();
            userRoleSet.forEach(userRole -> {
               Optional<UserRole> userRoleInDb = userRoleRepository.findFirstByName(userRole.getName());
                userRoleInDb.ifPresent(finalUserRoles::add);
            });
            userSecured.setPassword(passwordEncoder.encode(userSecured.getPassword()));
            userSecured.setUserRoles(finalUserRoles);
            saved = userSecuredRepository.save(userSecured);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.created(new URI("/user/" + userSecured.getId())).body(saved);
    }

    /*
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserSecured userSecured,
                                        @PathVariable Long id,
                                        Errors errors,
                                        OAuth2Authentication authentication) {
        log.error("auth" + authentication);
        Optional<UserSecured> userInDb = userSecuredRepository.findById(id);
        Optional<UserSecured> currentUser = userSecuredRepository.findFirstByUsername(authentication.getPrincipal().toString());

        //TODO custom method handler
        if (userInDb.isPresent() && userInDb.get().getId().equals(currentUser.get().getId())) {
            if (errors.hasErrors()) {
                String errorsMsg = errors.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining("/"));

                return ResponseEntity.badRequest().body(errorsMsg);
            }

            UserSecured saved;
            Set<UserRole> userRoleSet = new HashSet<>(userSecured.getUserRoles());
            userSecured.getUserRoles().clear();
            try {
                Set<UserRole> finalUserRoles = new HashSet<>();
                userRoleSet.forEach(userRole -> {
                    Optional<UserRole> userRoleInDb = userRoleRepository.findFirstByName(userRole.getName());
                    userRoleInDb.ifPresent(finalUserRoles::add);
                });
                userSecured.setPassword(passwordEncoder.encode(userSecured.getPassword()));
                userSecured.setUserRoles(finalUserRoles);
                saved = userSecuredRepository.save(userSecured);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    */

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<UserSecured> userSecured = userSecuredRepository.findById(id);
        if (userSecured.isPresent()) {
            try {
                userSecuredRepository.deleteById(id);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}
