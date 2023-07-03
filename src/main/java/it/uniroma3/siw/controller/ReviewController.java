package it.uniroma3.siw.controller;

import java.util.Collection;

import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {
	
	@Autowired 
	private ReviewService reviewService;
	@Autowired 
	private MovieService movieService;
	@Autowired 
	private CredentialsService credentialsService;
	@Autowired
	private ReviewValidator reviewValidator;
	

	@GetMapping(value="/user/formNewReview/{movieId}")
	public String formNewReview(@PathVariable("movieId") Long movieId, Model model) {
		Review review = new Review();
		model.addAttribute("review", review);
		
		return "user/formNewReview.html";
	}
	
	@PostMapping("/default/review/{movieId}")
	public String newReview(@PathVariable("movieId") Long movieId,@Valid @ModelAttribute("review") Review review, BindingResult bindingResult,  Model model) {
		
		Movie movie = movieService.findById(movieId);
		review.setMovie(movie);
		
		this.reviewValidator.validate(review, bindingResult);
		if (!bindingResult.hasErrors()) {
			Credentials credentials = credentialsService.getCurrentCredentials();
			review.setCredentials(credentials);
			
			Collection<Review> reviews = movie.getReviews();
			reviews.add(review);
			
			this.reviewService.save(review);
			model.addAttribute("movie", movie);
			return "default/movie.html";
			
		} else {
			return "user/formNewReview.html"; 
		}
	}
	
	@GetMapping("/admin/removeReview/{movieId}/{reviewId}")
	public String removeReview(@PathVariable("movieId") Long movieId, @PathVariable("reviewId") Long reviewId, Model model) {
		Movie movie = movieService.findById(movieId);
		Review review = reviewService.findById(reviewId);
		movie.getReviews().remove(review);
		movieService.save(movie);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
}
