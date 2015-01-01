package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

// TODO add help executor
public class HelpExecutor extends UndefinedExecutor {

    public HelpExecutor(StaplerRequest req, StaplerResponse rsp, String command) {
        super(req, rsp, command);
    }

}
