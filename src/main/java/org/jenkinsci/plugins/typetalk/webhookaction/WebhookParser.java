package org.jenkinsci.plugins.typetalk.webhookaction;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class WebhookParser {

    private HttpServletRequest req;

    public WebhookParser(HttpServletRequest req) {
        this.req = req;
    }

    public String parse() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            return parseJson(sb.toString());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    String parseJson(String json) {
        JSONObject jsonObject = JSONObject.fromObject(json);
        return jsonObject.getJSONObject("post").getString("message");
    }

}
