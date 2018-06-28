package com.smarttraining.config;

import com.smarttraining.security.SecurityUserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class AppSecurityCfg extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return new SecurityUserService();
    }
    
//    @Autowired
//    public void globalUserDetails(final AuthenticationManagerBuilder auth)
//            throws Exception {
//        auth.userDetailsService(userDetailsService())
//            .passwordEncoder(passwordEncoder());
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .cors().and().csrf().disable()
//            .formLogin().disable()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//            .anonymous().and()
//            .authorizeRequests()
//            .antMatchers("/api/users/register").anonymous()
//            .antMatchers( "/oauth/**","/tokens/**", "/api-docs/**").permitAll()
//            .anyRequest().authenticated();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
