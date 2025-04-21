package com.multimodule.webshop.userDetailsService;

import com.multimodule.webshop.dtos.UserDto;
import com.multimodule.webshop.mapper.UserMapper;
import com.multimodule.webshop.proto.common.User;
import com.multimodule.webshop.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import user_details.UserDetailsServiceGrpc;
import user_details.Username;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserDetailsServiceGrpc.UserDetailsServiceBlockingStub userDetailsServiceBlockingStub;
    private final UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDetailsServiceBlockingStub.loadUserByUsername(
                Username.newBuilder()
                        .setUsername(username)
                        .build()
        );

        UserDto userDto = userMapper.toDto(user);
        return new CustomUserDetails(userDto);
    }
}
