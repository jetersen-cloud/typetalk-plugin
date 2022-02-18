package org.jenkinsci.plugins.typetalk.pipeline;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.typetalk.delegate.NotifyDelegate;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Set;

public class TypetalkSendStep extends Step {

    @Nonnull
    private final String name;

    @Nonnull
    private final Long topicId;

    @Nonnull
    private final Long talkId;

    @Nonnull
    private final String description;

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

    @Nonnull
    public String getDescription() {
        return description;
    }

    @DataBoundConstructor
    public TypetalkSendStep(@Nonnull String name, @Nonnull Long topicId, @Nonnull Long talkId, @Nonnull String description) {
        this.name = name;
        this.topicId = topicId;
        this.talkId = talkId;
        this.description = description;
    }

    @Override
    public StepExecution start(final StepContext context) {
        return new TypetalkSendStepExecution(context, this);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "typetalkSend";
        }

        @Override
        @Nonnull
        public String getDisplayName() {
            return "Notify Typetalk when the build fails";
        }
    }

    public static class TypetalkSendStepExecution extends SynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        transient TypetalkSendStep step;

        protected TypetalkSendStepExecution(@NonNull final StepContext context, final TypetalkSendStep step) {
            super(context);
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            final StepContext context = getContext();
            final Run<?, ?> run = context.get(Run.class);
            final TaskListener taskListener = context.get(TaskListener.class);
            final NotifyDelegate notifyDelegate = new NotifyDelegate(step.name, step.topicId, step.talkId, step.description, taskListener, run);
            notifyDelegate.notifyResult();
            return null;
        }
    }
}
