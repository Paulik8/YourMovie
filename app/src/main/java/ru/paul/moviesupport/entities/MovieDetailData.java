package ru.paul.moviesupport.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class MovieDetailData {
    @Id
    public long id;
    private Integer idMovie;
    private byte[] movieDetail;

    public MovieDetailData(long id, Integer idMovie, byte[] movieDetail) {
        this.id = id;
        this.idMovie = idMovie;
        this.movieDetail = movieDetail;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(Integer idMovie) {
        this.idMovie = idMovie;
    }

    public byte[] getMovieDetail() {
        return movieDetail;
    }

    public void setMovieDetail(byte[] movieDetail) {
        this.movieDetail = movieDetail;
    }
}
