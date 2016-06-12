package org.jenkinsci.plugins.typetalk;


import hudson.Extension;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.api.Typetalk;
import org.jenkinsci.plugins.typetalk.support.Emoji;
import org.jenkinsci.plugins.typetalk.support.TypetalkMessage;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class TypetalkBuildWrapperStep extends AbstractStepImpl {

//    TODO move workflow package

    private final @Nonnull String name;
    private final @Nonnull Long topicId;
    private boolean notifyStart;
    private String notifyStartMessage;
    private boolean notifyEnd;
    private String notifyEndMessage;

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Long getTopicId() {
        return topicId;
    }

    public boolean isNotifyStart() {
        return notifyStart;
    }

    @DataBoundSetter
    public void setNotifyStart(boolean notifyStart) {
        this.notifyStart = notifyStart;
    }

    public String getNotifyStartMessage() {
        return notifyStartMessage;
    }

    @DataBoundSetter
    public void setNotifyStartMessage(String notifyStartMessage) {
        this.notifyStartMessage = notifyStartMessage;
    }

    public boolean isNotifyEnd() {
        return notifyEnd;
    }

    @DataBoundSetter
    public void setNotifyEnd(boolean notifyEnd) {
        this.notifyEnd = notifyEnd;
    }

    public String getNotifyEndMessage() {
        return notifyEndMessage;
    }

    @DataBoundSetter
    public void setNotifyEndMessage(String notifyEndMessage) {
        this.notifyEndMessage = notifyEndMessage;
    }

    @DataBoundConstructor
    public TypetalkBuildWrapperStep(@Nonnull String name, @Nonnull Long topicId) {
        this.name = name;
        this.topicId = topicId;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(TypetalkBuildWrapperStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "withTypetalk";
        }

        @Override
        public String getDisplayName() {
            return "Notify Typetalk when the build starts/ends";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

    }

    public static class TypetalkBuildWrapperStepExecution extends AbstractStepExecutionImpl {

        private static final long serialVersionUID = 1L;

        @Inject
        transient TypetalkBuildWrapperStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        @Override
        public boolean start() throws Exception {
            if (step.notifyStart) {
                listener.getLogger().println("Notifying build start to Typetalk...");

                String message;
                if (StringUtils.isBlank(step.notifyStartMessage)) {
                    TypetalkMessage typetalkMessage = new TypetalkMessage(Emoji.LOUDSPEAKER, "Build start");
                    message = typetalkMessage.buildMessageWithBuild(run);
                } else {
                    message = run.getEnvironment(listener).expand(step.notifyStartMessage);
                }

                Typetalk.createFromName(step.name).postMessage(step.topicId, message);
            }

            getContext().
                    newBodyInvoker().
                    withCallback(new TypetalkBuildWrapperStepCallback(step, listener, run)).
                    start();

            return false;
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {
            // Do nothing
        }
    }

    private static class TypetalkBuildWrapperStepCallback extends BodyExecutionCallback.TailCall {

        private final TypetalkBuildWrapperStep step;

        private final TaskListener listener;

        private final Run run;

        TypetalkBuildWrapperStepCallback(TypetalkBuildWrapperStep step, TaskListener listener, Run run) {
            this.step = step;
            this.listener = listener;
            this.run = run;
        }

        @Override
        protected void finished(StepContext context) throws Exception {
            if (step.notifyEnd && isSuccessBuild(run)) {
                listener.getLogger().println("Notifying build end to Typetalk...");

                String message;
                if (StringUtils.isBlank(step.notifyEndMessage)) {
                    TypetalkMessage typetalkMessage = new TypetalkMessage(Emoji.MEGA, "Build end");
                    message = typetalkMessage.buildMessageWithBuild(run);
                } else {
                    message = run.getEnvironment(listener).expand(step.notifyEndMessage);
                }

                Typetalk.createFromName(step.name).postMessage(step.topicId, message);
            }
        }

        private boolean isSuccessBuild(Run run) {
            // When there is nothing failure (equals success), getResult hasn't been set yet.
            return run.getResult() == null || run.getResult().equals(Result.SUCCESS);
        }

    }

}
