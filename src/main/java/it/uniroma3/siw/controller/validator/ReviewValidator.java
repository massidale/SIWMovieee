package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.controller.GlobalController;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

@Component
public class ReviewValidator implements Validator {

	@Autowired
	private CredentialsService credentialsService;
	
	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review) o;
		Movie movie = review.getMovie();
		Credentials credentials = credentialsService.getCurrentCredentials();
		
		for(Review r : movie.getReviews()) {
			if(r.getCredentials().equals(credentials))
				errors.reject("review.duplicate");
		}
	}
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
}