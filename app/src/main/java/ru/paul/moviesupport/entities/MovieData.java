package ru.paul.moviesupport.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import ru.paul.moviesupport.models.MoviePage;

@Entity
public class MovieData {
    @Id
    public long id;//записывать id фильма, либо если при прокрутке список items продолжается, то по номеру щелчка
    private byte [] page;

    public MovieData(long id, byte[] page) {
        this.id = id;
        this.page = page;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getPage() {
        return page;
    }

    public void setPage(byte[] page) {
        this.page = page;
    }
}
