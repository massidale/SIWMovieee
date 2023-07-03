package it.uniroma3.siw.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Service
public class ArtistService {
	
	@Autowired
	private ArtistRepository artistRepository;
	
	
	public Artist findById(Long id) {
		return this.artistRepository.findById(id).get();
	}
	
	public Iterable<Artist> findAll() {
		return this.artistRepository.findAll();
	}
	
	public Artist save(Artist artist) {
		 return this.artistRepository.save(artist);
	}

	public Iterable<Artist> findActorsNotInMovie(Long artistId) {
		return this.artistRepository.findActorsNotInMovie(artistId);
	}

	public boolean existsByNameAndSurname(String name, String surname) {
		return this.artistRepository.existsByNameAndSurname(name, surname);
	}

}
