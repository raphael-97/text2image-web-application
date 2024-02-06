package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserResponse;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterRequest;
import com.noCompany.BackendStableDiffusionWebApp.enums.Provider;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import com.noCompany.BackendStableDiffusionWebApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with username %s not in users database", username)
                ));
    }

    public User registerUser(RegisterRequest registerRequest) {
        User newUser = mapRegisterDtoToUser(registerRequest);
        if (registerRequest.getProvider() == Provider.self) {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        } else {
            newUser.setProvider(registerRequest.getProvider());
        }
        newUser.setRoles(Set.of(Role.USER));
        return userRepository.save(newUser);
    }

    public UserResponse findbyUsername(String username) {
        Optional<User> UserByEmail = userRepository.findByUsername(username);
        if (UserByEmail.isPresent()) {
            return mapUserToUserDto(UserByEmail.get());
        }
        throw new UsernameNotFoundException(String.format("User with username %s not in users database", username));
    }

    public User getUserByEmail(String email) {
        Optional<User> UserByEmail = userRepository.findByEmail(email);
        if (UserByEmail.isPresent()) {
            return UserByEmail.get();
        }
        throw new UsernameNotFoundException(String.format("User with email %s not in users database", email));
    }


    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User mapRegisterDtoToUser(RegisterRequest registerDto) {
        return User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .credits(5L)
                .build();
    }
    private UserResponse mapUserToUserDto(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .credits(user.getCredits())
                .build();
    }
}
