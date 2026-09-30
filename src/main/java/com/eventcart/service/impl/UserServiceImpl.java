package com.eventcart.service.impl;

import com.eventcart.dao.UserDao;
import com.eventcart.dao.impl.UserDaoImpl;
import com.eventcart.dto.UserDto;
import com.eventcart.entity.Role;
import com.eventcart.entity.User;
import com.eventcart.exception.ResourceNotFoundException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.UserService;
import com.eventcart.util.PasswordUtil;
import com.eventcart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for User business operations.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User registerUser(String email, String plainPassword, String fullName, String phone, Role role) {
        logger.info("Attempting to register user with email: {}", email);

        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Invalid email address format.");
        }
        if (!ValidationUtil.isStrongPassword(plainPassword)) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!ValidationUtil.isNotBlank(fullName)) {
            throw new ValidationException("Full name is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format.");
        }

        String cleanedEmail = email.trim().toLowerCase();
        if (userDao.existsByEmail(cleanedEmail)) {
            throw new ValidationException("An account with this email already exists.");
        }

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User user = new User();
        user.setEmail(cleanedEmail);
        user.setPasswordHash(hashedPassword);
        user.setFullName(fullName.trim());
        user.setPhoneNumber(ValidationUtil.clean(phone));
        user.setRole(role != null ? role : Role.CUSTOMER);
        user.setActive(true);

        User saved = userDao.save(user);
        logger.info("Successfully registered user id: {}, email: {}, role: {}", saved.getId(), saved.getEmail(), saved.getRole());
        return saved;
    }

    @Override
    public Optional<User> authenticate(String email, String plainPassword) {
        if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.isNotBlank(plainPassword)) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDao.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            logger.warn("Authentication failed: email '{}' not found.", email);
            return Optional.empty();
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            logger.warn("Authentication failed: user '{}' is deactivated.", email);
            return Optional.empty();
        }

        if (PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            logger.info("User '{}' authenticated successfully.", email);
            return Optional.of(user);
        } else {
            logger.warn("Authentication failed: invalid password for user '{}'.", email);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userDao.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public boolean isEmailAvailable(String email) {
        if (!ValidationUtil.isValidEmail(email)) return false;
        return !userDao.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public User updateProfile(Long userId, String fullName, String phone) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!ValidationUtil.isNotBlank(fullName)) {
            throw new ValidationException("Full name is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format.");
        }

        user.setFullName(fullName.trim());
        user.setPhoneNumber(ValidationUtil.clean(phone));
        return userDao.update(user);
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!PasswordUtil.checkPassword(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }

        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        userDao.update(user);
        logger.info("Password successfully changed for user id: {}", userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleUserStatus(Long userId, boolean active) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setActive(active);
        userDao.update(user);
        logger.info("User id {} status set to active={}", userId, active);
    }
}
