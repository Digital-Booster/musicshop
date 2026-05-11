package net.digitalbooster.musicshop.repository;

import net.digitalbooster.musicshop.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    @Query("SELECT t FROM Track t " +
           "LEFT JOIN t.album al " +
           "LEFT JOIN al.artist ar " +
           "WHERE (:genreId IS NULL OR t.genre.id = :genreId) " +
           "AND (:artistId IS NULL OR ar.id = :artistId) " +
           "AND (:albumId IS NULL OR al.id = :albumId) " +
           "AND (:trackName IS NULL OR t.name LIKE %:trackName%)")
    List<Track> findWithFilters(
        @Param("genreId") Integer genreId,
        @Param("artistId") Integer artistId,
        @Param("albumId") Integer albumId,
        @Param("trackName") String trackName
    );
}