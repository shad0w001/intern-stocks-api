package com.internship.stocks_api.repositories;

import com.internship.stocks_api.models.OAuthAccount;
import com.internship.stocks_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<OAuthAccount> findByUser(User user);
}
