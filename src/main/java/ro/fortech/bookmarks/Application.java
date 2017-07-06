package ro.fortech.bookmarks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ro.fortech.bookmarks.entities.Account;
import ro.fortech.bookmarks.entities.Bookmark;
import ro.fortech.bookmarks.repo.AccountRepo;
import ro.fortech.bookmarks.repo.BookmarkRepo;

import java.util.Arrays;

@ComponentScan(basePackages = "ro.fortech")
@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = {BookmarkRepo.class, AccountRepo.class})
@EntityScan(basePackageClasses = {Account.class, Bookmark.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    CommandLineRunner init(AccountRepo aRepo,
                           BookmarkRepo bRepo){
        return(args) -> Arrays.asList(
                "jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
                .forEach(a ->{
                            Account account = aRepo.save(new Account(a, "password"));
                            bRepo.save(new Bookmark(account,
                                    "http://bookmark.com/1/" + a, "A description"));
                            bRepo.save(new Bookmark(account,
                                    "http://bookmark.com/2/" + a, "A description"));
                });
    }
}