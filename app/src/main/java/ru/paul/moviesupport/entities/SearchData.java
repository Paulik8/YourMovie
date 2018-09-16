package ru.paul.moviesupport.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class SearchData {
    @Id(assignable = true)
    public long id;//записывать id фильма, либо если при прокрутке список items продолжается, то по номеру щелчка
    private Integer searchIdMovie;
    private byte [] movie;

    public SearchData(long id, Integer searchIdMovie, byte[] movie) {
        this.id = id;
        this.searchIdMovie = searchIdMovie;
        this.movie = movie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getSearchIdMovie() {
        return searchIdMovie;
    }

    public void setSearchIdMovie(Integer searchIdMovie) {
        this.searchIdMovie = searchIdMovie;
    }

    public byte[] getMovie() {
        return movie;
    }

    public void setMovie(byte[] movie) {
        this.movie = movie;
    }
}
