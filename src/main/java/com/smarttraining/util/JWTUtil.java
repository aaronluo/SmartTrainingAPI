package com.smarttraining.util;

import com.smarttraining.entity.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {
    @Value("${app.jwt.secret:smart-training}")
    protected String jwtSecret;
    
    public String composeJwtToken(User user) {
        LocalDateTime expirationDate = LocalDateTime.now().plusHours(24);
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("user_id", user.getId());
        claims.put("user_create_date", user.getCreateDate().toString());
        
        return Jwts.builder().setClaims(claims)
                .setSubject(user.getUsername())
                .setExpiration(Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }
    
    public String parseUsername(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    
    public LocalDateTime parseExpirationDate(String token) {
        Date expirationDate = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
        
        return LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
    }
    
}
