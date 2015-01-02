package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.logging.Logger;

public class UndefinedExecutor extends WebhookExecutor {

    public UndefinedExecutor(StaplerRequest req, StaplerResponse rsp, String command) {
        super(req, rsp, command);
    }

    @Override
    public void execute() {
        outputError("command '" + command + "' is not defined");
    }
}
