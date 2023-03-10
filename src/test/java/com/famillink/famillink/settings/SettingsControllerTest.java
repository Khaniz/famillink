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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    PasswordEncoder passwordEncoder;

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
    @DisplayName("????????? ???????????? - ?????? ??????")
    @Test
    void updateProfile() throws Exception { //setupBefore??? ??? ???????????? ??????.
        String bio = "?????? ?????? ??????";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account wisrule = accountRepository.findByNickname("wisrule");
        assertEquals(bio, wisrule.getBio());

    }

    @WithUserDetails(value = "wisrule", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ?????? ?????? ??????")
    @Test
    void updateProfile_error() throws Exception { //setupBefore??? ??? ???????????? ??????.
        String bio = "?????? ?????? ????????? ?????? ??? ??????" +
                "?????? ?????? ????????? ?????? ??? ??????" +
                "?????? ?????? ????????? ?????? ??? ??????" +
                "?????? ?????? ????????? ?????? ??? ??????" +
                "?????? ?????? ????????? ?????? ??? ??????";
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

    @WithUserDetails(value = "wisrule", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? ???")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordDomain"));

    }

    @WithUserDetails(value = "wisrule", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? ??????")
    @Test
    void updatePassword_normal() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("confirmNewPassword", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account wisrule = accountRepository.findByNickname("wisrule");
        assertTrue(passwordEncoder.matches("12345678", wisrule.getPassword()));
    }

    @WithUserDetails(value = "wisrule", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? ?????????")
    @Test
    void updatePassword_abnormal() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("confirmNewPassword", "12345679")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordDomain"))
                .andExpect(model().attributeExists("account"));



    }

    //TODO ?????? ???????????? ????????? ?????? ??????
}