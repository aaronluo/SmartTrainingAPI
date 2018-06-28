package com.smarttraining.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;


@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtResourceServerCfg extends ResourceServerConfigurerAdapter {

    @Autowired
    TokenStore tokenStore;
    
    @Autowired
    ResourceServerTokenServices tokenServices;
    
    
//    @Autowired
//    @Qualifier("authenticationManagerBean")
//    private AuthenticationManager authenticationManager;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
//        resources
//            .tokenStore(tokenStore).tokenServices(tokenServices)
//            .authenticationManager(authenticationManager)
//            .stateless(true);
        resources.tokenServices(tokenServices);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .cors().and().csrf().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .anonymous().and()
            .authorizeRequests().antMatchers("/api/users/register", "/api-docs", "/swagger-ui.html").permitAll().and()
            .authorizeRequests().anyRequest().authenticated();
//            .anonymous().disable()
//            .authorizeRequests()
//            .antMatchers("/api/users/login", "/api/users/register").permitAll()
//            .antMatchers( "/oauth/**","/tokens/**", "/api-docs/**", "/swagger-ui.html").permitAll()
//            .anyRequest().authenticated();
//        
//        http.addFilterBefore(
//                new CheckSecurityContextFilter(), UsernamePasswordAuthenticationFilter.class);
//        super.configure(http);
    }
    
}
