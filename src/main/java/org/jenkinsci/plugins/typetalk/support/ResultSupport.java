package org.jenkinsci.plugins.typetalk.support;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;

public class ResultSupport {

    public TypetalkMessage convertBuildToMessage(AbstractBuild build) {
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

    public boolean recoverSuccess(AbstractBuild build) {
        if (build.getPreviousBuild() == null) {
            return false;
        } else {
            return build.getResult().equals(Result.SUCCESS)
                && build.getPreviousBuild().getResult().isWorseThan(Result.SUCCESS);
        }
    }

    public Emoji convertProjectToEmoji(AbstractProject project) {
        switch (project.getIconColor()) {
            case RED:
            case RED_ANIME:
                return Emoji.RAGE;
            case YELLOW:
            case YELLOW_ANIME:
                return Emoji.CRY;
            case BLUE:
            case BLUE_ANIME:
                return Emoji.SMILEY;
            case DISABLED:
            case DISABLED_ANIME:
            case NOTBUILT:
            case NOTBUILT_ANIME:
                return Emoji.MASK;
            default:
                return Emoji.ASTONISHED;
        }
    }

}