package org.jenkinsci.plugins.typetalk.api;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

public class TypetalkMessage {

	public enum Emoji {
		LOUDSPEAKER(":loudspeaker:"),
		MEGA(":mega:"),
		ASTONISHED(":astonished:"),
		RAGE(":rage:"),
		CRY(":cry:"),
		SMILEY(":smiley:"),
		MASK(":mask:"),
		BOOK(":book:"),
		PAGE_FACING_UP(":page_facing_up:");

		private String symbol;

		public String getSymbol() {
			return symbol;
		}

		Emoji(String symbol) {
			this.symbol = symbol;
		}
	}

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

	public static TypetalkMessage convertFromResult(AbstractBuild<?, ?> build) {
		if (build.getResult().equals(Result.ABORTED)) {
			return new TypetalkMessage(Emoji.ASTONISHED, "Build aborted");
		} else if (build.getResult().equals(Result.NOT_BUILT)) {
			return new TypetalkMessage(Emoji.ASTONISHED, "Not built");
		} else if (build.getResult().equals(Result.FAILURE)) {
			return new TypetalkMessage(Emoji.RAGE, "Build failure");
		} else if (build.getResult().equals(Result.UNSTABLE)) {
			return new TypetalkMessage(Emoji.CRY, "Build unstable");
		} else if (build.getResult().equals(Result.SUCCESS)) {
			if (recoverSuccess(build)) {
				return new TypetalkMessage(Emoji.SMILEY, "Build recovery");
			} else {
				return new TypetalkMessage(Emoji.SMILEY, "Build success");
			}
		}

		throw new IllegalArgumentException("Unknown build result.");
	}

	private static boolean recoverSuccess(AbstractBuild<?, ?> build) {
		if (build.getPreviousBuild() == null) {
			return false;
		} else {
			return build.getResult().equals(Result.SUCCESS)
				&& build.getPreviousBuild().getResult().isWorseThan(Result.SUCCESS);
		}
	}

	public String messageWithBuildInfo(AbstractBuild<?, ?> build) {
		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (StringUtils.isEmpty(rootUrl)) {
			throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(emoji.symbol);
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

	public String messageWithProjectInfo(AbstractProject project) {
		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (StringUtils.isEmpty(rootUrl)) {
			throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(emoji.symbol);
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
