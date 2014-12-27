package org.jenkinsci.plugins.typetalk.webhookaction;

import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebhookExecutorFactory {
    public static WebhookExecutor create(HttpServletRequest req, HttpServletResponse rsp, String message) {
        String[] splitMessage = message.split("\\s");
        String command = splitMessage[1];

        switch (command) {
            case "build":
                String job = splitMessage[2];
                return new BuildExecutor(req, rsp, job);
            default:
                return new UndefinedExecutor(req, rsp, command);
        }
    }
}
