package com.famillink.famillink.settings;

import com.famillink.famillink.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    public Notifications(Account account) {
        this.famCreatedByEmail = account.isFamilyCreatedByEmail();
        this.famCreatedByWeb = account.isFamilyCreatedByWeb();
        this.famEnrollmentResultByEmail = account.isFamilyEnrollmentResultByEmail();
        this.famEnrollmentResultByWeb = account.isFamilyEnrollmentResultByWeb();
        this.famUpdatedByEmail = account.isFamilyUpdateByEmail();
        this.famUpdatedByWeb = account.isFamilyUpdateByWeb();
    }

    private boolean famCreatedByEmail;
    private boolean famCreatedByWeb;
    private boolean famEnrollmentResultByEmail;
    private boolean famEnrollmentResultByWeb;
    private boolean famUpdatedByEmail;
    private boolean famUpdatedByWeb;



}
