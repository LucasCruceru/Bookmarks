package ro.fortech.bookmarks.controller;


import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import ro.fortech.bookmarks.controller.BookmarkRESTController;
import ro.fortech.bookmarks.entities.Bookmark;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class BookmarkResource extends ResourceSupport {

    private final Bookmark bm;

    public BookmarkResource(Bookmark bm) {
        String username = bm.getAccount().getUsername();
        this.bm = bm;
        this.add(new Link(bm.getUri(), "bookmark-uri"));
        this.add(linkTo(BookmarkRESTController.class, username).withRel("bookmarks"));
        this.add(linkTo(methodOn(BookmarkRESTController.class, username)
                .readBookmark(username, bm.getId())).withSelfRel());
    }
    public Bookmark getBm() {
        return bm;

    }
}
