package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl;

import hudson.model.*;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor;
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildExecutor extends WebhookExecutor {

    private String job;

    private List<String> parameters;

    private Map<String, String> parameterMap = new HashMap<>();

    public BuildExecutor(WebhookRequest req, StaplerResponse rsp, String job, List<String> parameters) {
        super(req, rsp, "build");
        this.job = job;
        this.parameters = parameters;

        parseParameters();
    }

    private void parseParameters() {
        for (String parameter : parameters) {
            String[] splitParameter = parameter.split("=");

            if (splitParameter.length == 2) {
                parameterMap.put(splitParameter[0], splitParameter[1]);
            }
        }
    }

    /**
     * @see hudson.model.AbstractProject#doBuild
     * @see hudson.model.AbstractProject#doBuildWithParameters
     */
    @Override
    public void execute() {
        TopLevelItem item = Jenkins.getInstance().getItemMap().get(job);
        if (item == null || !(item instanceof AbstractProject)) {
            outputError("'" + job + "' is not found");
            return;
        }
        AbstractProject project = ((AbstractProject) item);

        if (!project.hasPermission(Item.BUILD)) {
            String name = Jenkins.getAuthentication().getName();
            outputError(String.format("'%s' cannot be built by '%s'", job, name), HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Jenkins.getInstance().getQueue().schedule(project, project.getQuietPeriod(), getParametersAction(project), getCauseAction());
        output("'" + job + "' has been scheduled");
    }

    private Action getParametersAction(AbstractProject project) {
        ParametersDefinitionProperty property = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);
        if (property == null) {
            return null;
        }

        List<ParameterValue> values = new ArrayList<>();
        List<ParameterDefinition> pds = property.getParameterDefinitions();

        if (isOnlySingleParameter(pds)) {

            ParameterDefinition pd = pds.get(0);
            if (!(pd instanceof SimpleParameterDefinition)) {
                return null;
            }
            SimpleParameterDefinition spd = (SimpleParameterDefinition) pd;
            values.add(spd.createValue(parameters.get(0)));

        } else {

            for (ParameterDefinition pd : pds) {
                if (!(pd instanceof SimpleParameterDefinition)) {
                    continue;
                }
                SimpleParameterDefinition spd = (SimpleParameterDefinition) pd;

                if (parameterMap.containsKey(spd.getName())) {
                    String parameterValue = parameterMap.get(spd.getName());
                    values.add(spd.createValue(parameterValue));
                } else {
                    ParameterValue defaultValue = spd.getDefaultParameterValue();
                    if (defaultValue != null) {
                        values.add(defaultValue);
                    }
                }
            }

        }

        return new ParametersAction(values);
    }

    private boolean isOnlySingleParameter(List<ParameterDefinition> pds) {
        return pds.size() == 1 && parameters.size() == 1 &&
                !parameters.get(0).contains("="); // when parameter contains '=', parameter is handled at the other code
    }

    private Action getCauseAction() {
        return new CauseAction(new Cause.RemoteCause(req.getRemoteAddr(), "by Typetalk Webhook"));
    }

}
