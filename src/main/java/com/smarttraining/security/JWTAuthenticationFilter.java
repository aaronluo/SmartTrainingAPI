package com.smarttraining.security;

import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.User;
import com.smarttraining.util.JWTUtil;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.jwt.header:Authorization}")
    String jwtHeader;
    
    @Value("${app.jwt.prefix:bearer }")
    String jwtPrefix;
    
    @Autowired
    Util util;
    
    @Autowired
    JWTUtil jwtUtil;
    
    @Autowired
    UserDao userDao;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = request.getHeader(jwtHeader);
        if(!util.isEmpty(token) && token.startsWith(jwtPrefix)) {
           token = token.substring(jwtPrefix.length());
           String username = jwtUtil.parseUsername(token);
           LocalDateTime expirationDate = jwtUtil.parseExpirationDate(token);
           
           User user = userDao.findByUsername(username).orElse(null);
           if(null != user || expirationDate.isAfter(LocalDateTime.now())){
               //TODO: 后期要加入roles
               UsernamePasswordAuthenticationToken authentication = 
                       new UsernamePasswordAuthenticationToken(username, null, null);
               
               authentication.setDetails(new WebAuthenticationDetailsSource()
                       .buildDetails(request));
               
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
        }
        
        filterChain.doFilter(request, response);
    }

}
