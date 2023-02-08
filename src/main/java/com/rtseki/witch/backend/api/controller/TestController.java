package com.rtseki.witch.backend.api.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rtseki.witch.backend.commom.AuthenticationFacade;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/test-controller")
public class TestController {

    @Autowired
    private AuthenticationFacade authenticationFacade;
	
	@GetMapping
	public ResponseEntity<String> sayHello(HttpServletRequest request) {
		Authentication authentication = authenticationFacade.getAuthentication();
		Principal principal = request.getUserPrincipal();
		return ResponseEntity.ok("Hello from secured endpoint " + principal.getName() + " " + authentication.getName());
	}
}