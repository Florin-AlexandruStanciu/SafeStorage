package com.SafeStorage.service;

import com.SafeStorage.model.User;
import com.SafeStorage.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;


@Service
@AllArgsConstructor
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findById(username).orElseThrow(()->new UsernameNotFoundException("Utilizatorul nu exista"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("rol")));
    }
}
