package ro.fortech.bookmarks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.fortech.bookmarks.entities.Bookmark;

import java.util.Collection;

public interface BookmarkRepo extends JpaRepository<Bookmark, Long>{
    Collection<Bookmark> findByAccountUsername(String username);
}
