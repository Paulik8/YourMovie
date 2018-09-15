package ru.paul.moviesupport.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import ru.paul.moviesupport.models.MoviePage;

@Entity
public class MovieData {
    @Id(assignable = true)
    public long id;//записывать id фильма, либо если при прокрутке список items продолжается, то по номеру щелчка
    private Integer idOfMovie;
    private byte [] movie;

    public MovieData(long id, Integer idOfMovie, byte[] movie) {
        this.id = id;
        this.idOfMovie = idOfMovie;
        this.movie = movie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getIdOfMovie() {
        return idOfMovie;
    }

    public void setIdOfMovie(Integer idOfMovie) {
        this.idOfMovie = idOfMovie;
    }

    public byte[] getMovie() {
        return movie;
    }

    public void setMovie(byte[] movie) {
        this.movie = movie;
    }
}
