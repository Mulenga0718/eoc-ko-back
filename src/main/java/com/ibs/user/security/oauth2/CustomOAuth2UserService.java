package com.ibs.user.security.oauth2;

import com.ibs.user.domain.User;
import com.ibs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = registerNewUser(oAuth2UserInfo, registrationId);
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase("naver")) {
            return new NaverOAuth2UserInfo((Map<String, Object>) attributes.get("response"));
        }
        throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        User user = User.builder()
                .username(registrationId + "_" + oAuth2UserInfo.getProviderId()) // Use username field
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .password("") // Social login users might not have a password
                .provider(registrationId)
                .providerId(oAuth2UserInfo.getProviderId())
                .build();
        return userRepository.save(user);
    }
}
