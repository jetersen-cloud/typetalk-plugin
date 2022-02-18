package org.jenkinsci.plugins.typetalk.pipeline;


import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.typetalk.delegate.BuildWrapperDelegate;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.BodyInvoker;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Set;

public class TypetalkBuildWrapperStep extends Step {

    @Nonnull
    private final String name;

    @Nonnull
    private final Long topicId;

    @Nonnull

    private final Long talkId;

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

    @Nonnull
    public Long getTalkId() {
        return talkId;
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
    public TypetalkBuildWrapperStep(@Nonnull String name, @Nonnull Long topicId, @Nonnull Long talkId) {
        this.name = name;
        this.topicId = topicId;
        this.talkId = talkId;
    }

    @Override
    public StepExecution start(final StepContext context) {
        return new TypetalkBuildWrapperStepExecution(context, this);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "withTypetalk";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Notify Typetalk when the build starts/ends";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

    }

    public static class TypetalkBuildWrapperStepExecution extends StepExecution {

        private static final long serialVersionUID = 1L;

        transient TypetalkBuildWrapperStep step;

        public TypetalkBuildWrapperStepExecution(@NonNull final StepContext context, final TypetalkBuildWrapperStep step) {
            super(context);
            this.step = step;
        }

        @Override
        public boolean start() throws IOException, InterruptedException {
            final StepContext context = getContext();
            final TaskListener listener = context.get(TaskListener.class);
            final Run<?, ?> run = context.get(Run.class);
            final BodyInvoker invoker = context.newBodyInvoker().withCallback(new Callback(step, listener, run));
            invoker.start();
            return false;
        }

        @Override
        public void stop(@Nonnull Throwable throwable) {
            // Do nothing
        }
    }

    public static class Callback extends BodyExecutionCallback {

        private static final long serialVersionUID = 1L;

        private transient final TypetalkBuildWrapperStep step;

        private transient final BuildWrapperDelegate delegate;

        Callback(TypetalkBuildWrapperStep step, TaskListener listener, Run<?, ?> run) {
            this.step = step;
            this.delegate = new BuildWrapperDelegate(step.name, step.topicId, step.talkId, listener, run);
        }

        @Override
        public void onStart(StepContext context) {
            try {
                delegate.notifyStart(step.notifyStart, step.notifyStartMessage);
            } catch (Exception x) {
                context.onFailure(x);
            }
        }

        @Override
        public void onSuccess(StepContext context, Object result) {
            try {
                delegate.notifyEnd(step.notifyEnd, step.notifyEndMessage);
                context.onSuccess(result);
            } catch (Exception x) {
                context.onFailure(x);
            }
        }

        @Override
        public void onFailure(StepContext context, Throwable t) {
            try {
                context.onFailure(t);
            } catch (Exception x) {
                context.onFailure(x);
            }
        }
    }
}
