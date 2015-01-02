package org.jenkinsci.plugins.typetalk.webhookaction;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class WebhookExecutor {

    protected Logger logger = Logger.getLogger(getClass().getName());

    protected StaplerRequest req;
    protected StaplerResponse rsp;
    protected String command;

    protected WebhookExecutor(StaplerRequest req, StaplerResponse rsp, String command) {
        this.req = req;
        this.rsp = rsp;
        this.command = command;
    }

    public abstract void execute();

    protected void output(String message) {
        outputInternal(Level.INFO, message, HttpServletResponse.SC_OK);
    }

    protected void outputError(String message) {
        outputError(message, HttpServletResponse.SC_BAD_REQUEST);
    }

    protected void outputError(String message, int status) {
        outputInternal(Level.WARNING, message, status);
    }

    private void outputInternal(Level level, String message, int status) {
        try {
            logger.log(level, message);

            rsp.setContentType("text/plain");
            rsp.setStatus(status);
            rsp.getWriter().println(message);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
