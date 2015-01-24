package org.jenkinsci.plugins.typetalk.webhookaction;

import hudson.model.AbstractProject;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage;
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

    protected WebhookExecutor(WebhookRequest req, StaplerResponse rsp, String command) {
        this.req = req;
        this.rsp = rsp;
        this.command = command;
    }

    public abstract void execute();

    protected void output(String message) {
        output(message, null);
    }

    protected void output(String message, AbstractProject project) {
        outputInternal(Level.INFO, HttpServletResponse.SC_OK, TypetalkMessage.Emoji.SMILEY, message, project);
    }

    protected void outputError(String message) {
        outputError(message, HttpServletResponse.SC_BAD_REQUEST);
    }

    protected void outputError(String message, int status) {
        outputInternal(Level.WARNING, status, TypetalkMessage.Emoji.CRY, message, null);
    }

    private void outputInternal(Level level, int status, TypetalkMessage.Emoji emoji, String message, AbstractProject project) {
        try {
            logger.log(level, message);

            rsp.setContentType("application/json");
            rsp.setStatus(status);
            rsp.getWriter().println(buildResponseMessage(emoji, message, project));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildResponseMessage(TypetalkMessage.Emoji emoji, String message, AbstractProject project) {
        JSONObject jsonObject = new JSONObject();

        TypetalkMessage typetalkMessage = new TypetalkMessage(emoji, message);
        jsonObject.element("message", typetalkMessage.messageWithProjectInfo(project));
        jsonObject.element("replyTo", req.getPostId());

        return jsonObject.toString();
    }
}
