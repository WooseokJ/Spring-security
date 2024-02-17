package com.cos.security.oauth;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.config.SecurityConfig;
import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
//    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 패스워드 만들기위함.
    private final UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에대한 후처리 되는 함수.
    // loadUser함수 종료시 @AuthenticationPrincipal 어노테이션 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // userRequest는 엑세스토큰, 사용자정보까지 받았다.
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration());
        System.out.println("getAccessToken = " + userRequest.getAccessToken().getTokenValue());
        // 구글 로그인 버튼클릭 -> 구글로그인 창 -> 로그인 완료 -> code 리턴(OAuth-Client라이브러리가 받음) -> AccessToken을 요청해서 받음.
        // userRequest정보 -> loadUser함수통해 회원프로필받음 ->
        System.out.println("getAuthorities = " + super.loadUser(userRequest).getAttributes());


        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("attribute = " + oAuth2User.getAttributes());

        // 위정보들 통해 회원가입진행
        String provider = userRequest.getClientRegistration().getClientId();// google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider+"_"+providerId;

//        String pw = bCryptPasswordEncoder.encode("아무거나2");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        // 회원가입 유무여부 판단.
        User userObject = userRepository.findByUsername(username);// 회원가입 유무여부 판단.
        if(userObject == null) { // 유저 없으므로 회원가입
            System.out.println("구글로그인 으로 최초 회원가입");
            // 강제로 회원가입
            userObject = User.builder()
                    .username(username)

                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userObject);
        } else {
            System.out.println("구글 로그인한적있어.");
        }

        return new PrincipalDetails(userObject, oAuth2User.getAttributes()); // oauth 로그인하므로 user객체, attribute 를 같이 만들어줌.
    }
}



/*
getClientRegistration = ClientRegistration{
			registrationId='google',
			clientId='875268640376-svi5ool7u51ck2pp5ee77pt25qhsarbq.apps.googleusercontent.com',
			clientSecret='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',
			clientAuthenticationMethod=client_secret_basic,
			authorizationGrantType=org.springframework.security.oauth2.core.AuthorizationGrantType@5da5e9f3,
			redirectUri='{baseUrl}/{action}/oauth2/code/{registrationId}', scopes=[email, profile],
			providerDetails=org.springframework.security.oauth2.client.registration.ClientRegistration$ProviderDetails@2781bda9,
			clientName='Google'
}
getAccessToken = ya29.a0AfB_byDObTBCEcoI0_bif7Rz6eas4Sga-D8h4aog4zHwsEpu1E7pdYz3kQ2Cw-9vs2TFQ_AkfCRK_Bgmhp2AJjkl1jHqWUS7jBj9E_x_Iaj7EP6ZlJmUP-Ilm7aL7m7MwGsEfAAJPIsnhHYAvCWz4tV2MmDl1caltd7eaCgYKAd4SARASFQHGX2Mijpuf3nzFK1-oQVAVMrcCtQ0171
getAuthorities = {
		sub=116427779241289398325,
		name=Wooseok Cho,
		given_name=Wooseok,
		family_name=Cho,
		picture=https://lh3.googleusercontent.com/a/ACg8ocIrBVgVPYAZVb77z6IkcEiiXlLABg_NeWvxLpAOlYNl=s96-c,
		email=wooseokbird@gmail.com,
		email_verified=true,
		locale=ko
}

// User class 보면 username, password, email, role, provider, providerId 가 잇는데
// 아래 정보로 로그인
// username = "google_116427779241289398325"
// pw = "암호화(겟인데어)"
// email = "wooseokbird@gmail.com"
// role = "ROLE_USER"
// provider = "google"
// providerId = 116427779241289398325
* */