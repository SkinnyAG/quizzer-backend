package edu.ntnu.fullstack.prosjekt.quizzer.services.impl;

import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.LoginDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.QuizAttemptDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.SavedQuizAttemptDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.dto.UserDto;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.entities.QuizAttemptEntity;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.entities.UserEntity;
import edu.ntnu.fullstack.prosjekt.quizzer.mappers.Mapper;
import edu.ntnu.fullstack.prosjekt.quizzer.repositories.AttemptRepository;
import edu.ntnu.fullstack.prosjekt.quizzer.repositories.UserRepository;
import edu.ntnu.fullstack.prosjekt.quizzer.services.UserService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * A class implementing the methods specified in its interface.
 * Provides services between user requests and database operations.
 */
@Log
@Service
public class UserServiceImpl implements UserService {

  /**
   * Used for Dependency Injection.
   */
  private UserRepository userRepository;

  /**
   * Used for Dependency Injection.
   */
  private AttemptRepository attemptRepository;

  /**
   * Used for Dependency Injection.
   */
  private Mapper<UserEntity, UserDto> userMapper;

  /**
   * Used for Dependency Injection.
   */
  private PasswordEncoder passwordEncoder;

  /**
   * Used for Dependency Injection.
   *
   * @param userRepository  The injected UserRepository object.
   * @param passwordEncoder The injected PasswordEncoder object,
   *                        used for salting and hashing passwords.
   */
  public UserServiceImpl(UserRepository userRepository,
                         Mapper<UserEntity, UserDto> userMapper, PasswordEncoder passwordEncoder,
                         AttemptRepository attemptRepository) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.attemptRepository = attemptRepository;
  }


  /**
   * Method for checking a loginDTOs credentials against a user.
   *
   * @param userToBeChecked the user to be checked.
   * @return true if hashed + salted password match, false if not.
   */
  @Override
  public Boolean checkCredentials(LoginDto userToBeChecked) {
    try {
      UserEntity userEntity = findEntityByUsername(userToBeChecked.getUsername());

      if (userEntity == null) {
        throw new UsernameNotFoundException("User not found with username: " + userToBeChecked.getUsername());
      }
      return passwordEncoder.matches(userToBeChecked.getPassword(), userEntity.getPassword());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Creates a user and hashes its password before storing it in the database.
   *
   * @param userDto The UserDto object to be added.
   * @return The created UserDto object.
   */
  @Override
  public UserDto createUser(UserDto userDto) {
    if (!userExists(userDto)) {
      UserEntity userEntity = userMapper.mapFrom(userDto);
      String hashedPassword = passwordEncoder.encode(userEntity.getPassword());
      userEntity.setPassword(hashedPassword);
      UserEntity savedUserEntity = userRepository.save(userEntity);
      return userMapper.mapTo(savedUserEntity);
    }
    throw new IllegalArgumentException("User already exists");
  }

  /**
   * Checks if a user exists in the database.
   *
   * @param userDto The UserDto object to check for.
   * @return A true of false value for the user existing.
   */
  @Override
  public Boolean userExists(UserDto userDto) {
    UserEntity userEntity = userMapper.mapFrom(userDto);
    return userRepository.existsById(userEntity.getUsername());
  }

  /**
   * Finds a user by its unique username.
   *
   * @param username Username of the user to locate.
   * @return A Dto representing the found user.
   */
  @Override
  public UserDto findDtoByUsername(String username) {
    if (userRepository.findById(username).isPresent()) {
      UserEntity foundUserEntity = userRepository.findById(username).get();
      return userMapper.mapTo(foundUserEntity);
    }
    return null;
  }

  /**
   * Finds a user by its unique username.
   *
   * @param username Username of the user to locate.
   * @return A Dto representing the found user.
   */
  @Override
  public UserEntity findEntityByUsername(String username) {
    if (userRepository.findById(username).isPresent()) {
      return userRepository.findById(username).get();
    }
    return null;
  }

  /**
   * Searches for users based on a search query.
   *
   * @param searchQuery The search query.
   * @param pageable    The pageable object.
   * @return A page of users.
   */
  @Override
  public Page<UserDto> searchUsers(String searchQuery, Pageable pageable) {
    Page<UserEntity> users = userRepository.findAllByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(searchQuery, searchQuery, pageable);
    ModelMapper mapper = new ModelMapper();
    return users.map(obj -> mapper.map(obj, UserDto.class));
  }

  /**
      * Updates the email address of a user.
      *
      * @param username the username of the user whose email is to be updated.
      * @param newEmail the new email to set for the user.
      */
  @Transactional
  public void updateUserEmail(String username, String newEmail) {
    UserEntity user = userRepository.findById(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setEmail(newEmail);
    userRepository.save(user);
  }

  /**
   * Updates the email address of a user.
   *
   * @param username the username of the user whose full name is to be updated.
   * @param newFullName the new full name to set for the user.
   */
  @Transactional
  public void updateUserFullName(String username, String newFullName) {
    UserEntity user = userRepository.findById(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setFullName(newFullName);
    userRepository.save(user);
  }

  /**
   * Updates the password of a user.
   *
   * @param username the username of the user whose full name is to be updated.
   * @param newPassword the new password to set for the user.
   */
  @Transactional
  public void updateUserPassword(String username, String newPassword) {
    UserEntity user = userRepository.findById(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    String newHashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(newHashedPassword);
    userRepository.save(user);
  }

    /**
     * Finds quiz attempts based on a user.
     *
     * @param username the username of the user.
     * @param pageable the pageable object.
     * @return a page of attempts.
     */
  @Override
  public Page<SavedQuizAttemptDto> findAttemptsByUser(String username, Pageable pageable) {
    UserEntity userEntity = findEntityByUsername(username);
    Page<QuizAttemptEntity> quizAttemptEntities = attemptRepository.findQuizAttemptEntitiesByUser(userEntity, pageable);
    return quizAttemptEntities.map(quizAttemptEntity -> {
      SavedQuizAttemptDto quizAttemptDto = new ModelMapper().map(quizAttemptEntity, SavedQuizAttemptDto.class);
      return quizAttemptDto;
    });
  }
}
