package org.jenkinsci.plugins.typetalk;

import hudson.model.AbstractBuild;
import hudson.model.Result;

public class TypetalkResult {

	public enum Emoji {
		ASTONISHED(":astonished:"),
		RAGE(":rage:"),
		CRY(":cry:"),
		SMILEY(":smiley:");

		private String symbol;

		Emoji(String symbol) {
			this.symbol = symbol;
		}
	}

	private Emoji emoji;
	private String message;

	public Emoji getEmoji() {
		return emoji;
	}

	public String getMessage() {
		return message;
	}

	public TypetalkResult(Emoji emoji, String message) {
		this.emoji = emoji;
		this.message = message;
	}

	public static TypetalkResult convert(AbstractBuild<?, ?> build) {
		if (build.getResult().equals(Result.ABORTED)) {
			return new TypetalkResult(Emoji.ASTONISHED, "Build aborted");
		} else if (build.getResult().equals(Result.NOT_BUILT)) {
			return new TypetalkResult(Emoji.ASTONISHED, "Not built");
		} else if (build.getResult().equals(Result.FAILURE)) {
			return new TypetalkResult(Emoji.RAGE, "Build failure");
		} else if (build.getResult().equals(Result.UNSTABLE)) {
			return new TypetalkResult(Emoji.CRY, "Build unstable");
		} else if (build.getResult().equals(Result.SUCCESS)) {
			if (recoverSuccess(build)) {
				return new TypetalkResult(Emoji.SMILEY, "Build recovery");
			} else {
				return new TypetalkResult(Emoji.SMILEY, "Build success");
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

	@Override
	public String toString() {
		return emoji.symbol + " " + message;
	}

}
