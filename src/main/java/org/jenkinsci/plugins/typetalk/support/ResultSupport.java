package org.jenkinsci.plugins.typetalk.support;

import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

public class ResultSupport {

    public boolean successFromPreviousBuild(Run build) {
        if (build.getPreviousBuild() == null) {
            return (inProgressPipeline(build) || build.getResult().equals(Result.SUCCESS));
        } else {
            return (inProgressPipeline(build) || build.getResult().equals(Result.SUCCESS))
                    && build.getPreviousBuild().getResult().equals(Result.SUCCESS);
        }
    }

    public TypetalkMessage convertBuildToMessage(Run build) {
        if (inProgressPipeline(build) || build.getResult().equals(Result.SUCCESS)) {
            if (recoverSuccess(build)) {
                return new TypetalkMessage(Emoji.SMILEY, "Build recovery");
            } else {
                return new TypetalkMessage(Emoji.SMILEY, "Build success");
            }
        } else if (build.getResult().equals(Result.ABORTED)) {
            return new TypetalkMessage(Emoji.ASTONISHED, "Build aborted");
        } else if (build.getResult().equals(Result.NOT_BUILT)) {
            return new TypetalkMessage(Emoji.ASTONISHED, "Not built");
        } else if (build.getResult().equals(Result.FAILURE)) {
            return new TypetalkMessage(Emoji.RAGE, "Build failure");
        } else if (build.getResult().equals(Result.UNSTABLE)) {
            return new TypetalkMessage(Emoji.CRY, "Build unstable");
        }

        throw new IllegalArgumentException("Unknown build result.");
    }

    public boolean recoverSuccess(Run build) {
        if (build.getPreviousBuild() == null) {
            return false;
        } else {
            return (inProgressPipeline(build) || build.getResult().equals(Result.SUCCESS))
                && build.getPreviousBuild().getResult().isWorseThan(Result.SUCCESS);
        }
    }

    private boolean inProgressPipeline(Run build) {
        return build instanceof WorkflowRun && build.getResult() == null;
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