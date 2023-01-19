package com.famillink.famillink.settings;

import com.famillink.famillink.account.CurrentUser;
import com.famillink.famillink.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        //void타입으로 설정하면, 리퀘스트 받은 URL과 같은 뷰를 보여줌
        //뷰 위치의 변경 등을 위해 따로 설정하는 편이 나을듯하다.
        return "settings/profile";
    }
}
