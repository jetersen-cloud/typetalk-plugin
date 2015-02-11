package org.jenkinsci.plugins.typetalk.webhookaction;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.Arrays;
import java.util.LinkedList;

public class WebhookExecutorFactory {

    public static WebhookExecutor create(StaplerRequest req, StaplerResponse rsp) {
        WebhookRequest webhookRequest = new WebhookRequest(req);
        webhookRequest.parseBodyToJson();
        try {
            webhookRequest.parseQueryStringToProject();
        } catch (NoSuchProjectException e) {
            // when a project specified with query string is not found
            return new NoSuchProjectExecutor(webhookRequest, rsp, e.getProject());
        }

        LinkedList<String> parameters = new LinkedList<>(Arrays.asList(webhookRequest.getPostMessage().split("\\s+")));
        String botUser = parameters.poll();
        String command = parameters.poll();

        // default command is 'help'
        if (StringUtils.isBlank(command)) {
            return new HelpExecutor(webhookRequest, rsp, botUser, parameters);
        }

        switch (command) {
            case "build":
                String project;
                if (webhookRequest.getProject() == null) {
                    project = parameters.poll();
                } else {
                    project = webhookRequest.getProject().getName();
                }
                if (StringUtils.isBlank(project)) {
                    // show help if project is not specified
                    return HelpExecutor.createBuildHelpExecutor(webhookRequest, rsp, botUser);
                }

                return new BuildExecutor(webhookRequest, rsp, project, parameters);
            case "list":
                String pattern = parameters.poll();
                return new ListExecutor(webhookRequest, rsp, pattern);
            case "help":
                return new HelpExecutor(webhookRequest, rsp, botUser, parameters);
            default:
                return new UndefinedExecutor(webhookRequest, rsp, command);
        }
    }

}
