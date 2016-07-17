package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import hudson.model.*;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.support.Emoji;
import org.jenkinsci.plugins.typetalk.support.ResultSupport;
import org.jenkinsci.plugins.typetalk.support.TypetalkMessage;
import org.jenkinsci.plugins.typetalk.webhookaction.NoSuchProjectException;
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
        try {
            ResponseParameter responseParameter = createResponseParameter();
            responseParameter.setDescription("Command [ help ] is executed");
            if (!responseParameter.isEmojiSet()) {
                responseParameter.setEmoji(Emoji.BOOK);
            }

            output(responseParameter);
        } catch (NoSuchProjectException e) {
            outputError(new ResponseParameter("Project [ " + e.getProject() + " ] is not found"));
        }
    }

    private ResponseParameter createResponseParameter() {
        if (req.getProject() == null) {
            String command = parameters.poll();
            if (StringUtils.isBlank(command)) {
                return getDefaultResponseParameter();
            }

            switch (command) {
                case "build":
                    String project = parameters.poll();
                    if (StringUtils.isBlank(project)) {
                        return getBuildResponseParameter();
                    } else {
                        return getProjectBuildResponseParameter(project, false);
                    }
                case "list":
                    return getListResponseParameter();
                default:
                    return getDefaultResponseParameter();
            }
        } else {
            return getProjectBuildResponseParameter(req.getProject().getName(), true);
        }
    }

    private ResponseParameter getDefaultResponseParameter() {
        List<String> messages = new ArrayList<>();
        messages.add("Usage");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(botUser + " build <project> (<key=value>)");
        messages.add(botUser + " list (<regexp filter>)");
        messages.add(botUser + " help (<sub command>)");
        messages.add(TypetalkMessage.CODE_SEPARATOR);

        return new ResponseParameter(ResponseParameter.flatMessages(messages));
    }

    private ResponseParameter getBuildResponseParameter() {
        List<String> messages = new ArrayList<>();
        messages.add("Usage");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(botUser + " build <project> (<key=value>)");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(Emoji.BOOK.getSymbol() + " Sample");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(botUser + " build helloWorldProject                         | build without parameters");
        messages.add(botUser + " build helloWorldProject 1.0.0                   | build with only single parameter");
        messages.add(botUser + " build helloWorldProject version=1.0.0           | build with single parameter");
        messages.add(botUser + " build helloWorldProject version=1.0.0 env=stage | build with multiple parameters");
        messages.add(TypetalkMessage.CODE_SEPARATOR);

        return new ResponseParameter(ResponseParameter.flatMessages(messages));
    }

    private ResponseParameter getProjectBuildResponseParameter(String p, boolean specifiedProjectWithQueryString) {
        TopLevelItem item = Jenkins.getInstance().getItem(p);
        if (!(item instanceof Job)) {
            throw new NoSuchProjectException(p);
        }
        Job project = ((Job) item);
        ParametersDefinitionProperty property = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);

        // description
        List<String> messages = new ArrayList<>();
        messages.add(project.getName());
        if (StringUtils.isBlank(project.getDescription())) {
            messages.add("");
        } else {
            messages.add(TypetalkMessage.CODE_SEPARATOR);
            messages.add(project.getDescription());
            messages.add(TypetalkMessage.CODE_SEPARATOR);
        }

        // usage
        messages.add(Emoji.BOOK.getSymbol() + " Usage");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        String specifiedProject;
        if (specifiedProjectWithQueryString) {
            specifiedProject = "";
        } else {
            specifiedProject = " " + p;
        }
        String option;
        if (property == null) {
            option = "";
        } else if (property.getParameterDefinitions().size() == 1) {
            option = " <value>";
        } else {
            option = " <key=value>";
        }
        messages.add(botUser + " build" + specifiedProject + option);
        messages.add(TypetalkMessage.CODE_SEPARATOR);

        // parameter ( if defined )
        if (property != null) {
            messages.add(Emoji.BOOK.getSymbol() + " Parameters");
            messages.add(TypetalkMessage.CODE_SEPARATOR);

            int maxParameterLength = 0;
            for (ParameterDefinition pd : property.getParameterDefinitions()) {
                maxParameterLength = Math.max(maxParameterLength, pd.getName().length());
            }
            String format = "%" + maxParameterLength + "s : %s";
            for (ParameterDefinition pd : property.getParameterDefinitions()) {
                String description = StringUtils.isNotBlank(pd.getDescription()) ? pd.getDescription() : "(no description)";
                messages.add(String.format(format, pd.getName(), description));
            }

            messages.add(TypetalkMessage.CODE_SEPARATOR);
        }

        ResponseParameter responseParameter = new ResponseParameter(ResponseParameter.flatMessages(messages));
        responseParameter.setEmoji(new ResultSupport().convertProjectToEmoji(project));
        responseParameter.setProject(project);

        return responseParameter;
    }

    private ResponseParameter getListResponseParameter() {
        List<String> messages = new ArrayList<>();
        messages.add("Usage");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(botUser + " list (<regexp filter>)");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(Emoji.BOOK.getSymbol() + " Sample");
        messages.add(TypetalkMessage.CODE_SEPARATOR);
        messages.add(botUser + " list            | list all projects ");
        messages.add(botUser + " list helloWorld | list projects with simple filter");
        messages.add(botUser + " list hel....rld | list projects with regexp filter");
        messages.add(TypetalkMessage.CODE_SEPARATOR);

        return new ResponseParameter(ResponseParameter.flatMessages(messages));
    }

}
