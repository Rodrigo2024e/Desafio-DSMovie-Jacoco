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
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository scoreRepository;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private UserService userService;
	
	private UserEntity admin, client;
	private Long existingMovieId, nonExistingMoviedId, scoreEntityPK;
	private ScoreEntity score;
	private MovieEntity movie;
	private MovieDTO movieDto;
	private ScoreDTO scoreDto;
	private UserEntity user;

	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L;
		nonExistingMoviedId = 2L;
		scoreEntityPK = 3L;
		
		client = UserFactory.createUserEntity();
		
		score = ScoreFactory.createScoreEntity();
		
		movie = MovieFactory.createMovieEntity();
		
		scoreDto =  ScoreFactory.createScoreDTO();
		
		scoreDto = new ScoreDTO(score);
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMoviedId)).thenReturn(Optional.empty());
		
		Mockito.when(movieRepository.findById(scoreEntityPK)).thenReturn(Optional.of(movie));
		Mockito.when(scoreRepository.save(any())).thenReturn(score);
	
	}
	
	
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		

		Mockito.when(userService.authenticated()).thenReturn(client);
		
		movie.setId(existingMovieId);
		ScoreEntity scoreEntity = new ScoreEntity();
		
		MovieDTO result = service.saveScore(scoreDto);
		
		Assertions.assertNotNull(result);
	}
/*	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
	}

*/
}
