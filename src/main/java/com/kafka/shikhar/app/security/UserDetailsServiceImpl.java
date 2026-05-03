package com.kafka.shikhar.app.security;

import com.kafka.shikhar.app.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security {@link UserDetailsService} implementation.
 *
 * <p>Spring Security calls {@link #loadUserByUsername(String)} during the
 * authentication flow.  We treat the user's <em>email</em> as the username.
 *
 * <p>This service intentionally has no business logic – it is a pure adapter
 * between our DAO layer and Spring Security's authentication machinery.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    /**
     * Load a user by their email address.
     *
     * @param email the principal identifier (email)
     * @return the matching {@link UserDetails} (our {@code UserEntity} implements it)
     * @throws UsernameNotFoundException if no user with the given email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("UserDetailsService → loading user by email={}", email);
        return userDao.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("UserDetailsService → user not found for email={}", email);
                    return new UsernameNotFoundException("No user found with email: " + email);
                });
    }
}
