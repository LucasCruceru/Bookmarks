package ro.fortech.bookmarks.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Bookmark {


    @JsonIgnore
    @ManyToOne
    public Account account;

    @Id
    @GeneratedValue
    private Long id;

    public Bookmark(){

    }
    public String uri;
    public String description;

    public Bookmark(Account account, String uri, String description) {
        this.account = account;
        this.uri = uri;
        this.description = description;
    }

    public Account getAccount() {
        return account;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }
}

