package gestion.extrait.api

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import java.security.Key

class JwtTokenUtil {

    static final long EXPIRATION = 86400000 // 24h
    static final String SECRET = "gestion_extrait_secret_key_très_longue_2024"

    static Key getSigningKey() {
        byte[] keyBytes = SECRET.padRight(32, '0').bytes
        return Keys.hmacShaKeyFor(keyBytes)
    }

    static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.email)
                .claim("role", user.role.toString())
                .claim("id", user.id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact()
    }

    static String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
    }

    static String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String)
    }

    static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
            return true
        } catch (Exception e) {
            return false
        }
    }

}