package org.jenkinsci.plugins.typetalk.webhookaction;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.util.QueryParameterMap;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class WebhookRequest {

    private StaplerRequest req;

    private JSONObject json;

    private AbstractProject project;

    public WebhookRequest(StaplerRequest req)  {
        this.req = req;
    }

    public void parseQueryStringToProject() {
        String p = new QueryParameterMap(req).get("project");
        if (StringUtils.isNotBlank(p)) {
            TopLevelItem item = Jenkins.getInstance().getItem(p);
            if (!(item instanceof AbstractProject)) {
                throw new NoSuchProjectException(p);
            }
            project = ((AbstractProject) item);
        }
    }

    public void parseBodyToJson() {
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

    public AbstractProject getProject() {
        return project;
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
