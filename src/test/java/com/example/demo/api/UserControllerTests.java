package com.example.demo.api;

import com.example.demo.model.requests.CreateUserRequest;
import java.net.URI;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    static private String jwtToken;

    @Test
    @Order(1)
    public void createUser() throws Exception {
        CreateUserRequest request = getUserRequest();
        mvc.perform(
                post(new URI("/api/user/create"))
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void login() throws Exception {
        CreateUserRequest request = getUserRequest();
        MvcResult result = mvc.perform(
                post(new URI("/login"))
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        jwtToken = result.getResponse().getHeader("Authorization");
    }

    @Test
    public void loginWrongPassword() throws Exception {
        CreateUserRequest request = getUserRequest();
        request.setPassword("wrongpassword");
        mvc.perform(
                post(new URI("/login"))
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserById() throws Exception {
        mvc.perform(
                get("/api/user/id/1")
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByName() throws Exception {
        mvc.perform(
                get("/api/user/admin")
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void getUserByWrongName() throws Exception {
        mvc.perform(
                get("/api/user/superman")
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void getUserByNameNoToken() throws Exception {
        mvc.perform(
                get("/api/user/superman")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    private CreateUserRequest getUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin");
        request.setPassword("password");
        request.setConfirmPassword("password");
        return request;
    }
}
