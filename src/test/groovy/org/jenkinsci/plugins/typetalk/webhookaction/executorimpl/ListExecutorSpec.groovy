package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl

import jenkins.model.JenkinsLocationConfiguration
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.WebhookRequest
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.kohsuke.stapler.StaplerResponse
import spock.lang.Specification
import spock.lang.Unroll

class ListExecutorSpec extends Specification {

    @Rule JenkinsRule j

    def req = Mock(WebhookRequest)
    def res = Mock(StaplerResponse)

    def writer = new StringWriter()

    WebhookExecutor executor

    def setup() {
        res.writer >> new PrintWriter(writer)
    }

    @Unroll
    def "execute : pattern is '#pattern'"() {
        setup:
        setUpJenkins()
        executor = new ListExecutor(req, res, pattern)

        when:
        executor.execute()

        then:
        countResultProjects() == result.size()
        result.each { assert writer.toString().contains(it) }

        where:
        pattern         || result
        "typetalk"      || ["typetalk-plugin", "typetalk2-plugin", "typetalk3-plugin"]
        "typetalk2"     || ["typetalk2-plugin"]
        "typetalk(2|3)" || ["typetalk2-plugin", "typetalk3-plugin"]
    }

    def "execute : not found"() {
        setup:
        setUpJenkins()
        executor = new ListExecutor(req, res, "notfound")

        when:
        executor.execute()

        then:
        countResultProjects() == 0
    }

    // --- helper method ---

    def setUpJenkins() {
        JenkinsLocationConfiguration.get().url = "http://localhost:8080/"

        ["typetalk-plugin", "typetalk2-plugin", "typetalk3-plugin"].each {
            j.createFreeStyleProject(it)
        }
    }

    def countResultProjects() {
        writer.toString().count(":astonished:")
    }

}
