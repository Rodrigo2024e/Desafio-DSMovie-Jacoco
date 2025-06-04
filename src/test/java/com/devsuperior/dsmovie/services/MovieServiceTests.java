package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;
	
	@Mock
	private MovieRepository movieRepository;
	
	private Long existingMovieId, nonExistingMovieId, dependentMovieId;
	private String titleName;
	private MovieEntity movie;
	private MovieDTO movieDto;
	private PageImpl<MovieEntity> page;
	
	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependentMovieId = 3L;
		
		titleName = "Mickey Mouse";
		
		movie = MovieFactory.createMovie(titleName);
		movieDto = new MovieDTO(movie);
		page = new PageImpl<>(List.of(movie));
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		Mockito.when(movieRepository.searchByTitle(any(), (Pageable)any())).thenReturn(page);
		
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		Mockito.when(movieRepository.getReferenceById(existingMovieId)).thenReturn(movie);
		Mockito.when(movieRepository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(movieRepository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(dependentMovieId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(nonExistingMovieId)).thenReturn(false);
		
		Mockito.doNothing().when(movieRepository).deleteById(existingMovieId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependentMovieId);
			
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieDTO> result = service.findAll(titleName, pageable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getTitle(), titleName);
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		
		MovieDTO result = service.findById(existingMovieId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertEquals(result.getTitle(), titleName);
		
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingMovieId);
			
		});
		
	}

	@Test
	public void insertShouldReturnMovieDTO() {
		
		MovieDTO result = service.insert(movieDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movie.getId());
		
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		
		MovieDTO result = service.update(existingMovieId, movieDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertEquals(result.getTitle(), movieDto.getTitle());			
		
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingMovieId, movieDto);
		});
		
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingMovieId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
			service.delete(nonExistingMovieId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentMovieId);
		});
	}

}
