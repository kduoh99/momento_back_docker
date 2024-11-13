package com.hackathon.momento.global.jwt;

import com.hackathon.momento.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class TokenProvider {

    private final Key key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidityTime,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidityTime) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenValidityTime = accessTokenValidityTime;
        this.refreshTokenValidityTime = refreshTokenValidityTime;
    }

    public String createAccessToken(Member member) {
        return createToken(member, accessTokenValidityTime);
    }

    public String createRefreshToken(Member member) {
        return createToken(member, refreshTokenValidityTime);
    }

    private String createToken(Member member, long validityTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("Role", member.getRoleType().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        String roleName = claims.get("Role", String.class);

        if (roleName == null) {
            log.error("Role extraction failed.");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", Collections.singletonList(authority));
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT expired.");
        } catch (SignatureException e) {
            log.error("Invalid JWT signature.");
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.error("Invalid JWT format.");
        } catch (IllegalArgumentException e) {
            log.error("JWT is empty or blank.");
        } catch (Exception e) {
            log.error("JWT validation failed.", e);
        }

        return false;
    }
}