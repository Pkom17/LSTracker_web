package org.itech.labSampleTracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String header = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			try {
				var jws = jwtService.parse(token);
				var body = jws.getBody();
				String username = body.getSubject();
				String role = String.valueOf(body.get("role"));
				var auth = new UsernamePasswordAuthenticationToken(username, null,
						List.of(new SimpleGrantedAuthority(role)));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception ignored) {
				// invalide/expiré -> laisser sans auth, 401 sera renvoyé par la config
			}
		}
		chain.doFilter(req, res);
	}
}
