package com.app.demo.Jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Permitir OPTIONS (preflight CORS) sin validar token
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// Permitir rutas de autenticación sin validar token
		String path = request.getRequestURI();
		if (path.startsWith("/auth/")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		final String token = getTokenFromRequest(request);

		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String username = null;

		try {
			username = jwtService.getUsernameFromToken(token);
		} catch (io.jsonwebtoken.ExpiredJwtException ex) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Token expired\"}");
			return; 
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Invalid token\"}");
			return;
		}

		if (username != null) {
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtService.isTokenValid(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, 
							null, 
							userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authToken);
					
					// Verificar que se estableció correctamente
					var context = SecurityContextHolder.getContext();
					var auth = context.getAuthentication();
					
					// Log para debug
					System.out.println("✅ Authentication establecida para: " + username);
					System.out.println("✅ Authorities: " + userDetails.getAuthorities());
					System.out.println("✅ SecurityContext Authentication: " + (auth != null ? auth.getName() : "null"));
					System.out.println("✅ SecurityContext Authorities: " + (auth != null ? auth.getAuthorities() : "null"));
					System.out.println("✅ Request URI: " + request.getRequestURI());
					System.out.println("✅ Request Method: " + request.getMethod());
				} else {
					// Token inválido
					System.out.println("❌ Token inválido para usuario: " + username);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.getWriter().write("{\"error\": \"Invalid token\"}");
					return;
				}
			} catch (Exception e) {
				System.out.println("❌ Error al cargar UserDetails para: " + username + " - " + e.getMessage());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\": \"Error loading user: " + e.getMessage() + "\"}");
				return;
			}
		} else {
			// No hay username pero hay token - esto no debería pasar
			System.out.println("⚠️ Token presente pero username es null");
		}

		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
}
