package org.jenkinsci.plugins.typetalk.support;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

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

    public String buildMessageWithBuild(AbstractBuild<?, ?> build) {
		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (StringUtils.isEmpty(rootUrl)) {
			throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(emoji.getSymbol());
		builder.append(" ");
		builder.append(message);
		builder.append(" [ ");
		builder.append(build.getProject().getDisplayName());
		builder.append(" ]");
		builder.append("\n");
		builder.append(rootUrl);
		builder.append(build.getUrl());

		return builder.toString();
	}

	public String buildMessageWithProject(AbstractProject project) {
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
