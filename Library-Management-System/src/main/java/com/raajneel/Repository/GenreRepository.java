package com.raajneel.Repository;

import com.raajneel.Model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findByActiveTrueOrderByDisplayOrderAsc();

    List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

    List<Genre> findByParentGenreIdAndActiveTrueOrderByDisplayOrderAsc(
            Long parentGenreId
    );

    long countByActiveTrue();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.genre.id = :genreId AND b.active = true")
    long countBooksByGenre(@Param("genreId") Long genreId);

    @Query("SELECT b.genre.id, COUNT(b) FROM Book b WHERE b.active = true GROUP BY b.genre.id")
    List<Object[]> countBooksGroupedByGenre();
}
