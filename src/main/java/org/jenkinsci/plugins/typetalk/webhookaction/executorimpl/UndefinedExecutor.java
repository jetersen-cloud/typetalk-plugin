package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerResponse;

public class UndefinedExecutor extends WebhookExecutor {

    public UndefinedExecutor(WebhookRequest req, StaplerResponse rsp, String command) {
        super(req, rsp, command);
    }

    @Override
    public void execute() {
        outputError("command '" + command + "' is not defined");
    }
}
