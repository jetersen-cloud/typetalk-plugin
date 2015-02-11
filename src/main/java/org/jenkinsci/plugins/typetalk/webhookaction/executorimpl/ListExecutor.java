package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.support.Emoji;
import org.jenkinsci.plugins.typetalk.support.ResultSupport;
import org.jenkinsci.plugins.typetalk.webhookaction.ResponseParameter;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ListExecutor extends WebhookExecutor {

    private static final String PROJECT_MESSAGE_FORMAT = "%s [%s](%s)";

    private Pattern pattern;

    public ListExecutor(WebhookRequest req, StaplerResponse rsp, String pattern) {
        super(req, rsp, "list");

        if (StringUtils.isNotBlank(pattern)) {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public void execute() {
        ArrayList<String> messages = new ArrayList<>();
        Emoji CommandEmoji;

        for (TopLevelItem item : Jenkins.getInstance().getItems()) {
            if (!(item instanceof AbstractProject)) {
                continue;
            }
            AbstractProject project = ((AbstractProject) item);

            if (pattern != null && !pattern.matcher(project.getName()).find()) {
                continue;
            }

            Emoji projectEmoji = new ResultSupport().convertProjectToEmoji(project);
            messages.add(String.format(PROJECT_MESSAGE_FORMAT, projectEmoji.getSymbol(), project.getName(), project.getAbsoluteUrl()));
        }

        if (messages.isEmpty()) {
            messages.add("Project is not found");
            CommandEmoji = Emoji.CRY;
        } else {
            messages.add(0, "Project list");
            messages.add("");
            CommandEmoji = Emoji.PAGE_FACING_UP;
        }

        ResponseParameter responseParameter = new ResponseParameter(ResponseParameter.flatMessages(messages));
        responseParameter.setDescription("Command [ list ] is executed");
        responseParameter.setEmoji(CommandEmoji);

        output(responseParameter);
    }

}
