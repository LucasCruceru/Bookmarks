package ro.fortech.bookmarks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.fortech.bookmarks.entities.Bookmark;

import java.util.Collection;

@Repository
public interface BookmarkRepo extends JpaRepository<Bookmark, Long>{
    Collection<Bookmark> findByAccountUsername(String username);
}
