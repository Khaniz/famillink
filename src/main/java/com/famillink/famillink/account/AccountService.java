package com.famillink.famillink.account;

import com.famillink.famillink.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignedUpConfirmEmail(newAccount);
    }
    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // TODO encoding
                .familyEnrollmentResultByEmail(true)
                .familyCreatedByWeb(true)
                .familyUpdateByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    private void sendSignedUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Familink 회원가입 인증");
        mailMessage.setText(
                "/check-email-token?token=" +
                        newAccount.getEmailCheckToken() +
                        "&email=" +
                        newAccount.getEmail()
        );
        javaMailSender.send(mailMessage);
    }
}
