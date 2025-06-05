package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	    @InjectMocks
	    private ScoreService service;

	    @Mock
	    private MovieRepository movieRepository;
	    
	    @Mock
	    private ScoreRepository scoreRepository;

	    @Mock
	    private UserService userService;

	    private MovieEntity movie;
	    private ScoreDTO scoreDTO;
	    private UserEntity client;

	    private Long existingMovieId;
	    private Long nonExistingMovieId;

	    @BeforeEach
	    void setUp() throws Exception {
	        existingMovieId = 1L;
	        nonExistingMovieId = 2L;

	        movie = MovieFactory.createMovieEntity();
	        movie.setId(existingMovieId);

	        client = UserFactory.createUserEntity();

	        scoreDTO = ScoreFactory.createScoreDTO();
	        scoreDTO.setMovieId(existingMovieId);
	        scoreDTO.setScore(movie.getScore());

	        Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
	        Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
	        Mockito.when(movieRepository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);
	        Mockito.when(movieRepository.save(any())).thenReturn(movie);
	    }

	    @Test
	    public void saveScoreShouldReturnMovieDTO() {
	    	   // Arrange
	        scoreDTO = ScoreFactory.createScoreDTO();
	        scoreDTO.setMovieId(existingMovieId);

	        movie = MovieFactory.createMovieEntity();
	        movie.setId(scoreDTO.getMovieId());

	        client = UserFactory.createUserEntity();

	        // Simula um score na lista do filme
	        ScoreEntity score = new ScoreEntity();
	        score.setMovie(movie);
	        score.setUser(client);
	        score.setValue(scoreDTO.getScore());

	        movie.getScores().add(score); 

	        Mockito.when(userService.authenticated()).thenReturn(client);
	        Mockito.when(movieRepository.findById(scoreDTO.getMovieId()))
	               .thenReturn(Optional.of(movie));
	        Mockito.when(movieRepository.save(any())).thenReturn(movie);
	        Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(score);

	        // Act
	        MovieDTO result = service.saveScore(scoreDTO);

	        // Assert
	        Assertions.assertNotNull(result);
	        Assertions.assertEquals(scoreDTO.getMovieId(), result.getId());
	        Assertions.assertEquals(movie.getTitle(), result.getTitle());
	        Assertions.assertEquals(score.getValue(), movie.getScore()); 
	        Assertions.assertEquals(1, movie.getCount()); 
	    }

	    @Test
	    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
	    	   // Arrange
	        scoreDTO = ScoreFactory.createScoreDTO();
	        scoreDTO.setMovieId(nonExistingMovieId); 
	        client = UserFactory.createUserEntity();

	        Mockito.when(userService.authenticated()).thenReturn(client);

	        // Act & Assert
	        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
	            service.saveScore(scoreDTO);
	        });
	    }

	}
	


