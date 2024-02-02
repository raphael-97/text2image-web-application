package com.noCompany.BackendStableDiffusionWebApp.service;

import com.noCompany.BackendStableDiffusionWebApp.domain.User;
import com.noCompany.BackendStableDiffusionWebApp.dto.UserDto;
import com.noCompany.BackendStableDiffusionWebApp.dto.auth.RegisterDto;
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

    public UserDto registerUser(RegisterDto registerDto) {
        User newUser = mapRegisterDtoToUser(registerDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(Set.of("USER"));
        User savedUser = userRepository.save(newUser);
        return mapUserToUserDto(savedUser);
    }

    public UserDto findbyUsername(String username) {
        Optional<User> UserByEmail = userRepository.findByUsername(username);
        if (UserByEmail.isPresent()) {
            return mapUserToUserDto(UserByEmail.get());
        }
        throw new UsernameNotFoundException(String.format("User with username %s not in users database", username));
    }

    public UserDto getUserByEmail(String email) {
        Optional<User> UserByEmail = userRepository.findByEmail(email);
        if (UserByEmail.isPresent()) {
            return mapUserToUserDto(UserByEmail.get());
        }
        throw new UsernameNotFoundException(String.format("User with email %s not in users database", email));
    }

    private User mapRegisterDtoToUser(RegisterDto registerDto) {
        return User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .credits(5L)
                .build();
    }
    private UserDto mapUserToUserDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .credits(user.getCredits())
                .build();
    }
}
