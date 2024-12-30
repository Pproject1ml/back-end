package org._1mg.tt_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLoginSuccess() throws Exception {
        // 로그인 요청 데이터
        String loginRequest = """
            {
                "oauthId" : 11120,
                "oauthProvider" : "GOOGLE",
                "email" : "tmp@gmail.com"
            }
        """;


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(header().exists("Authorization")) // Authorization 헤더 존재 확인
                .andExpect(header().string("Authorization", org.hamcrest.Matchers.startsWith("Bearer "))); // Bearer 토큰 확인
    }
}

