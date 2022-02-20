package org.jenkinsci.plugins.typetalk.support;

import hudson.model.Job;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import jenkins.model.Jenkins;
import jenkins.scm.RunWithSCM;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class TypetalkMessage {

    public static final String CODE_SEPARATOR = "```";

    private final Emoji emoji;
    private final String message;

    public Emoji getEmoji() {
        return emoji;
    }

    public String getMessage() {
        return message;
    }

    public TypetalkMessage(Emoji emoji, String message) {
        this.emoji = emoji;
        this.message = message;
    }

    public String buildMessageWithBuild(Run<?, ?> build) {
        return buildMessageWithBuild(build, null);
    }

    public String buildMessageWithBuild(Run<?, ?> build, String description) {
        final StringBuilder builder = new StringBuilder(buildMessagePrefix());
        builder.append(" [ ");
        builder.append(build.getParent().getDisplayName());
        builder.append(" ]");
        builder.append("\n");
        final String rootUrl = getRootUrl();
        if (rootUrl != null) {
            builder.append(rootUrl);
        }
        builder.append(build.getUrl());

        if (build instanceof RunWithSCM) {
            final RunWithSCM<?, ?> runWithSCM = (RunWithSCM<?, ?>) build;
            final List<ChangeLogSet<?>> changeSets = runWithSCM.getChangeSets();

            final String uniqueIds = new UniqueIdConverter().changeSetsToAuthorUniqueIds(changeSets);
            if (StringUtils.isNotEmpty(uniqueIds)) {
                builder.append("\n\n");
                builder.append(uniqueIds);
            }
        }

        if (StringUtils.isNotEmpty(description)) {
            builder.append("\n\n");
            builder.append(description);
        }

        return builder.toString();
    }

    public String buildMessageWithProject(Job<?, ?> project) {
        final StringBuilder builder = new StringBuilder(buildMessagePrefix());
        builder.append("\n");

        final String rootUrl = getRootUrl();
        if (rootUrl != null) {
            builder.append(rootUrl);
        }

        if (project != null) {
            builder.append(project.getUrl());
        }

        return builder.toString();
    }

    private String buildMessagePrefix() {
        return emoji.getSymbol() + " " + message;
    }

    private static String getRootUrl() {
        final Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins == null) {
            return null;
        }
        final String rootUrl = jenkins.getRootUrl();
        if (StringUtils.isEmpty(rootUrl)) {
            throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
        }
        return rootUrl;
    }

}
