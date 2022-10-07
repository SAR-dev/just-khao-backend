package just.khao.com.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import just.khao.com.entity.AuthEntity;
import just.khao.com.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JwtAuthConfiguration extends OncePerRequestFilter {
    @Value("${jwt.accesstoken.secretkey}")
    private String accessTokenSecret;

    private final AuthService authService;

    public JwtAuthConfiguration(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String access_token;
        Boolean expired = false;
        AuthEntity authEntity = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            access_token = authorizationHeader.substring(7);
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(accessTokenSecret)).build();
            email = verifier.verify(access_token).getSubject();
            expired = verifier.verify(access_token).getExpiresAt().before(new Date());
            authEntity = authService.findByEmail(email);
        }

        if (email != null && !expired && authEntity != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new User(
                    authEntity.getEmail(),
                    authEntity.getHashed_password(),
                    new ArrayList<>()
            );
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
