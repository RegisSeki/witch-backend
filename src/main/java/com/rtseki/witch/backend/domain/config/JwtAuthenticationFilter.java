package com.rtseki.witch.backend.domain.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rtseki.witch.backend.domain.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request, 
		@NonNull HttpServletResponse response, 
		@NonNull FilterChain filterChain
	)throws ServletException, IOException {
		try {
			final String authHeader = request.getHeader("Authorization");
			final String jwtToken;
			final String userEmail;
			
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			}
			
			jwtToken = authHeader.substring(7);
			userEmail = jwtService.extractUsername(jwtToken);
						
			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
				
				if (jwtService.isTokenValid(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userEmail,
						null,
						userDetails.getAuthorities()
					);
					
					authToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
					);
					
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}catch(Exception e) {
			handleInvalidJwt(response);
			return;
		}
		
		filterChain.doFilter(request, response);
	}
	
	  private void handleInvalidJwt(HttpServletResponse response) throws IOException {
	    response.setContentType("application/json");
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.getWriter().write("Token not valid");
	  }
}
