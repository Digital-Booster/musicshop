package net.digitalbooster.musicshop.controller;

import net.digitalbooster.musicshop.model.Album;
import net.digitalbooster.musicshop.model.Artist;
import net.digitalbooster.musicshop.model.Genre;
import net.digitalbooster.musicshop.model.Track;
import net.digitalbooster.musicshop.repository.AlbumRepository;
import net.digitalbooster.musicshop.repository.ArtistRepository;
import net.digitalbooster.musicshop.repository.GenreRepository;
import net.digitalbooster.musicshop.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CatalogController {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer artistId,
            @RequestParam(required = false) Integer albumId,
            @RequestParam(required = false) String trackName,
            Model model) {
        
        List<Track> tracks;

        if (genreId != null || artistId != null || albumId != null || (trackName != null && !trackName.isEmpty())) {
            tracks = trackRepository.findWithFilters(genreId, artistId, albumId, trackName);
        } else {
            tracks = trackRepository.findAll();
        }

        List<Genre> genres = genreRepository.findAll();
        List<Artist> artists = artistRepository.findAll();
        List<Album> albums = albumRepository.findAll();

        model.addAttribute("tracks", tracks);
        model.addAttribute("genres", genres);
        model.addAttribute("artists", artists);
        model.addAttribute("albums", albums);
        
        // Add selected values to the model to retain them in the form
        model.addAttribute("selectedGenre", genreId);
        model.addAttribute("selectedArtist", artistId);
        model.addAttribute("selectedAlbum", albumId);
        model.addAttribute("selectedTrackName", trackName);

        return "catalog";
    }
}
