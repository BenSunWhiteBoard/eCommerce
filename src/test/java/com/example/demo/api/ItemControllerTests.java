package com.example.demo.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.requests.CreateUserRequest;
import java.net.URI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestMethodOrder(OrderAnnotation.class)
public class ItemControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    static private String jwtToken;

    @Test
    @Order(1)
    public void signUpAndLogin() throws Exception{
        CreateUserRequest request = getUserRequest();
        mvc.perform(
                post(new URI("/api/user/create"))
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

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
    public void getAllItems() throws Exception {
        mvc.perform(
                get("/api/item")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getItemById() throws Exception {
        mvc.perform(
                get("/api/item/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getItemByWrongId() throws Exception {
        mvc.perform(
                get("/api/item/4")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemByWrongName() throws Exception {
        mvc.perform(
                get("/api/name/wrongName")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private CreateUserRequest getUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin2");
        request.setPassword("password");
        request.setConfirmPassword("password");
        return request;
    }

}
