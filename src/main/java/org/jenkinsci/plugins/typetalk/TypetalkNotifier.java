package org.jenkinsci.plugins.typetalk;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.typetalk.api.Typetalk;
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;

public class TypetalkNotifier extends Notifier {

	public final String name;
	public final String topicNumber;

	@DataBoundConstructor
	public TypetalkNotifier(String name, String topicNumber, boolean notifyWhenSuccess) {
		this.name = name;
		this.topicNumber = topicNumber;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {

		if (successFromPreviousBuild(build)) {
			return true;
		}

		listener.getLogger().println("Notifying build result to Typetalk...");

		TypetalkMessage typetalkMessage = TypetalkMessage.convertFromResult(build);
		String message = typetalkMessage.messageWithBuildInfo(build);
		Long topicId = Long.valueOf(topicNumber);

		Typetalk.createFromName(name).postMessage(topicId, message);

		return true;
	}

	private boolean successFromPreviousBuild(AbstractBuild<?, ?> build) {
		if (build.getPreviousBuild() == null) {
			return build.getResult().equals(Result.SUCCESS);
		} else {
			return build.getResult().equals(Result.SUCCESS)
				&& build.getPreviousBuild().getResult().equals(Result.SUCCESS);
		}
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		private volatile Credential[] credentials = new Credential[0];

		public Credential[] getCredentials() {
			return credentials;
		}

		public Credential getCredential(String name) {
			for (Credential credential : credentials) {
				if (credential.getName().equals(name)) {
					return credential;
				}
			}
			return null;
		}

		public DescriptorImpl() {
			super(TypetalkNotifier.class);
			load();
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Notify Typetalk when the build fails";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			try {
				credentials = req.bindJSONToList(Credential.class,
						req.getSubmittedForm().get("credential")).toArray(new Credential[0]);
				save();
				return true;
			} catch (ServletException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public static final class Credential implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String name;

		private final String clientId;

		private final Secret clientSecret;

		@DataBoundConstructor
		public Credential(String name, String clientId, String clientSecret) {
			this.name = name;
			this.clientId = clientId;
			this.clientSecret = Secret.fromString(clientSecret);
		}

		public String getName() {
			return name;
		}

		public String getClientId() {
			return clientId;
		}

		public String getClientSecret() {
			return Secret.toString(clientSecret);
		}

	}

}
