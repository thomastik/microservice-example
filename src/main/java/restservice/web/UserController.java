package restservice.web;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import restservice.domain.User;
import restservice.domain.UserRepository;

@RestController
public class UserController {
	
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Inject
    UserRepository repository;
    
    @GetMapping("/user/")
    public List<User> getUsers() {
    		if (logger.isInfoEnabled()) 
    			logger.info("Fetching " + repository.count() + " Users");
        
		return repository.findAll();
	}

    @GetMapping("/user/{id}")
    public ResponseEntity<? extends Object> getUser(@PathVariable("id") Long id) {
        logger.info("Fetching User with id {}", id);
        User user = repository.findOne(id);
        if (user == null) {
            logger.error("User with id {} not found.", id);
            return new ResponseEntity<>("User with id " + id 
                    + " not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }    

    // -------------------Create a User-------------------------------------------  
    @PostMapping("/user/")
    public ResponseEntity<String> postUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("Creating User : {}", user);
 
        repository.save(user);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // ------------------- Update a User ------------------------------------------------
    
    @PutMapping("/user/{id}")
    public ResponseEntity<? extends Object> putUser(@PathVariable("id") Long id, @RequestBody User user) {
        logger.info("Updating User with id {}", id);
 
        User currentUser = repository.findOne(id);
 
        if (currentUser == null) {
            logger.error("Unable to update. User with id {} not found.", id);
            return new ResponseEntity<>("Unable to upate. User with id " + id + " not found.",
                    HttpStatus.NOT_FOUND);
        }
 
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
 
        repository.save(currentUser);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }
 
    // ------------------- Delete a User-----------------------------------------
 
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting User with id {}", id);
 
        User user = repository.findOne(id);
        if (user == null) {
            logger.error("Unable to delete. User with id {} not found.", id);
            return new ResponseEntity<>("Unable to delete. User with id " + id + " not found.",
                    HttpStatus.NOT_FOUND);
        }
        repository.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
 
    // ------------------- Delete All Users-----------------------------
 
    @DeleteMapping("/user/")
    public ResponseEntity<String> deleteAllUsers() {
        logger.info("Deleting All Users");
 
        repository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
 
}