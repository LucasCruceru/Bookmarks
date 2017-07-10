package ro.fortech.bookmarks.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import ro.fortech.bookmarks.entities.Bookmark;

import java.security.Principal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class BookmarkResource extends ResourceSupport {

    private final Bookmark bookmark;


    public BookmarkResource(Bookmark bookmark, Principal principal) {
        String username = bookmark.getAccount().getUsername();
        this.bookmark = bookmark;
        this.add(new Link(bookmark.getUri(), "bookmark-uri"));
        this.add(linkTo(BookmarkRestController.class, username).withRel("bookmarks"));
        this.add(linkTo(methodOn(BookmarkRestController.class, username).readBookmark(principal, bookmark.getId())).withSelfRel());
    }
    public Bookmark getBm() {
        return bookmark;

    }

}
