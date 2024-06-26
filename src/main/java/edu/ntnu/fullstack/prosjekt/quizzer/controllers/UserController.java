package edu.ntnu.fullstack.prosjekt.quizzer.controllers;

import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.LoginDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.MessageDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.SavedQuizAttemptDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.UserDto;
import edu.ntnu.fullstack.prosjekt.quizzer.services.UserService;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Rest Controller used for managing requests relating to user database operations.
 * Base endpoint is /api/users/
 */
@Log
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
@RestController
public class UserController {
  /**
   * Used for Dependency Injection.
   */
  private final UserService userService;

  /**
   * Used for Dependency Injection.
   *
   * @param userService The injected UserService object.
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<Page<UserDto>> searchUsers(@RequestParam String searchQuery, Pageable pageable) {
    try {
      return ResponseEntity.ok(userService.searchUsers(searchQuery, pageable));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", e);
    }
  }

  /**
   * Endpoint for registering a new user.
   *
   * @param user The user attempting registration.
   * @return A response with a status code and message. Fails if user already exists.
   */
  @PostMapping(path = "/register")
  public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
    log.info("Received register request from user: " + user);
    try {
      UserDto savedUserDto = userService.createUser(user);
      return new ResponseEntity<>(savedUserDto, HttpStatus.CREATED);

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", e);
    }
  }

  /**
   * Endpoint for signing in with a user.
   *
   * @param loginUser The sign in information of the user.
   * @return A response with a status code and message. Fails if credentials are incorrect.
   */

  @PostMapping(path = "/login")
  public ResponseEntity<MessageDto> loginUser(@RequestBody LoginDto loginUser) {
    try {
      if (userService.checkCredentials(loginUser)) {
        return ResponseEntity.ok(new MessageDto("User authenticated successfully"));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDto("Invalid credentials"));
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", e);
    }
  }

  /**
   * Endpoint for getting user information.
   *
   * @param username the chosen user to gather information from.
   * @return A response entity with either a not authorized message, or the user.
   */
  @GetMapping("/{username}")
  public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
    String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!username.equals(authenticatedUsername)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to view this information");
    }
    UserDto foundUserDto = userService.findDtoByUsername(username);
    return new ResponseEntity<>(foundUserDto, HttpStatus.OK);
  }

  /**
   * Endpoint for updating a user's information such as email, full name, and password.
   *
   * @param username the chosen user for update.
   * @param userDto  DTO containing the new information (email, full name, password).
   * @return A response entity with either a not authorized message, or ok-message.
   */

  @PatchMapping("/{username}")
  public ResponseEntity<MessageDto> updateUser(@PathVariable("username") String username, @RequestBody UserDto userDto) {
    String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!username.equals(authenticatedUsername)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDto("You are not authorized to update this information"));
    }
    try {
      boolean updated = false;
      if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
        userService.updateUserEmail(username, userDto.getEmail());
        updated = true;
      }
      if (userDto.getFullName() != null && !userDto.getFullName().isEmpty()) {
        userService.updateUserFullName(username, userDto.getFullName());
        updated = true;
      }
      if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
        userService.updateUserPassword(username, userDto.getPassword());
        updated = true;
      }

      if (!updated) {
        return ResponseEntity.badRequest().body(new MessageDto("No valid field provided for update"));
      }
      return ResponseEntity.ok(new MessageDto("User information updated successfully"));

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "An error occurred while updating the information: " + e.getMessage());
    }
  }

  @GetMapping("/quiz-attempts")
  public ResponseEntity<Page<SavedQuizAttemptDto>> getQuizAttempts(Pageable pageable) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return new ResponseEntity<>(userService.findAttemptsByUser(username, pageable), HttpStatus.OK);
  }
}

