package ro.fortech.bookmarks;

import org.omg.CORBA.ServerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import ro.fortech.bookmarks.entities.Account;
import ro.fortech.bookmarks.entities.Bookmark;
import ro.fortech.bookmarks.repo.AccountRepo;
import ro.fortech.bookmarks.repo.BookmarkRepo;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    FilterRegistrationBean corsFilter(
            @Value("${tagit.origin:http://localhost:9000}")String origin){

    return  new FilterRegistrationBean(new Filter(){

        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            String method = request.getMethod();

            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods",
                    "POST,GET,OPTIONS,DELETE");
            response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader(
                    "Access-Control-Allow-Headers",
                    "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");


            if ("OPTIONS".equals(method)) {
                response.setStatus(HttpStatus.OK.value());
            } else {
                filterChain.doFilter(req, res);
            }
        }


            public void init(FilterConfig filterConfig) {}
            public void destroy() {}
        });
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