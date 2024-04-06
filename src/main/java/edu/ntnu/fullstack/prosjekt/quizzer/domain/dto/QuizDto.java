package edu.ntnu.fullstack.prosjekt.quizzer.domain.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The QuizDto class is a mirror of the QuizEntity class, with the intention of creating
 *  * a separation between user input/output and database objects.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
  /**
   * The quizId field is a unique identifier and primary key for a quiz entry in
   * the database.
   */
  private Long quizId;

  /**
   * The title field represents the name of the quiz.
   */
  private String title;

  /**
   * The description field should be used for describing the quiz.
   */
  private String description;

  /**
   * Links to an image for the quiz.
   */
  private String imageLink = "";

  /**
   * The owner field should represent the user who owns the quiz, with many quizzes belonging to
   * one user. This field is a foreign key linking owner and quizzes.
   */
  private UserDto owner;
  
  private List<QuestionDto> questions;
}
