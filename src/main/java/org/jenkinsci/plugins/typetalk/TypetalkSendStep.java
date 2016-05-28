package org.jenkinsci.plugins.typetalk;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.typetalk.api.Typetalk;
import org.jenkinsci.plugins.typetalk.support.ResultSupport;
import org.jenkinsci.plugins.typetalk.support.TypetalkMessage;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class TypetalkSendStep extends AbstractStepImpl {

    private final @Nonnull String name;
    private final @Nonnull Long topicId;

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Long getTopicId() {
        return topicId;
    }

    @DataBoundConstructor
    public TypetalkSendStep(@Nonnull String name, @Nonnull Long topicId) {
        this.name = name;
        this.topicId = topicId;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(TypetalkSendStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "typetalkSend";
        }

        @Override
        public String getDisplayName() {
            return "Notify Typetalk when the build fails";
        }
    }

    public static class TypetalkSendStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient TypetalkSendStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run run;

        /**
         * Almost same as {@link TypetalkNotifier#perform(AbstractBuild, Launcher, BuildListener)}
         */
        @Override
        protected Void run() throws Exception {
            ResultSupport resultSupport = new ResultSupport();
            if (resultSupport.successFromPreviousBuild(run)) {
                return null;
            }

            listener.getLogger().println("Notifying build result to Typetalk...");

            TypetalkMessage typetalkMessage = new ResultSupport().convertBuildToMessage(run);
            String message = typetalkMessage.buildMessageWithBuild(run);

            Typetalk.createFromName(step.name).postMessage(step.topicId, message);

            return null;
        }

    }

}