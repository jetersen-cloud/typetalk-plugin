package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl

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

    // --- helper method ---

    def setUpRootUrl() {
        JenkinsLocationConfiguration.get().url = "http://localhost:8080/"
    }

}
