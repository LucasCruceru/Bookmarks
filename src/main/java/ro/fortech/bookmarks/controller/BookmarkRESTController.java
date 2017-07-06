package ro.fortech.bookmarks.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.fortech.bookmarks.exceptions.UserNotFoundException;
import ro.fortech.bookmarks.entities.Bookmark;
import ro.fortech.bookmarks.repo.AccountRepo;
import ro.fortech.bookmarks.repo.BookmarkRepo;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{userId}/bookmarks")
public class BookmarkRESTController {

    private final BookmarkRepo bRepo;
    private final AccountRepo aRepo;

    @Autowired
    BookmarkRESTController(BookmarkRepo bRepo, AccountRepo aRepo) {
        this.bRepo = bRepo;
        this.aRepo = aRepo;
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Bookmark> readBookmarks(@PathVariable String userId) {
        this.validateUser(userId);

        List<BookmarkResource> bookmarkResourceList = bRepo
                .findByAccountUsername(userId).stream().map(BookmarkResource::new)
                .collect(Collectors.toList());

        return this.bRepo.findByAccountUsername(userId);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {
        this.validateUser(userId);

        return this.aRepo.findByUsername(userId)
                .map(account -> {
                    Bookmark result = bRepo.save(new Bookmark(account,
                            input.uri, input.description));

                    Link forOneBookmark = new BookmarkResource(result).getLink("self");



                    return ResponseEntity.created(URI.create(forOneBookmark.getHref())).build();
                })
                .orElse(ResponseEntity.noContent().build());

    }

    @RequestMapping(method = RequestMethod.GET, value = "/{bookmarkId}")
    public Bookmark readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
        this.validateUser(userId);
        return this.bRepo.findOne(bookmarkId);
    }

    private void validateUser(String userId) {
        this.aRepo.findByUsername(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}


