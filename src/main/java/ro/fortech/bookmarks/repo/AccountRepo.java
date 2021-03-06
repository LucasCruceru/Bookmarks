package ro.fortech.bookmarks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.fortech.bookmarks.entities.Account;

import java.util.Optional;
@Repository
public interface AccountRepo extends JpaRepository<Account, Long>{
    Optional<Account> findByUsername(String username);
}
