package com.internship.stocks_api.services.oauth2;

import com.internship.stocks_api.dtos.auth.LoginResponseDto;
import com.internship.stocks_api.errors.OAuthErrors;
import com.internship.stocks_api.models.OAuthAccount;
import com.internship.stocks_api.models.User;
import com.internship.stocks_api.repositories.OAuthAccountRepository;
import com.internship.stocks_api.repositories.UserRepository;
import com.internship.stocks_api.security.JwtTokenProvider;
import com.internship.stocks_api.shared.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oauthAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Result<LoginResponseDto> loginOrRegister(String provider, String providerUserId, String email) {
        try {
            if (provider == null || providerUserId == null) {
                return Result.failure(OAuthErrors.authProblem("Provider and providerUserId must not be null"));
            }

            User user = null;

            var oauthAccountOpt = oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);
            if (oauthAccountOpt.isPresent()) {
                user = oauthAccountOpt.get().getUser();
            } else {
                if (email != null) {
                    user = userRepository.findByEmail(email).orElse(null);
                }

                if (user == null) {
                    user = new User();
                    //in case the user does not have a public email (depends on provider) I create a dummy one
                    user.setEmail(email != null ? email : createDummyEmail(provider, providerUserId));
                    user.setCreatedAt(LocalDateTime.now());
                    userRepository.save(user);
                }

                OAuthAccount oauthAccount = new OAuthAccount();
                oauthAccount.setUser(user);
                oauthAccount.setProvider(provider);
                oauthAccount.setProviderUserId(providerUserId);
                oauthAccount.setCreatedAt(LocalDateTime.now());
                oauthAccountRepository.save(oauthAccount);
            }

            String jwt = jwtTokenProvider.generateToken(user.getId());
            return Result.success(new LoginResponseDto(jwt, "Bearer", user.getId()));

        } catch (Exception ex) {
            return Result.failure(OAuthErrors.authProblem(ex.getMessage()));
        }
    }

    private String createDummyEmail(String provider, String providerUserId){
        return provider + "_" + providerUserId + "@dummy.com";
    }
}