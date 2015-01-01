package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

// TODO add list executor ( with regexp filter )
public class ListExecutor extends UndefinedExecutor {

    public ListExecutor(StaplerRequest req, StaplerResponse rsp, String command) {
        super(req, rsp, command);
    }

}
