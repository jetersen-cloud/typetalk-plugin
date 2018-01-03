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

    public String buildMessageWithBuild(Run build) {
		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (StringUtils.isEmpty(rootUrl)) {
			throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(emoji.getSymbol());
		builder.append(" ");
		builder.append(message);
		builder.append(" [ ");
		builder.append(build.getParent().getDisplayName());
		builder.append(" ]");
		builder.append("\n");
		builder.append(rootUrl);
		builder.append(build.getUrl());

		if (build instanceof RunWithSCM) {
			List<ChangeLogSet> changeSets = ((RunWithSCM) build).getChangeSets();

			String uniqueIds = new UniqueIdConverter().changeSetsToAuthorUniqueIds(changeSets);
			if (StringUtils.isNotEmpty(uniqueIds)) {
				builder.append("\n\n");
				builder.append(uniqueIds);
			}
		}

		return builder.toString();
	}

	public String buildMessageWithProject(Job project) {
		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (StringUtils.isEmpty(rootUrl)) {
			throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(emoji.getSymbol());
		builder.append(" ");
		builder.append(message);
		builder.append("\n");
		builder.append(rootUrl);
		if (project != null) {
			builder.append(project.getUrl());
		}

		return builder.toString();
	}

}
