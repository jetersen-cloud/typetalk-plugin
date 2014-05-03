package org.jenkinsci.plugins.typetalk;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.api.Typetalk;
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class TypetalkBuildWrapper extends BuildWrapper {

	public final String name;
	public final String topicNumber;
	public final boolean notifyStart;
	public final String notifyStartMessage;
	public final boolean notifyEnd;
	public final String notifyEndMessage;

	@DataBoundConstructor
	public TypetalkBuildWrapper(String name, String topicNumber, boolean notifyStart, String notifyStartMessage, boolean notifyEnd, String notifyEndMessage) {
		this.name = name;
		this.topicNumber = topicNumber;
		this.notifyStart = notifyStart;
		this.notifyStartMessage = notifyStartMessage;
		this.notifyEnd = notifyEnd;
		this.notifyEndMessage = notifyEndMessage;
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
		if (notifyStart) {
			listener.getLogger().println("Notifying build start to Typetalk...");

			String message;
			if (StringUtils.isBlank(notifyStartMessage)) {
				TypetalkMessage typetalkMessage = new TypetalkMessage(TypetalkMessage.Emoji.LOUDSPEAKER, "Build start");
				message = typetalkMessage.messageWithBuildInfo(build);
			} else{
				message = build.getEnvironment(listener).expand(notifyStartMessage);
			}
			Long topicId = Long.valueOf(topicNumber);

			Typetalk.createFromName(name).postMessage(topicId, message);
		}

		return new Environment() {
			@Override
			public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
				if (notifyEnd && isSuccessBuild(build)) {
					listener.getLogger().println("Notifying build end to Typetalk...");

					String message;
					if (StringUtils.isBlank(notifyEndMessage)) {
						TypetalkMessage typetalkMessage = new TypetalkMessage(TypetalkMessage.Emoji.MEGA, "Build end");
						message = typetalkMessage.messageWithBuildInfo(build);
					} else {
						message = build.getEnvironment(listener).expand(notifyEndMessage);
					}
					Long topicId = Long.valueOf(topicNumber);

					Typetalk.createFromName(name).postMessage(topicId, message);
				}

				return true;
			}

			private boolean isSuccessBuild(AbstractBuild build) {
				// When there is nothing failure (equals success), getResult hasn't been set yet.
				return build.getResult() == null || build.getResult().equals(Result.SUCCESS);
			}
		};
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends BuildWrapperDescriptor {

		public DescriptorImpl() {
			super(TypetalkBuildWrapper.class);
			load();
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Notify to Typetalk when the build starts/ends";
		}
	}

}
