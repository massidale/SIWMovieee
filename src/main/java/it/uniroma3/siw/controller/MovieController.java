package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;

@Controller
public class MovieController {
	@Autowired 
	private MovieService movieService;
	@Autowired 
	private ArtistService artistService;
	@Autowired 
	private ImageService imageService;
	@Autowired 
	private MovieValidator movieValidator;

	
	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieService.findById(id));
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.findAll());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		
		Artist director = this.artistService.findById(directorId);
		Movie movie = this.movieService.findById(movieId);
		movie.setDirector(director);
		this.movieService.save(movie);
		
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistService.findAll());
		model.addAttribute("movie", movieService.findById(id));
		return "admin/directorsToAdd.html";
	}
	
	@PostMapping("/admin/addImageToMovie/{movieId}")
	public String addImageToMovie(Model model, @PathVariable Long movieId, @RequestParam("file") MultipartFile file) throws IOException {
		Movie movie = movieService.findById(movieId);
		if (!file.isEmpty()) {
			Image img = new Image(file.getBytes());
			this.imageService.save(img);
			movie.setImage(img);
			this.movieService.save(movie);
		}
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}

	@PostMapping("/admin/movie")
	public String newMovie( Model model, @Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, @RequestParam("file") MultipartFile file) throws IOException {

		if (!file.isEmpty()) {
			Image img = new Image(file.getBytes());
			this.imageService.save(img);
			movie.setImage(img);
		}
		
		this.movieValidator.validate(movie, bindingResult);
		
		if (!bindingResult.hasErrors()) {

			this.movieService.save(movie); 
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie";
			
		} else {
			return "admin/formNewMovie.html";
		}
	}

	@GetMapping("/default/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.findById(id));
		return "default/movie.html";
	}

	@GetMapping("/default/movie")
	public String getMovies(Model model) {		
		model.addAttribute("movies", this.movieService.findAll());
		return "default/movies.html";
	}
	
	@GetMapping("/default/formSearchMovies")
	public String formSearchMovies() {
		return "default/formSearchMovies.html";
	}

	@PostMapping("/default/searchMovies")
	public String searchMovies(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieService.findByYear(year));
		return "default/foundMovies.html";
	}
	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		List<Artist> actorsToAdd = this.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieService.findById(id));

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.findById(movieId);
		Artist actor = this.artistService.findById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		this.movieService.save(movie);
		
		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.findById(movieId);
		Artist actor = this.artistService.findById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		this.movieService.save(movie);

		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}

	private List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistService.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}
	
	@GetMapping("/admin/removeImageFromMovie/{movieId}")
	public String removeImageFromMovie(@PathVariable Long movieId, Model model) {
		Movie movie = this.movieService.findById(movieId);
		movie.setImage(null);
		this.movieService.save(movie);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	
	}
}
