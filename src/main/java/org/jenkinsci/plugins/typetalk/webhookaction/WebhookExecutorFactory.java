package org.jenkinsci.plugins.typetalk.webhookaction;

import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WebhookExecutorFactory {
    public static WebhookExecutor create(StaplerRequest req, StaplerResponse rsp, String message) {
        LinkedList<String> messageList = new LinkedList<>(Arrays.asList(message.split("\\s")));
        String botUser = messageList.poll(); // not used
        String command = messageList.poll();

        switch (command) {
            case "build":
                String job = messageList.poll();
                return new BuildExecutor(req, rsp, job, messageList);
            default:
                return new UndefinedExecutor(req, rsp, command);
        }
    }
}
