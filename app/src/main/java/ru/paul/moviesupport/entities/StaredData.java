package ru.paul.moviesupport.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class StaredData {
    @Id
    public long id;
    private Integer movieId;
    private byte[] movie;

    public StaredData(long id, Integer movieId, byte[] movie) {
        this.id = id;
        this.movieId = movieId;
        this.movie = movie;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public byte[] getMovie() {
        return movie;
    }

    public void setMovie(byte[] movie) {
        this.movie = movie;
    }
}
