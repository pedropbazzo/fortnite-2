package io.github.robertograham.fortniteclienttwo.implementation;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

final class AuthenticationResource {

    private static final URI TOKEN_URI = URI.create("https://account-public-service-prod03.ol.epicgames.com/account/api/oauth/token");
    private static final URI EXCHANGE_URI = URI.create("https://account-public-service-prod03.ol.epicgames.com/account/api/oauth/exchange");
    private static final URI KILL_TOKEN_URI = URI.create("https://account-public-service-prod03.ol.epicgames.com/account/api/oauth/sessions/kill/");
    private static final NameValuePair GRANT_TYPE_PASSWORD = new BasicNameValuePair("grant_type", "password");
    private static final NameValuePair GRANT_TYPE_EXCHANGE_CODE = new BasicNameValuePair("grant_type", "exchange_code");
    private static final NameValuePair GRANT_TYPE_REFRESH_TOKEN = new BasicNameValuePair("grant_type", "refresh_token");
    private static final NameValuePair TOKEN_TYPE_EG1 = new BasicNameValuePair("token_type", "eg1");
    private static final NameValuePair INCLUDE_PERMS_TRUE = new BasicNameValuePair("includePerms", String.valueOf(true));
    private final HttpClient httpClient;

    private AuthenticationResource(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    static AuthenticationResource newInstance(HttpClient httpClient) {
        return new AuthenticationResource(httpClient);
    }

    private Optional<Token> postForToken(String authorizationHeaderToken,
                                         NameValuePair... urlEncodedFormParts) throws IOException {
        final HttpPost httpPost = new HttpPost(TOKEN_URI);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, String.format("basic %s", authorizationHeaderToken));
        httpPost.setEntity(
                new UrlEncodedFormEntity(
                        Arrays.asList(urlEncodedFormParts)
                )
        );
        return httpClient.execute(
                httpPost,
                ResponseHandlerProvider.INSTANCE.responseHandlerForClass(Token.class)
        );
    }

    Optional<Token> passwordGrantedToken(String epicGamesEmailAddress, String epicGamesPassword, String epicGamesLauncherToken) throws IOException {
        return postForToken(
                epicGamesLauncherToken,
                GRANT_TYPE_PASSWORD,
                INCLUDE_PERMS_TRUE,
                new BasicNameValuePair("username", epicGamesEmailAddress),
                new BasicNameValuePair("password", epicGamesPassword)
        );
    }

    Optional<Exchange> accessTokenGrantedExchange(String accessToken) throws IOException {
        final HttpGet httpGet = new HttpGet(EXCHANGE_URI);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, String.format("bearer %s", accessToken));
        return httpClient.execute(
                httpGet,
                ResponseHandlerProvider.INSTANCE.responseHandlerForClass(Exchange.class)
        );
    }

    Optional<Token> exchangeCodeGrantedToken(String exchangeCode, String fortniteClientToken) throws IOException {
        return postForToken(
                fortniteClientToken,
                GRANT_TYPE_EXCHANGE_CODE,
                TOKEN_TYPE_EG1,
                INCLUDE_PERMS_TRUE,
                new BasicNameValuePair("exchange_code", exchangeCode)
        );
    }

    Optional<Token> refreshTokenGrantedToken(String refreshToken, String fortniteClientToken) throws IOException {
        return postForToken(
                fortniteClientToken,
                GRANT_TYPE_REFRESH_TOKEN,
                INCLUDE_PERMS_TRUE,
                new BasicNameValuePair("refresh_token", refreshToken)
        );
    }

    void retireAccessToken(String accessToken) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(
                String.format(
                        "%s%s",
                        KILL_TOKEN_URI,
                        accessToken
                )
        );
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION, String.format("bearer %s", accessToken));
        httpClient.execute(
                httpDelete,
                ResponseHandlerProvider.STRING_OPTIONAL_RESPONSE_HANDLER
        );
    }
}
