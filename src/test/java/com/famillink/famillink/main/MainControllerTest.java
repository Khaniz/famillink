package com.famillink.famillink.main;

import com.famillink.famillink.account.AccountRepository;
import com.famillink.famillink.account.AccountService;
import com.famillink.famillink.account.SignUpForm;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("gjf.kee@gmail.com");
        signUpForm.setNickname("wisrule");
        signUpForm.setPassword("dnflEkfchlrh");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("이메일로 로그인")
    @Test
    void loginWithEmail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "gjf.kee@gmail.com")
//                        .param("email", "gjf.kee@gmail.com")
                        .param("password", "dnflEkfchlrh")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @DisplayName("닉네임으로로 로그인")
    @Test
    void loginWithNickname() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "wisrule")
//                        .param("email", "gjf.kee@gmail.com")
                        .param("password", "dnflEkfchlrh")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @DisplayName("로그인 실패")
    @Test
    void loginFailed() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "1112345")
                        .param("password", "1234123656")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}