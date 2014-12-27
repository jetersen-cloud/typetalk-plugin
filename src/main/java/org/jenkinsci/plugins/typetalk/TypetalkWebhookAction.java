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
    public HttpResponse doNotify(final StaplerRequest request) {

        return new HttpResponse() {
            public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node)
                    throws IOException, ServletException {

                String message = new WebhookParser(req).parse();
                WebhookExecutor executor = WebhookExecutorFactory.create(req, rsp, message);
                executor.execute();

//                for (ResponseContributor c : contributors) {
//                    c.addHeaders(req, rsp);
//                }
//                PrintWriter w = rsp.getWriter();
//                for (ResponseContributor c : contributors) {
//                    c.writeBody(req, rsp, w);
//                }
            }
        };
    }

    static class TypetalkRequest {
        Object topic;

//        {"topic":{"id":9526,"name":"Typetalk Hack Tokyo Dec 2014","suggestion":"Typetalk Hack Tokyo Dec 2014","lastPostedAt":"2014-12-16T11:17:44Z","createdAt":"2014-12-16T08:32:53Z","updatedAt":"2014-12-16T08:32:53Z"},"post":{"id":754399,"topicId":9526,"replyTo":null,"message":"@ikikkobot+ hello","account":{"id":10,"name":"ikikko","fullName":"nakamura","suggestion":"nakamura","imageUrl":"https://typetalk.in/accounts/10/profile_image.png?t=1413099125640","createdAt":"2012-03-07T05:13:52Z","updatedAt":"2014-12-16T09:12:55Z"},"mention":null,"attachments":[],"likes":[],"talks":[],"links":[],"createdAt":"2014-12-16T11:17:44Z","updatedAt":"2014-12-16T11:17:44Z"}}

//        @see https://github.com/github/hubot-scripts/blob/master/src/scripts/jenkins.coffee
//
//        # Commands:
//        #   hubot jenkins b <jobNumber> - builds the job specified by jobNumber. List jobs to get number.
//        #   hubot jenkins build <job> - builds the specified Jenkins job
//        #   hubot jenkins build <job>, <params> - builds the specified Jenkins job with parameters as key=value&key2=value2
//        #   hubot jenkins list <filter> - lists Jenkins jobs
//        #   hubot jenkins describe <job> - Describes the specified Jenkins job
//        #   hubot jenkins last <job> - Details about the last build for the specified Jenkins job

    }

}