package ro.fortech.bookmarks.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ro.fortech.bookmarks.Application;
import ro.fortech.bookmarks.entities.Account;
import ro.fortech.bookmarks.entities.Bookmark;
import ro.fortech.bookmarks.repo.AccountRepo;
import ro.fortech.bookmarks.repo.BookmarkRepo;


import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class BookmarkRESTControllerTest {


        public final String auth = "Authorization";
        public final String bearer = "Bearer d41891c9-c93e-4daf-b5a1-d70e10b65a4f";

        private Principal principal;

        private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                MediaType.APPLICATION_JSON.getSubtype(),
                Charset.forName("utf8"));

        private MockMvc mockMvc;

        private String userName = "bdussault";

        private HttpMessageConverter mappingJackson2HttpMessageConverter;

        private Account account;

        private List<Bookmark> bookmarkList = new ArrayList<>();

        @Autowired
        private BookmarkRepo bookmarkRepository;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private AccountRepo accountRepository;

        @Autowired
        void setConverters(HttpMessageConverter<?>[] converters) {

            this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                    .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                    .findAny()
                    .orElse(null);

            assertNotNull("the JSON message converter must not be null",
                    this.mappingJackson2HttpMessageConverter);
        }

        @Before
        public void setup() throws Exception {
            this.mockMvc = webAppContextSetup(webApplicationContext).build();

            this.bookmarkRepository.deleteAllInBatch();
            this.accountRepository.deleteAllInBatch();

            this.account = accountRepository.save(new Account(userName, "password"));
            this.bookmarkList.add(bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + userName, "A description")));
            this.bookmarkList.add(bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + userName, "A description")));
        }

        @Test
        public void userNotFound() throws Exception {
            mockMvc.perform(post("/george/bookmarks/")
                    .principal(getPrinciple())
                    .header(auth,bearer)
                    .content(this.json(new Bookmark()))
                    .contentType(contentType))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void readSingleBookmark() throws Exception {
            mockMvc.perform(get("/bookmarks/"
                    + this.bookmarkList.get(0).getId())
                    .principal(getPrinciple())
                    .header(auth,bearer))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("bm.id", is(this.bookmarkList.get(0).getId().intValue())))
                    .andExpect(jsonPath("bm.uri", is("http://bookmark.com/1/" + userName)))
                    .andExpect(jsonPath("bm.description", is("A description")));
        }

        @Test
        public void readBookmarks() throws Exception {
            mockMvc.perform(get("/bookmarks")
                    .principal(getPrinciple())
                    .header(auth,bearer))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("content", hasSize(2)))
                    .andExpect(jsonPath("content.[0].bm.id", is(this.bookmarkList.get(0).getId().intValue())))
                    .andExpect(jsonPath("content.[0].bm.uri", is("http://bookmark.com/1/" + userName)))
                    .andExpect(jsonPath("content.[0].bm.description", is("A description")))
                    .andExpect(jsonPath("content.[1].bm.id", is(this.bookmarkList.get(1).getId().intValue())))
                    .andExpect(jsonPath("content.[1].bm.uri", is("http://bookmark.com/2/" + userName)))
                    .andExpect(jsonPath("content.[1].bm.description", is("A description")));
        }

        @Test
        public void createBookmark() throws Exception {
            String bookmarkJson = json(new Bookmark(
                    this.account, "http://spring.io", "a bookmark to the best resource for Spring news and information"));

            this.mockMvc.perform(post("/bookmarks")
                    .principal(getPrinciple())
                    .header(auth,bearer)
                    .contentType(contentType)
                    .content(bookmarkJson))
                    .andExpect(status().isCreated());
        }

        @Test
        public void deleteSingleBookmark() throws Exception {
            mockMvc.perform(delete("/bookmarks/"
                + this.bookmarkList.get(0).getId())
                .principal(getPrinciple())
                .header(auth,bearer))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("content", hasSize(1)));
        }

        @Test
        public void updateBookmark() throws Exception {

        Bookmark temp = new Bookmark(this.account, "http://spring.io", "a bookmark to the best resource for Spring news and information");
            this.bookmarkList.get(0).updateBookmark(temp);

        String bookmarkJson = json(this.bookmarkList.get(0));

        this.mockMvc.perform(put("/bookmarks/" + this.bookmarkList.get(0).getId())
                .principal(getPrinciple())
                .header(auth,bearer)
                .contentType(contentType)
                .content(bookmarkJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("uri", is("http://spring.io")))
                .andExpect(jsonPath("description", is("a bookmark to the best resource for Spring news and information")))
        ;
        }

        private String json(Object o) throws IOException {
            MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
            this.mappingJackson2HttpMessageConverter.write(
                    o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
            return mockHttpOutputMessage.getBodyAsString();
        }

        private Principal getPrinciple() {
            if (principal == null)
                principal = new Principal() {
                    @Override
                    public String getName() {
                        return userName;
                    }
                };

            return principal;
        }
    }

