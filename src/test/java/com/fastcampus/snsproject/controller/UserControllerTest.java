package com.fastcampus.snsproject.controller;

import com.fastcampus.snsproject.controller.request.UserJoinRequest;
import com.fastcampus.snsproject.controller.request.UserLoginRequest;
import com.fastcampus.snsproject.exception.ErrorCode;
import com.fastcampus.snsproject.exception.SnsApplicationException;
import com.fastcampus.snsproject.model.User;
import com.fastcampus.snsproject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public  void 회원가입() throws Exception {
        String userName = "userName";
        String password = "password";

        //todo : mocking
        when(userService.join(userName,password)).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                //TODO : add request body
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName,password)))
        ).andDo(print())
                .andExpect(status().isOk());


    }

    @Test
    public  void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는_경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName,password)).thenThrow(new SnsApplicationException(
                ErrorCode.DUPLICATED_USER_NAME));

        //TODO : mocking
        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        //TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName,password)))
                ).andDo(print())
                .andExpect(status().isConflict());


    }

    @Test
    public  void 로그인() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName,password)).thenReturn("test_token");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName,password)))
                ).andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    public  void 로그인시_회원가입이_안된_userName을_입력할경우_에러봔한() throws Exception {
        String userName = "userName";
        String password = "password";


        when(userService.login(userName,password)).thenThrow( new SnsApplicationException(
                ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        //TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName,password)))
                ).andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    public  void 로그인시_틀린_PW를_입력할경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        //todo : mocking
        when(userService.login(userName,password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        //TODO : add request body
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName,password)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());

    }
}
