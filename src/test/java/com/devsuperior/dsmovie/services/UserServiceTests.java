package com.devsuperior.dsmovie.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	   @InjectMocks
	    private UserService service;

	    @Mock
	    private UserRepository repository;

	    @Mock
	    private CustomUserUtil customUserUtil;

	    private String existingUsername, nonExistingUsername;
	    private UserEntity user;
	    private List<UserDetailsProjection> userDetails;

	    @BeforeEach
	    void setUp() throws Exception {
	        existingUsername = "maria@gmail.com";
	        nonExistingUsername = "user@gmail.com";

	        user = UserFactory.createUserEntity();
	        user.setUsername(existingUsername);
	        userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);

	        // Mock: authenticated()
	        Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingUsername);
	        Mockito.when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
	        Mockito.when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

	        // Mock: loadUserByUsername()
	        Mockito.when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetails);
	        Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());
	    }

	    @Test
	    public void authenticatedShouldReturnUserEntityWhenUserExists() {
	        UserEntity result = service.authenticated();

	        Assertions.assertNotNull(result);
	        Assertions.assertEquals(existingUsername, result.getUsername());
	    }

	    @Test
	    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
	     
	        Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(nonExistingUsername);

	        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
	            service.authenticated();
	        });
	    }

	    @Test
	    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
	        UserDetails result = service.loadUserByUsername(existingUsername);

	        Assertions.assertNotNull(result);
	        Assertions.assertEquals(existingUsername, result.getUsername());
	    }

	    @Test
	    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
	        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
	            service.loadUserByUsername(nonExistingUsername);
	        });
	    }
}


