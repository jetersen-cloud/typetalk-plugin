package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage;
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
        TypetalkMessage.Emoji emoji;

        for (TopLevelItem item : Jenkins.getInstance().getItems()) {
            if (!(item instanceof AbstractProject)) {
                continue;
            }
            AbstractProject project = ((AbstractProject) item);

            if (pattern != null && !pattern.matcher(project.getName()).find()) {
                continue;
            }

            TypetalkMessage.Emoji ball2emoji;
            switch (project.getIconColor()) {
                case RED:
                case RED_ANIME:
                    ball2emoji = TypetalkMessage.Emoji.RAGE;
                    break;
                case YELLOW:
                case YELLOW_ANIME:
                    ball2emoji = TypetalkMessage.Emoji.CRY;
                    break;
                case BLUE:
                case BLUE_ANIME:
                    ball2emoji = TypetalkMessage.Emoji.SMILEY;
                    break;
                case DISABLED:
                case DISABLED_ANIME:
                case NOTBUILT:
                case NOTBUILT_ANIME:
                    ball2emoji = TypetalkMessage.Emoji.MASK;
                    break;
                default:
                    ball2emoji = TypetalkMessage.Emoji.ASTONISHED;
            }
            messages.add(String.format(PROJECT_MESSAGE_FORMAT, ball2emoji.getSymbol(), project.getName(), project.getAbsoluteUrl()));
        }

        if (messages.isEmpty()) {
            messages.add("Project is not found");
            emoji = TypetalkMessage.Emoji.CRY;
        } else {
            messages.add(0, "Project list");
            messages.add("");
            emoji = TypetalkMessage.Emoji.PAGE_FACING_UP;
        }

        ResponseParameter responseParameter = new ResponseParameter(ResponseParameter.flatMessages(messages));
        responseParameter.setDescription("Command [ list ] is executed");
        responseParameter.setEmoji(emoji);

        output(responseParameter);
    }

}
