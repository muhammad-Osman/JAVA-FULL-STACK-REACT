package com.usman.controller;

import com.usman.enums.RoleEnums;
import com.usman.models.Role;
import com.usman.models.Users;
import com.usman.payload.request.LogOutRequest;
import com.usman.payload.request.LoginRequest;
import com.usman.payload.request.SignupRequest;
import com.usman.payload.response.AuthResponse;
import com.usman.payload.response.MessageResponse;
import com.usman.repository.RoleRepository;
import com.usman.repository.UserRepository;
import com.usman.service.impl.UserDetailsImpl;
import com.usman.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private  AuthenticationManager authenticationManager;

  @Autowired
  private  UserRepository userRepository;

  @Autowired
  private  RoleRepository roleRepository;

  @Autowired
  private  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getId(),
        userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

    if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    Users user = new Users(signUpRequest.getFirstName(),
            signUpRequest.getLastName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(RoleEnums.USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "ADMIN":
            Role adminRole = roleRepository.findByName(RoleEnums.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
          break;
        case "USER":
            Role userRole = roleRepository.findByName(RoleEnums.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
          break;
          default:
            throw new RuntimeException("Error: Role is not found.");

        }
      });
    }
    user.setRoles(roles);
    userRepository.save(user);

    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(),
                    signUpRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> rolesList = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
            .collect(Collectors.toList());

    return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), rolesList));
  }

}