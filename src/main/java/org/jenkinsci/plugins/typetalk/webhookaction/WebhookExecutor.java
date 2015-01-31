package org.jenkinsci.plugins.typetalk.webhookaction;

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

    protected void output(ResponseParameter parameter) {
        outputInternal(Level.INFO, HttpServletResponse.SC_OK, parameter);
    }

    protected void outputError(ResponseParameter parameter) {
        outputError(parameter, HttpServletResponse.SC_BAD_REQUEST);
    }

    protected void outputError(ResponseParameter parameter, int status) {
        outputInternal(Level.WARNING, status, parameter);
    }

    private void outputInternal(Level level, int status, ResponseParameter parameter) {
        try {
            logger.log(level, parameter.getDescription());

            rsp.setContentType("application/json");
            rsp.setStatus(status);
            rsp.getWriter().println(buildResponseMessage(parameter));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildResponseMessage(ResponseParameter parameter) {
        JSONObject jsonObject = new JSONObject();

        TypetalkMessage typetalkMessage = new TypetalkMessage(parameter.getEmoji(), parameter.getMessage());
        jsonObject.element("message", typetalkMessage.messageWithProjectInfo(parameter.getProject()));
        jsonObject.element("replyTo", req.getPostId());

        return jsonObject.toString();
    }
}
