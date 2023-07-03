package it.uniroma3.siw.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Service
public class MovieService {
	
	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private ImageRepository imageRepository;
	
	public Movie findById(Long id) {
		return this.movieRepository.findById(id).get();
	}
	
	public Iterable<Movie> findAll() {
		return this.movieRepository.findAll();
	}
	
	public Movie save(Movie movie) {
		 return this.movieRepository.save(movie);
	}

	public Iterable<Movie> findByYear(int year) {
		return this.movieRepository.findByYear(year);
	}

}
