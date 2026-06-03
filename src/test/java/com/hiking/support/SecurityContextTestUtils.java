package com.hiking.support;

import com.hiking.entity.User;
import com.hiking.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Test helper for populating the Spring SecurityContext with a {@link CustomUserDetails}
 * principal, matching how the production services read the current user.
 */
public final class SecurityContextTestUtils {

    private SecurityContextTestUtils() {
    }

    /**
     * Authenticate as the given user with the supplied authorities (e.g. "ROLE_USER", "ROLE_ADMIN").
     */
    public static void authenticate(User user, String... authorities) {
        CustomUserDetails principal = new CustomUserDetails(user);
        List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
