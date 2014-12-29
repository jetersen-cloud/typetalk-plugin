package org.jenkinsci.plugins.typetalk.webhookaction;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class WebhookExecutor {
    protected StaplerRequest req;
    protected StaplerResponse rsp;
    protected String command;
    protected PrintWriter writer;

    protected WebhookExecutor(StaplerRequest req, StaplerResponse rsp, String command) {
        this.req = req;
        this.rsp = rsp;
        this.command = command;

        rsp.setContentType("text/plain");

        try {
            writer = rsp.getWriter();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public abstract void execute();
}
