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

import java.util.Set;

public class TypetalkSendStep extends Step {

    @NonNull
    private final String name;

    @NonNull
    private final Long topicId;

    @NonNull
    private final Long talkId;

    @NonNull
    private final String description;

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public Long getTopicId() {
        return topicId;
    }

    @NonNull
    public Long getTalkId() {
        return talkId;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @DataBoundConstructor
    public TypetalkSendStep(@NonNull String name, @NonNull Long topicId, @NonNull Long talkId, @NonNull String description) {
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
        @NonNull
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
