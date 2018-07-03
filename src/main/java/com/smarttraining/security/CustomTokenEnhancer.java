package com.smarttraining.security;

import com.smarttraining.entity.User;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    UserService userService;
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
            OAuth2Authentication authentication) {

        final Map<String, Object> additionalInfo = new HashMap<>();
        try {
            User user = userService.getUser(authentication.getName());
            additionalInfo.put("user_id", user.getId());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        } catch (UserNotFoundException e) {

        }

        return accessToken;
    }

}
