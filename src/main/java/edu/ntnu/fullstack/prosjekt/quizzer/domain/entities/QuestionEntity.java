package edu.ntnu.fullstack.prosjekt.quizzer.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

/**
 * QuestionEntity is one of the main entities in the application, acting as questions for quizzes in
 * the database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class QuestionEntity {
  /**
   * The questionId field is a unique identifier and primary key for a question entry in
   * the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_id_seq")
  private Long questionId;

  /**
   * The label field represents the question that is being asked, such as "How many people live
   * in Norway?."
   */
  private String label;

  /**
   * Links to an image for the quiz.
   */
  private String imageLink;

  /**
   * The position field should tell the question which position it will have in an ordered quiz,
   * which will be randomized if wanted.
   */
  private Short position;

  /**
   * The alternatives field should store JSON with the different alternatives that
   * a user can answer.
   */
  private String alternatives;


  /**
   * The quiz field references which quiz the question belongs to, in a many questions to
   * one quiz relationship.
   */
  @ManyToOne
  @JoinColumn(name = "quiz_Id")
  @JsonBackReference
  private QuizEntity quiz;
}
