package com.eventcart.service;

import com.eventcart.dto.UserDto;
import com.eventcart.entity.Role;
import com.eventcart.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User business logic and authentication.
 */
public interface UserService {

    User registerUser(String email, String plainPassword, String fullName, String phone, Role role);

    Optional<User> authenticate(String email, String plainPassword);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean isEmailAvailable(String email);

    User updateProfile(Long userId, String fullName, String phone);

    void changePassword(Long userId, String currentPassword, String newPassword);

    List<UserDto> getAllUsers();

    void toggleUserStatus(Long userId, boolean active);
}
