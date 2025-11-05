package net.digitalbooster.musicshop.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "media_types")
public class MediaType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MediaTypeId")
    private Integer mediaTypeId;

    @Column(name = "Name", length = 120)
    private String name;

    @OneToMany(mappedBy = "mediaType")
    private Set<Track> tracks;

    // Getters and Setters
    public Integer getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }
}