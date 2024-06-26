package edu.ntnu.fullstack.prosjekt.quizzer.repositories;

import edu.ntnu.fullstack.prosjekt.quizzer.domain.entities.CategoryEntity;
import edu.ntnu.fullstack.prosjekt.quizzer.domain.entities.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides basic CRUD functionality for database operations against the category database table.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String>,
        PagingAndSortingRepository<CategoryEntity, String> {
}
