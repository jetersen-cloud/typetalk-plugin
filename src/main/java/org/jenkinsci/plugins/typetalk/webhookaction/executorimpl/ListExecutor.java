package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerResponse;

// TODO add list executor ( with regexp filter )
public class ListExecutor extends UndefinedExecutor {

    public ListExecutor(WebhookRequest req, StaplerResponse rsp, String command) {
        super(req, rsp, command);
    }

}
