package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage;
import org.jenkinsci.plugins.typetalk.webhookaction.ResponseParameter;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HelpExecutor extends WebhookExecutor {

    private String botUser;
    private LinkedList<String> parameters;

    public static HelpExecutor createBuildHelpExecutor(WebhookRequest req, StaplerResponse rsp, String botUser) {
        LinkedList<String> parameters = new LinkedList<>();
        parameters.add("build");
        return new HelpExecutor(req, rsp, botUser, parameters);
    }

    public HelpExecutor(WebhookRequest req, StaplerResponse rsp, String botUser, LinkedList<String> parameters) {
        super(req, rsp, "help");
        this.botUser = botUser;
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        ResponseParameter responseParameter = new ResponseParameter(ResponseParameter.flatMessages(getMessages()));
        responseParameter.setDescription("Command [ help ] is executed");
        responseParameter.setEmoji(TypetalkMessage.Emoji.BOOK);

        output(responseParameter);
    }

    private List<String> getMessages() {
        String command = parameters.poll();
        if (StringUtils.isBlank(command)) {
            return getDefaultMessages();
        }

        switch (command) {
            case "build":
                return getBuildMessages();
            case "list":
                return getListMessages();
            default:
                return getDefaultMessages();
        }
    }

    private List<String> getDefaultMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Usage");
        messages.add("```");
        messages.add(botUser + " build <project> (<key=value>)");
        messages.add(botUser + " list (<regexp filter>)");
        messages.add(botUser + " help (<sub command>)");
        messages.add("```");

        return messages;
    }

    private List<String> getBuildMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Usage");
        messages.add("```");
        messages.add(botUser + " build <project> (<key=value>)");
        messages.add("```");
        messages.add(TypetalkMessage.Emoji.BOOK.getSymbol() + " Sample");
        messages.add("```");
        messages.add(botUser + " build helloWorldProject                         | build without parameters");
        messages.add(botUser + " build helloWorldProject 1.0.0                   | build with only single parameter");
        messages.add(botUser + " build helloWorldProject version=1.0.0           | build with single parameter");
        messages.add(botUser + " build helloWorldProject version=1.0.0 env=stage | build with multiple parameters");
        messages.add("```");

        return messages;
    }

    private List<String> getListMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Usage");
        messages.add("```");
        messages.add(botUser + " list (<regexp filter>)");
        messages.add("```");
        messages.add(TypetalkMessage.Emoji.BOOK.getSymbol() + " Sample");
        messages.add("```");
        messages.add(botUser + " list            | list all projects ");
        messages.add(botUser + " list helloWorld | list projects with simple filter");
        messages.add(botUser + " list hel....rld | list projects with regexp filter");
        messages.add("```");

        return messages;
    }

}