package ru.paul.moviesupport.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class GenreData {
    @Id
    public long id;
    private Integer genreId;
    private String genreName;

    public GenreData(long id, Integer genreId, String genreName) {
        this.id = id;
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }


}
