package com.famillink.famillink.account;

import com.famillink.famillink.domain.Account;
import com.famillink.famillink.settings.Notifications;
import com.famillink.famillink.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignedUpConfirmEmail(newAccount);
        return newAccount;
    }
    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // encoded
                .familyEnrollmentResultByEmail(true)
                .familyCreatedByWeb(true)
                .familyUpdateByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendSignedUpConfirmEmail(Account newAccount) {
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

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true) //write lock을 하지않기때문에 성능에 유리한 설정
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);

    }

    public void updateProfile(Account account, Profile profile) {

//        account.setUrl(profile.getUrl());
//        account.setBio(profile.getBio());
//        account.setOccupation(profile.getOccupation());
//        account.setLocation(profile.getLocation());
//        account.setProfileImage(profile.getProfileImage());
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

    }


    public void updateNotifications(Account account, Notifications notifications) {
//        account.setFamilyCreatedByWeb(notifications.isFamCreatedByWeb());
//        account.setFamilyCreatedByEmail(notifications.isFamCreatedByEmail());
//        account.setFamilyUpdateByWeb(notifications.isFamUpdatedByWeb());
//        account.setFamilyUpdateByWeb(notifications.isFamUpdatedByWeb());
//        account.setFamilyEnrollmentResultByWeb(notifications.isFamEnrollmentResultByWeb());
//        account.setFamilyEnrollmentResultByEmail(notifications.isFamEnrollmentResultByEmail());
        modelMapper.map(notifications,account);
        accountRepository.save(account); //실제로 업데이트 반영 안됨;;
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }
}
