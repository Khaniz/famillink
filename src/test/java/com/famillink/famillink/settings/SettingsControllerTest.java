package com.famillink.famillink.settings;

import com.famillink.famillink.account.AccountRepository;
import com.famillink.famillink.account.AccountService;
import com.famillink.famillink.account.SignUpForm;
import com.famillink.famillink.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

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

    @WithUserDetails(value = "wisrule", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 정상 입력")
    @Test
    void updateProfile() throws Exception { //setupBefore가 잘 작동되고 있음.
        String bio = "간단 소개 수정";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account wisrule = accountRepository.findByNickname("wisrule");
        assertEquals(bio, wisrule.getBio());

    }

    @WithUserDetails(value = "wisrule", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 에러 유발 입력")
    @Test
    void updateProfile_error() throws Exception { //setupBefore가 잘 작동되고 있음.
        String bio = "간단 소개 수정이 너무 긴 경우" +
                "간단 소개 수정이 너무 긴 경우" +
                "간단 소개 수정이 너무 긴 경우" +
                "간단 소개 수정이 너무 긴 경우" +
                "간단 소개 수정이 너무 긴 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account wisrule = accountRepository.findByNickname("wisrule");
        assertNull(wisrule.getBio());

    }
}