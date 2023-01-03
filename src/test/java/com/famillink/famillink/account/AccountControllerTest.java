package com.famillink.famillink.account;

import com.famillink.famillink.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_validation_input_error() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "asldkjba;lskdf")
                        .param("email", "email@email.com")
                ).andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"));
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    void checkEmailToken_validation_input_accept() throws Exception {
        Account account = Account.builder()
                .email("wisrule@gmail.com")
                .password("12345678")
                .nickname("wisdomRule")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailCheckToken())
                        .param("email", newAccount.getEmail())
                ).andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"));
    }


    @DisplayName("회원 가입 화면 확인")
    @Test
    void
    securityFilterChain() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_validation_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","wisrule")
                .param("email","wisrule@gmail.com")
                .param("password","wisruler!@")
                .with(csrf())
        ).andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/"));

        Account byEmail = accountRepository.findByEmail("wisrule@gmail.com");
        assertNotNull(byEmail);
        assertNotEquals(byEmail.getPassword(), "wisruler!@"); //bcrypt 되어 다르다는걸 확인

//        assertTrue(accountRepository.existsByEmail("wisrule@gmail.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }



}