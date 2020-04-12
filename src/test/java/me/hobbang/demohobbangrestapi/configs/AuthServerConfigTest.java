package me.hobbang.demohobbangrestapi.configs;

import me.hobbang.demohobbangrestapi.accounts.Account;
import me.hobbang.demohobbangrestapi.accounts.AccountRole;
import me.hobbang.demohobbangrestapi.accounts.AccountService;
import me.hobbang.demohobbangrestapi.common.BaseControllerTest;
import me.hobbang.demohobbangrestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        // Given
        String username = "hobbang2@email.com";
        String password = "pass";
        Account hobbang = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(hobbang);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }
}