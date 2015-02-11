package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.jenkinsci.plugins.typetalk.webhookaction.ResponseParameter;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerResponse;

public class NoSuchProjectExecutor extends WebhookExecutor {

    private String project;

    public NoSuchProjectExecutor(WebhookRequest req, StaplerResponse rsp, String project) {
        super(req, rsp);
        this.project = project;
    }

    @Override
    public void execute() {
        outputError(new ResponseParameter("Project [ " + project + " ] is not defined"));
    }
}
