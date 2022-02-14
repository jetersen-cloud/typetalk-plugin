package org.jenkinsci.plugins.typetalk.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jenkins.model.Jenkins;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jenkinsci.plugins.typetalk.TypetalkNotifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Typetalk {

    private static final String DEFAULT_BASE_URL = "https://typetalk.com";

    private final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String clientId;

    private final String clientSecret;

    public Typetalk(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public static Typetalk createFromName(String name) {
        final Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins != null) {
            final TypetalkNotifier.DescriptorImpl descriptor = (TypetalkNotifier.DescriptorImpl) jenkins.getDescriptor(TypetalkNotifier.class);
            if (descriptor != null) {
                final TypetalkNotifier.Credential credential = descriptor.getCredential(name);
                if (credential != null) {
                    return new Typetalk(credential.getClientId(), credential.getClientSecret().getPlainText());
                }
                throw new IllegalArgumentException("Credential is not found.");
            }
            throw new NullPointerException("Descriptor is null");
        }
        throw new NullPointerException("Jenkins is not started or is stopped");
    }

    /**
     * Post a message to Typetalk
     */
    public int postMessage(final Long topicId, final String message, final Long talkId)
            throws IOException {

        try (final CloseableHttpClient httpClient = httpClientBuilder.build()) {
            final String accessToken = retrieveAccessToken(httpClient);
            final StatusLine statusLine = postMessage(httpClient, accessToken, topicId, message, talkId);
            return statusLine.getStatusCode();
        }
    }

    private StatusLine postMessage(final HttpClient httpClient, final String accessToken, final Long topicId, final String message, final Long talkId) throws IOException {
        final MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessage(message);
        messageEntity.setTalkIds(new Long[]{talkId});

        // If '#' is included in the message, Typetalk will create a tag.
        // Set true to prevent from creating tag.
        // Tags might be created unexpectedly since `#` might be included in the string passed by Jenkins.
        messageEntity.setIgnoreHashtag(true);

        final StringEntity entity = new StringEntity(objectMapper.writeValueAsString(messageEntity), StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.toString());

        final HttpPost post = new HttpPost(buildTopicURL(topicId));
        post.setEntity(entity);
        post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        final HttpResponse response = httpClient.execute(post);

        return response.getStatusLine();
    }

    private String retrieveAccessToken(final HttpClient client) throws IOException {
        final List<NameValuePair> params = Arrays.asList(
                new BasicNameValuePair("client_id", clientId),
                new BasicNameValuePair("client_secret", clientSecret),
                new BasicNameValuePair("grant_type", "client_credentials"),
                new BasicNameValuePair("scope", "topic.post")
        );

        final HttpPost post = new HttpPost(buildAccessTokenURL());
        post.setEntity(new UrlEncodedFormEntity(params));

        final HttpResponse response = client.execute(post);

        final JsonNode root = objectMapper.readTree(response.getEntity().getContent());
        return root.get("access_token").asText();
    }

    private static String getDefaultBaseUrl() {
        final String baseUrl = System.getenv("TYPETALK_BASE_URL");
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }
        return DEFAULT_BASE_URL;
    }

    private static String buildAccessTokenURL() {
        return getDefaultBaseUrl() + "/oauth2/access_token";
    }

    private static String buildTopicURL(final long topicId) {
        return getDefaultBaseUrl() + "/api/v1/topics/" + topicId;
    }
}
