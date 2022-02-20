package org.jenkinsci.plugins.typetalk;

import hudson.Extension;
import hudson.model.RootAction;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutorFactory;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

@Extension
public class TypetalkWebhookAction implements RootAction {

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

        return (req, rsp, node) -> {
            WebhookExecutor executor = WebhookExecutorFactory.create(req, rsp);
            executor.execute();
        };

    }

}