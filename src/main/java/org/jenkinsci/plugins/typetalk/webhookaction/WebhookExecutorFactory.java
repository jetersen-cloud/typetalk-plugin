package org.jenkinsci.plugins.typetalk.webhookaction;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.HelpExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.ListExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor;
import org.kohsuke.stapler.StaplerResponse;

import java.util.Arrays;
import java.util.LinkedList;

public class WebhookExecutorFactory {
    public static WebhookExecutor create(WebhookRequest req, StaplerResponse rsp) {
        LinkedList<String> parameters = new LinkedList<>(Arrays.asList(req.getPostMessage().split("\\s+")));
        String botUser = parameters.poll(); // not used
        String command = parameters.poll();

        // default command is 'help'
        if (StringUtils.isBlank(command)) {
            return new HelpExecutor(req, rsp, botUser, parameters);
        }

        switch (command) {
            case "build":
                String job = parameters.poll();
                if (StringUtils.isBlank(job)) {
                    // show help if job is not specified
                    return HelpExecutor.createBuildHelpExecutor(req, rsp, botUser);
                }

                return new BuildExecutor(req, rsp, job, parameters);
            case "list":
                return new ListExecutor(req, rsp, command);
            case "help":
                return new HelpExecutor(req, rsp, botUser, parameters);
            default:
                return new UndefinedExecutor(req, rsp, command);
        }
    }
}
