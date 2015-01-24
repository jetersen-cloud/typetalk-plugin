package org.jenkinsci.plugins.typetalk.webhookaction;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class WebhookRequest {

    private StaplerRequest req;
    private JSONObject json;

    WebhookRequest() {
        // for test
    }

    public WebhookRequest(StaplerRequest req) {
        this.req = req;
        parseBodyToJson();
    }

    private void parseBodyToJson() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            json = JSONObject.fromObject(sb.toString());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getRemoteAddr() {
        return req.getRemoteAddr();
    }

    public int getPostId() {
        return json.getJSONObject("post").getInt("id");
    }

    public String getPostMessage() {
        return json.getJSONObject("post").getString("message");
    }

}
