package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl

import hudson.model.ParametersDefinitionProperty
import hudson.model.StringParameterDefinition
import jenkins.model.JenkinsLocationConfiguration
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.kohsuke.stapler.StaplerResponse
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

class HelpExecutorSpec extends Specification {

    @Rule JenkinsRule j

    def req = Mock(WebhookRequest)
    def res = Mock(StaplerResponse)

    WebhookExecutor executor
    def writer = new StringWriter()

    def setup() {
        res.writer >> new PrintWriter(writer)
    }

    @Unroll
    def "execute : parameters #parameters"() {
        setup:
        setUpRootUrl()
        executor = new HelpExecutor(req, res, "@jenkins+", parameters as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("build <project>")
        writer.toString().contains("list")
        writer.toString().contains("help")

        where:
        parameters || result
        []         || true
        ["dummy"]  || true
    }

    def "execute : parameters [build]"() {
        setup:
        setUpRootUrl()
        executor = new HelpExecutor(req, res, "@jenkins+", ["build"] as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("build <project> (<key=value>)")
        writer.toString().contains("build helloWorldProject version=1.0.0 env=stage")
    }

    def "execute : parameters [build project] - no parameter"() {
        setup:
        setUpRootUrl()
        setUpProject([])
        executor = new HelpExecutor(req, res, "@jenkins+", ["build", "typetalk-plugin"] as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("build typetalk-plugin")
        writer.toString().contains("http://localhost:8080/job/typetalk-plugin")
    }

    def "execute : parameters [build project] - single parameter"() {
        setup:
        setUpRootUrl()
        setUpProject([
            new StringParameterDefinition("version", null, "version description"),
        ])
        executor = new HelpExecutor(req, res, "@jenkins+", ["build", "typetalk-plugin"] as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("build typetalk-plugin <value>")
        writer.toString().contains("version : version description")
        writer.toString().contains("http://localhost:8080/job/typetalk-plugin")
    }

    def "execute : parameters [build project] - multiple parameters"() {
        setup:
        setUpRootUrl()
        setUpProject([
            new StringParameterDefinition("version", null, "version description"),
            new StringParameterDefinition("env", null, "env description")
        ])
        executor = new HelpExecutor(req, res, "@jenkins+", ["build", "typetalk-plugin"] as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("build typetalk-plugin <key=value>")
        writer.toString().contains("version : version description")
        writer.toString().contains("env : env description")
        writer.toString().contains("http://localhost:8080/job/typetalk-plugin")
    }

    def "execute : parameters [build project] - not found"() {
        setup:
        setUpRootUrl()
        executor = new HelpExecutor(req, res, "@jenkins+", ["build", "typetalk-plugin"] as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("not found")
    }

    def "execute : parameters [list]"() {
        setup:
        setUpRootUrl()
        executor = new HelpExecutor(req, res, "@jenkins+", ["list"] as LinkedList)

        when:
        executor.execute()

        then:
        1 * res.setStatus(HttpServletResponse.SC_OK)
        writer.toString().contains("list")
        writer.toString().contains("list helloWorld")
    }

    // --- helper method ---

    def setUpRootUrl() {
        JenkinsLocationConfiguration.get().url = "http://localhost:8080/"
    }

    def setUpProject(spds) {
        def project = j.createFreeStyleProject("typetalk-plugin")
        project.addProperty(new ParametersDefinitionProperty(spds))
    }

}
