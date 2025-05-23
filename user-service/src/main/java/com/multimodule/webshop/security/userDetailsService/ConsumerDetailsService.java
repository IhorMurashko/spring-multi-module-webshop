package com.multimodule.webshop.security.userDetailsService;

import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.repositories.ConsumerJpaRepository;
import com.multimodule.webshop.security.userDetails.SecurityModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerDetailsService implements UserDetailsService {

    private final ConsumerJpaRepository consumerJpaRepository;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Consumer> consumer = consumerJpaRepository.findByUsername(username);

        if (consumer.isPresent()) {
            return new SecurityModel(consumer.get());
        } else {
            log.error("Consumer with email: {} not found", username);

            throw new UsernameNotFoundException(String.format("Consumer with email: %s not found", username));
        }

    }
}
