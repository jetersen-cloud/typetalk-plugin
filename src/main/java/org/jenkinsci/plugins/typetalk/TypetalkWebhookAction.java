package org.jenkinsci.plugins.typetalk;

import hudson.Extension;
import hudson.model.RootAction;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutorFactory;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookParser;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.servlet.ServletException;
import java.io.IOException;


/**
 * @see <a href="https://github.com/github/hubot-scripts/blob/master/src/scripts/jenkins.coffee">hubot script</a>
 */
@Extension
public class TypetalkWebhookAction implements RootAction {

    // FIXME parameterized build
    // FIXME check authentication

    // TODO handle response back ( if Typetalk supports it )
    // TODO add list executor ( with regexp filter )
    // TODO add help executor

    // TODO enable alias job name

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return "typetalk";
    }

    @RequirePOST
    public HttpResponse doNotify() {

        return new HttpResponse() {
            public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node)
                    throws IOException, ServletException {
                String message = new WebhookParser(req).parse();
                WebhookExecutor executor = WebhookExecutorFactory.create(req, rsp, message);

                executor.execute();
            }
        };

    }

}