package com.example.client.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final RestTemplate restTemplate;

    @Value("${client.gatewayBaseUrl:http://localhost:8085}")
    private String gatewayBaseUrl;

    @Value("${client.auth.enabled:false}")
    private boolean authEnabled;

    @Value("${client.auth.username:user}")
    private String authUsername;

    @Value("${client.auth.password:user}")
    private String authPassword;

    @Value("${client.auth.role:user}")
    private String authRole;

    public ClientController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/product-composite/{id}")
    public ResponseEntity<String> callProductComposite(@PathVariable Long id) {
        String url = String.format("%s/product-composite/%d", gatewayBaseUrl, id);

        HttpHeaders headers = new HttpHeaders();
        if (authEnabled) {
            headers.add("username", authUsername);
            headers.add("password", authPassword);
            headers.add("role", authRole);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}

