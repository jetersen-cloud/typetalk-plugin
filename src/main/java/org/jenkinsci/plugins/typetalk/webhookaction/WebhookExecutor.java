package org.jenkinsci.plugins.typetalk.webhookaction;

import net.sf.json.JSONObject;
import org.jenkinsci.plugins.typetalk.support.Emoji;
import org.jenkinsci.plugins.typetalk.support.TypetalkMessage;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class WebhookExecutor {

    protected Logger logger = Logger.getLogger(getClass().getName());

    protected WebhookRequest req;
    protected StaplerResponse rsp;
    protected String command;

    protected WebhookExecutor(WebhookRequest req, StaplerResponse rsp) {
        this(req, rsp, null);
    }

    protected WebhookExecutor(WebhookRequest req, StaplerResponse rsp, String command) {
        this.req = req;
        this.rsp = rsp;
        this.command = command;
    }

    public abstract void execute();

    protected void output(ResponseParameter parameter) {
        outputInternal(Level.INFO, parameter);
    }

    protected void outputError(ResponseParameter parameter) {
        parameter.setEmoji(Emoji.CRY);
        outputInternal(Level.WARNING, parameter);
    }

    private void outputInternal(Level level, ResponseParameter parameter) {
        try {
            logger.log(level, parameter.getDescription());

            rsp.setContentType("application/json; charset=utf-8");
            rsp.setStatus(HttpServletResponse.SC_OK);
            rsp.getWriter().println(buildResponseMessage(parameter));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildResponseMessage(ResponseParameter parameter) {
        JSONObject jsonObject = new JSONObject();

        TypetalkMessage typetalkMessage = new TypetalkMessage(parameter.getEmoji(), parameter.getMessage());
        jsonObject.element("message", typetalkMessage.buildMessageWithProject(parameter.getProject()));
        jsonObject.element("replyTo", req.getPostId());

        // If '#' is included in the message, Typetalk will create a tag.
        // Set true to prevent from creating tag.
        // Tags might be created unexpectedly since `#` might be included in the string passed by Jenkins.
        jsonObject.element("ignoreHashtag", true);

        return jsonObject.toString();
    }
}
