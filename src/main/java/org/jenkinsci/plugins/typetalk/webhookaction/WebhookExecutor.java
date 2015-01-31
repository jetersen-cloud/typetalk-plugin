package org.jenkinsci.plugins.typetalk.webhookaction;

import hudson.model.AbstractProject;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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
        output(message, (AbstractProject) null);
    }

    protected void output(String message, AbstractProject project) {
        output(message, message, project);
    }

    protected void output(String description, List<String> messages) {
        output(description, messages, TypetalkMessage.Emoji.SMILEY);
    }

    protected void output(String description, List<String> messages, TypetalkMessage.Emoji emoji) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messages.size() - 1; i++) {
            builder.append(messages.get(i) + "\n");
        }
        builder.append(messages.get(messages.size() - 1));

        // FIXME 本当なら直接 internal は呼ばないほうがいい。けど、parameterObjects を導入したら解決するので、それで
        outputInternal(Level.INFO, HttpServletResponse.SC_OK, emoji, description, builder.toString(), null);
    }

    protected void output(String description, String message, AbstractProject project) {
        outputInternal(Level.INFO, HttpServletResponse.SC_OK, TypetalkMessage.Emoji.SMILEY, description, message, project);
    }

    protected void outputError(String message) {
        outputError(message, HttpServletResponse.SC_BAD_REQUEST);
    }

    protected void outputError(String message, int status) {
        outputInternal(Level.WARNING, status, TypetalkMessage.Emoji.CRY, message, message, null);
    }

    private void outputInternal(Level level, int status, TypetalkMessage.Emoji emoji, String description, String message, AbstractProject project) {
        try {
            logger.log(level, description);

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
