package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerResponse;

// TODO add help executor
public class HelpExecutor extends UndefinedExecutor {

    public HelpExecutor(WebhookRequest req, StaplerResponse rsp, String command) {
        super(req, rsp, command);
    }

}
