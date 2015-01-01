package org.jenkinsci.plugins.typetalk.webhookaction;

import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.HelpExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.ListExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.Arrays;
import java.util.LinkedList;

public class WebhookExecutorFactory {
    public static WebhookExecutor create(StaplerRequest req, StaplerResponse rsp, String message) {
        LinkedList<String> messageList = new LinkedList<>(Arrays.asList(message.split("\\s")));
        String botUser = messageList.poll(); // not used
        String command = messageList.poll();

        switch (command) {
            case "build":
                String job = messageList.poll();
                return new BuildExecutor(req, rsp, job, messageList);
            case "list":
                return new ListExecutor(req, rsp, command);
            case "help":
                return new HelpExecutor(req, rsp, command);
            default:
                return new UndefinedExecutor(req, rsp, command);
        }
    }
}
