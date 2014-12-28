package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class BuildExecutor extends WebhookExecutor {

    // TODO nakamura : パラメータ付きビルドの対応
    // TODO nakamura : 認証付きの場合の確認

    private static final Logger LOGGER = Logger.getLogger(BuildExecutor.class.getName());

    private String job;

    public BuildExecutor(HttpServletRequest req, HttpServletResponse rsp, String job) {
        super(req, rsp, "build");
        this.job = job;
    }

    @Override
    public void execute() {
        TopLevelItem item = Jenkins.getInstance().getItemMap().get(job);
        if (item == null || !(item instanceof AbstractProject)) {
            String message = "'" + job + "' is not found";
            LOGGER.warning(message);

            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.println(message);

            return;
        }

        Cause.RemoteCause cause = new Cause.RemoteCause(req.getRemoteAddr(), "by Typetalk Webhook");
        ((AbstractProject) item).scheduleBuild(cause);

        String message = "'" + job + "' has been scheduled";
        LOGGER.info(message);

        rsp.setStatus(HttpServletResponse.SC_OK);
        writer.println(message);
    }
}
