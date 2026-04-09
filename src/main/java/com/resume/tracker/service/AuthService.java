package com.resume.tracker.service;

import com.resume.tracker.dto.AuthRequest;
import com.resume.tracker.dto.AuthResponse;
import com.resume.tracker.dto.RegisterRequest;
import com.resume.tracker.dto.UserResponse;
import com.resume.tracker.model.User;
import com.resume.tracker.model.UserRole;
import com.resume.tracker.repository.EmailAnalysisRepository;
import com.resume.tracker.repository.JobApplicationRepository;
import com.resume.tracker.repository.UserRepository;
import com.resume.tracker.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JobApplicationRepository jobApplicationRepository;
    private final EmailAnalysisRepository emailAnalysisRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       JobApplicationRepository jobApplicationRepository,
                       EmailAnalysisRepository emailAnalysisRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.jobApplicationRepository = jobApplicationRepository;
        this.emailAnalysisRepository = emailAnalysisRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        boolean firstUser = userRepository.count() == 0;

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(firstUser ? UserRole.ADMIN : UserRole.USER);
        user = userRepository.save(user);

        if (firstUser) {
            User createdUser = user;
            var orphanedApplications = jobApplicationRepository.findByOwnerIsNull();
            orphanedApplications.forEach(application -> application.setOwner(createdUser));
            if (!orphanedApplications.isEmpty()) {
                jobApplicationRepository.saveAll(orphanedApplications);
            }

            var orphanedEmails = emailAnalysisRepository.findByOwnerIsNull();
            orphanedEmails.forEach(email -> email.setOwner(createdUser));
            if (!orphanedEmails.isEmpty()) {
                emailAnalysisRepository.saveAll(orphanedEmails);
            }
        }

        return buildAuthResponse(user);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        return buildAuthResponse(user);
    }

    public UserResponse me(User user) {
        return toUserResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        org.springframework.security.core.userdetails.User securityUser =
                new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(), java.util.List.of());
        AuthResponse response = new AuthResponse();
        response.setToken(jwtService.generateToken(securityUser));
        response.setUser(toUserResponse(user));
        return response;
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        return response;
    }
}
