package com.example.demo.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import java.net.URI;
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
public class CartControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<ModifyCartRequest> json;

    @Autowired
    private JacksonTester<CreateUserRequest> json2;

    static private String jwtToken;

    @Test
    @Order(1)
    public void signUpAndLogin() throws Exception{
        CreateUserRequest request = getUserRequest();
        mvc.perform(
                post(new URI("/api/user/create"))
                        .content(json2.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult result = mvc.perform(
                post(new URI("/login"))
                        .content(json2.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        jwtToken = result.getResponse().getHeader("Authorization");
    }

    @Test
    @Order(2)
    public void addToCart() throws Exception {
        ModifyCartRequest request = getCartRequest();
        mvc.perform(
                post("/api/cart/addToCart")
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void removeFromCart() throws Exception {
        ModifyCartRequest request = getCartRequest();
        mvc.perform(
                post("/api/cart/removeFromCart")
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void removeNotExist() throws Exception {
        ModifyCartRequest request = getCartRequest();
        request.setItemId(3);
        mvc.perform(
                post("/api/cart/removeFromCart")
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private ModifyCartRequest getCartRequest(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(getUserRequest().getUsername());
        request.setItemId(2);
        request.setQuantity(4);
        return request;
    }

    private CreateUserRequest getUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin3");
        request.setPassword("password");
        request.setConfirmPassword("password");
        return request;
    }
}
