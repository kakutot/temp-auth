import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import src.App;
import src.controller.TokenRevokeController;
import src.model.UserRole;
import src.model.UserSecured;
import src.repository.UserRoleRepository;
import src.security.OauthConsts;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@ActiveProfiles("mvc")
public class AuthTests {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private WebApplicationContext wac;
    @Autowired private FilterChainProxy springSecurityFilterChain;
    @Autowired private UserRoleRepository userRoleRepository;

    private MockMvc mockMvc;

    private static final String DEFAULT_CLIENT_ID = OauthConsts.DEFAULT_CLIENT_ID;
    private static final String DEFAULT_CLIENT_SECRET = OauthConsts.DEFAULT_CLIENT_SECRET;

    private static final String TRUNCATED_CLIENT_ID = OauthConsts.TRUNCATED_CLIENT_ID;
    private static final String TRUNCATED_CLIENT_SECRET = OauthConsts.TRUNCATED_CLIENT_SECRET;

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String USERNAME = "Roma";
    private static final String PASSWORD = "ab";
    private static String REVOKED_TOKEN;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
        REVOKED_TOKEN = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        DEFAULT_CLIENT_ID,
                        DEFAULT_CLIENT_SECRET,
                        null,
                        status().isOk()));
    }

    private MultiValueMap<String, String> prepareParams(String username,
                                                        String password,
                                                        String clientId,
                                                        String clientSecret,
                                                        String grantType) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType == null || grantType.isEmpty() ? "password" : grantType);
        params.add("client_id", clientId);
        params.add("secret", clientSecret);
        params.add("username", username);
        params.add("password", password);;

        return params;
    }

    private String extractAccessToken(ResultActions resultActions) throws UnsupportedEncodingException {
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private ResultActions obtainAccessToken(String username,
                                            String password,
                                            String clientId,
                                            String clientSecret,
                                            String grantType,
                                            ResultMatcher statusResultMatcher) throws Exception {
        ResultActions result = mockMvc.perform(post("/oauth/token")
                .params(prepareParams(username, password, clientId, clientSecret, grantType))
                .with(httpBasic(clientId, clientSecret))
                .accept(CONTENT_TYPE))
                .andExpect(statusResultMatcher)
                .andExpect(content().contentType(CONTENT_TYPE));

        return result;
    }

    @Test
    public void givenGrantTypePassword_thenOk() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .params(prepareParams(USERNAME, PASSWORD, DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET, null))
                .with(httpBasic(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET))
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));
    }

    @Test
    public void givenGrantTypeClientDetails_ForVaidClient_thenOk() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .params(prepareParams(USERNAME, PASSWORD, DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET, "client_credentials"))
                .with(httpBasic(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET))
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));
    }

    @Test
    public void givenInvalidGrantType_thenBadRequest() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        mockMvc.perform(post("/oauth/token")
                .params(prepareParams(USERNAME, PASSWORD, DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET, "temp"))
                .with(httpBasic(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET))
                .accept(CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE));
    }

    @Test
    public void givenValidClientAndToken_whenGetSecureRequest_thenOk() throws Exception {
        final String accessToken = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        DEFAULT_CLIENT_ID,
                        DEFAULT_CLIENT_SECRET,
                        null,
                        status().isOk()));
        logger.info("token:" + accessToken);
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + accessToken)
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    public void givenValidClientAndRandomUser_whenGetSecureRequest_thenBadRequest() throws Exception {
        obtainAccessToken("temp",
                PASSWORD,
                TRUNCATED_CLIENT_ID,
                DEFAULT_CLIENT_SECRET,
                null,
                status().isBadRequest());
    }

    @Test
    public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenInvalidClient_whenGetSecureRequest_thenForbidden() throws Exception {
        final String accessToken = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        TRUNCATED_CLIENT_ID,
                        TRUNCATED_CLIENT_SECRET,
                        null,
                        status().isOk()));
        System.out.println("token:" + accessToken);
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + accessToken)
                .accept(CONTENT_TYPE))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenValidToken_thenNotRevoked() throws Exception {
        final String accessToken = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        DEFAULT_CLIENT_ID,
                        DEFAULT_CLIENT_SECRET,
                        null,
                        status().isOk()));
        System.out.println("token:" + accessToken);
        mockMvc.perform(get("/revoke-token/" + accessToken)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void givenRevokedToken_thenRevoked() throws Exception {
        mockMvc.perform(get("/revoke-token/" + REVOKED_TOKEN)
                .header("Authorization", "Bearer " + REVOKED_TOKEN)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void givenToken_thenRevokedIsOk() throws Exception {
        final String accessToken = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        DEFAULT_CLIENT_ID,
                        DEFAULT_CLIENT_SECRET,
                        null,
                        status().isOk()));
        TokenRevokeController.TokenV tokenV = new TokenRevokeController.TokenV();
        tokenV.setToken(accessToken);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(tokenV);

        mockMvc.perform(post("/revoke-token")
                .header("Authorization", "Bearer " + REVOKED_TOKEN)
                .content(requestJson)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    public void givenTokenAndValidClient_thenCreateUser() throws Exception {
        final String accessToken = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        DEFAULT_CLIENT_ID,
                        DEFAULT_CLIENT_SECRET,
                        null,
                        status().isOk()));
        UserSecured userSecured = new UserSecured();
        userSecured.setId(123l);
        userSecured.setUsername("temp");
        userSecured.setEnabled(true);
        userSecured.setPassword("test");
        userSecured.setUserRoles(Collections.singleton(userRoleRepository.findFirstByName(UserRole.USER_ROLE.USER).get()));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(userSecured);

        mockMvc.perform(post("/user")
                .header("Authorization", "Bearer " + accessToken)
                .content(requestJson)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                .andExpect(status().isCreated());
    }

    @Test
    public void givenTokenAndValidClient_ButUsernameExisted_thenBadRequest() throws Exception {
        final String accessToken = extractAccessToken(
                obtainAccessToken(USERNAME,
                        PASSWORD,
                        DEFAULT_CLIENT_ID,
                        DEFAULT_CLIENT_SECRET,
                        null,
                        status().isOk()));
        UserSecured userSecured = new UserSecured();
        userSecured.setId(123l);
        userSecured.setUsername("temp");
        userSecured.setEnabled(true);
        userSecured.setPassword("test");
        userSecured.setUserRoles(Collections.singleton(userRoleRepository.findFirstByName(UserRole.USER_ROLE.USER).get()));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(userSecured);

        mockMvc.perform(post("/user")
                .header("Authorization", "Bearer " + accessToken)
                .content(requestJson)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

}