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
<<<<<<< ours
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
=======
import org.springframework.beans.factory.annotation.Value;
>>>>>>> theirs
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
    private final String loginUsername;
    private final String loginPassword;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       JobApplicationRepository jobApplicationRepository,
                       EmailAnalysisRepository emailAnalysisRepository,
                       @Value("${app.login.username:admin}") String loginUsername,
                       @Value("${app.login.password:admin12345}") String loginPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.jobApplicationRepository = jobApplicationRepository;
        this.emailAnalysisRepository = emailAnalysisRepository;
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
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
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
        String username = request.getUsername().trim().toLowerCase();

        if (loginUsername.equalsIgnoreCase(username) && loginPassword.equals(request.getPassword())) {
            User defaultUser = getOrCreateDefaultUser();
            return buildAuthResponse(defaultUser);
        }

        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
=======
=======
>>>>>>> theirs
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
<<<<<<< ours
>>>>>>> theirs
=======
>>>>>>> theirs
=======
        if (!loginUsername.equalsIgnoreCase(request.getUsername()) || !loginPassword.equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        User user = getOrCreateDefaultUser();
>>>>>>> theirs
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

    public User getOrCreateDefaultUser() {
        return userRepository.findByEmailIgnoreCase(loginUsername)
                .orElseGet(() -> {
                    User user = new User();
                    user.setFullName("Resume Tracker Admin");
                    user.setEmail(loginUsername);
                    user.setPasswordHash(passwordEncoder.encode(loginPassword));
                    user.setRole(UserRole.ADMIN);
                    User savedUser = userRepository.save(user);

                    var orphanedApplications = jobApplicationRepository.findByOwnerIsNull();
                    orphanedApplications.forEach(application -> application.setOwner(savedUser));
                    if (!orphanedApplications.isEmpty()) {
                        jobApplicationRepository.saveAll(orphanedApplications);
                    }

                    var orphanedEmails = emailAnalysisRepository.findByOwnerIsNull();
                    orphanedEmails.forEach(email -> email.setOwner(savedUser));
                    if (!orphanedEmails.isEmpty()) {
                        emailAnalysisRepository.saveAll(orphanedEmails);
                    }
                    return savedUser;
                });
    }
}
