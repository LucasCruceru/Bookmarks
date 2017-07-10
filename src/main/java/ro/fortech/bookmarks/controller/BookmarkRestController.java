package ro.fortech.bookmarks.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.fortech.bookmarks.entities.Bookmark;
import ro.fortech.bookmarks.exceptions.UserNotFoundException;
import ro.fortech.bookmarks.repo.AccountRepo;
import ro.fortech.bookmarks.repo.BookmarkRepo;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkRestController {

    private final BookmarkRepo bRepo;
    private final AccountRepo aRepo;

    @Autowired
    BookmarkRestController(BookmarkRepo bRepo, AccountRepo aRepo) {
        this.bRepo = bRepo;
        this.aRepo = aRepo;
    }


    @RequestMapping(method = RequestMethod.GET)
    Resources<BookmarkResource> readBookmarks(Principal principal) {
        this.validateUser(principal);

        List<BookmarkResource> bookmarkResourceList = bRepo
                .findByAccountUsername(principal.getName()).stream()
                .map( bm ->
                    new BookmarkResource( bm, principal)
                )
                .collect(Collectors.toList());

        return new Resources<>(bookmarkResourceList);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
        this.validateUser(principal);

        return this.aRepo.findByUsername(principal.getName())
                .map(account -> {
                    Bookmark result = bRepo.save(new Bookmark(account,
                            input.uri, input.description));

                    Link forOneBookmark = new BookmarkResource(result, principal)
                            .getLink(Link.REL_SELF);

                    return ResponseEntity.created(URI
                            .create(forOneBookmark.getHref()))
                            .build();
                })
                .orElse(ResponseEntity.noContent().build());

    }

    @RequestMapping(method = RequestMethod.GET, value = "/{bookmarkId}")
    public BookmarkResource readBookmark(Principal principal, @PathVariable Long bookmarkId) {
        this.validateUser(principal);
        return new BookmarkResource(
                this.bRepo.findOne(bookmarkId), principal);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{bookmarkId}")
    public Resources<BookmarkResource> deleteBookmark(Principal principal, @PathVariable Long bookmarkId) {
        this.validateUser(principal);
        bRepo.delete(bookmarkId);

        return this.readBookmarks(principal);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{bookmarkId}")
    public Bookmark PUT(Principal principal, @PathVariable Long bookmarkId, @RequestBody Bookmark input) {
        this.validateUser(principal);

        this.bRepo.findOne(bookmarkId).updateBookmark(input);


        return this.bRepo.findOne(bookmarkId);
    }

    private void validateUser(Principal principal) {
        String userId = principal.getName();

        this.aRepo.findByUsername(userId)
                .orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}


