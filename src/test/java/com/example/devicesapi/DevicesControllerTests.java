package com.example.devicesapi;

import com.example.devicesapi.entities.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        classes = DevicesapiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
        //properties = "spring.main.web-application-type=servlet",
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class

})
class DevicesControllerTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("devices_database")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldFindAllPosts() {
        Device[] posts = restTemplate.getForObject("/api/devices", Device[].class);
        assertThat(posts.length).isGreaterThan(1);
    }
    
//    @Test
//    void shouldFindPostWhenValidPostID() {
//        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/1", HttpMethod.GET, null, Post.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//    }
//
//    @Test
//    void shouldThrowNotFoundWhenInvalidPostID() {
//        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/999", HttpMethod.GET, null, Post.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Rollback
//    void shouldCreateNewPostWhenPostIsValid() {
//        Post post = new Post(101,1,"101 Title","101 Body",null);
//
//        ResponseEntity<Post> response = restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<Post>(post), Post.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(101);
//        assertThat(response.getBody().userId()).isEqualTo(1);
//        assertThat(response.getBody().title()).isEqualTo("101 Title");
//        assertThat(response.getBody().body()).isEqualTo("101 Body");
//    }
//
//    @Test
//    void shouldNotCreateNewPostWhenValidationFails() {
//        Post post = new Post(101,1,"","",null);
//        ResponseEntity<Post> response = restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<Post>(post), Post.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//    }
//
//    @Test
//    @Rollback
//    void shouldUpdatePostWhenPostIsValid() {
//        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/99", HttpMethod.GET, null, Post.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        Post existing = response.getBody();
//        assertThat(existing).isNotNull();
//        Post updated = new Post(existing.id(),existing.userId(),"NEW POST TITLE #1", "NEW POST BODY #1",existing.version());
//
//        assertThat(updated.id()).isEqualTo(99);
//        assertThat(updated.userId()).isEqualTo(10);
//        assertThat(updated.title()).isEqualTo("NEW POST TITLE #1");
//        assertThat(updated.body()).isEqualTo("NEW POST BODY #1");
//    }
//
//    @Test
//    @Rollback
//    void shouldDeleteWithValidID() {
//        ResponseEntity<Void> response = restTemplate.exchange("/api/posts/88", HttpMethod.DELETE, null, Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//    }

}