package com.famillink.famillink.settings;

import com.famillink.famillink.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    public Notifications(Account account) {
        this.familyCreatedByEmail = account.isFamilyCreatedByEmail();
        this.familyCreatedByWeb = account.isFamilyCreatedByWeb();
        this.familyEnrollmentResultByEmail = account.isFamilyEnrollmentResultByEmail();
        this.familyEnrollmentResultByWeb = account.isFamilyEnrollmentResultByWeb();
        this.familyUpdatedByEmail = account.isFamilyUpdateByEmail();
        this.familyUpdatedByWeb = account.isFamilyUpdateByWeb();
    }

    private boolean familyCreatedByEmail;
    private boolean familyCreatedByWeb;
    private boolean familyEnrollmentResultByEmail;
    private boolean familyEnrollmentResultByWeb;
    private boolean familyUpdatedByEmail;
    private boolean familyUpdatedByWeb;

    //mapper 사용을 위한 객체 이름 맞춰주기
}
