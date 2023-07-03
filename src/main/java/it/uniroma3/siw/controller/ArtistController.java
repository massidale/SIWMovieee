package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;

@Controller
public class ArtistController {
	;
	@Autowired 
	private ArtistService artistService;
	@Autowired 
	private ImageService imageService;
	@Autowired 
	private ArtistValidator artistValidator;

	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}
	
	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}
	
	@PostMapping("/admin/artist")
	public String newMovie( Model model, @Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, @RequestParam("file") MultipartFile file) throws IOException {

		if (!file.isEmpty()) {
			Image img = new Image(file.getBytes());
			this.imageService.save(img);
			artist.setImage(img);
		}
		
		this.artistValidator.validate(artist, bindingResult);
		
		if (!bindingResult.hasErrors()) {

			this.artistService.save(artist); 
			model.addAttribute("artist", artist);
			return "admin/formUpdateArtist";
			
		} else {
			return "admin/formNewArtist.html";
		}
	}

	@GetMapping("/default/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.findById(id));
		return "default/artist.html";
	}

	@GetMapping("/default/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistService.findAll());
		return "default/artists.html";
	}
	
	@GetMapping(value="/admin/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", artistService.findById(id));
		return "admin/formUpdateArtist.html";
	}
	
	@PostMapping("/admin/addImageToArtist/{artistId}")
	public String addImageToArtist(Model model, @PathVariable Long artistId, @RequestParam("file") MultipartFile file) throws IOException {
		Artist artist = artistService.findById(artistId);
		if (!file.isEmpty()) {
			Image img = new Image(file.getBytes());
			this.imageService.save(img);
			artist.setImage(img);
			this.artistService.save(artist);
		}
		model.addAttribute("artist", artist);
		return "admin/formUpdateArtist.html";
	}
	
	@GetMapping("/admin/removeImageFromArtist/{artistId}")
	public String removeImageFromArtist(@PathVariable Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		artist.setImage(null);
		this.artistService.save(artist);
		model.addAttribute("artist", artist);
		return "admin/formUpdateArtist.html";
	
	}
	
	@GetMapping(value="/admin/manageArtists")
	public String manageArtists(Model model) {
		model.addAttribute("artists", this.artistService.findAll());
		return "admin/manageArtists.html";
	}
}
