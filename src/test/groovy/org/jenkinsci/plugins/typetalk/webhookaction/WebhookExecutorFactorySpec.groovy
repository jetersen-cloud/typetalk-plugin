package org.jenkinsci.plugins.typetalk.webhookaction

import groovy.text.SimpleTemplateEngine
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.HelpExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.ListExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.NoSuchProjectExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import spock.lang.Specification
import spock.lang.Unroll

class WebhookExecutorFactorySpec extends Specification {

    @Rule JenkinsRule j

    def req = Mock(StaplerRequest)
    def res = Mock(StaplerResponse)

    @Unroll
    def "create BuildExecutor : #message"() {
        setup:
        req.reader >> createMockReader(message)

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == BuildExecutor
        executor.project == "typetalk-plugin"
        executor.parameters == parameters

        where:
        message                                                   || parameters
        "@jenkins+ build typetalk-plugin"                         || []
        "@jenkins+ build typetalk-plugin 1.0.0"                   || ["1.0.0"]
        "@jenkins+ build typetalk-plugin version=1.0.0"           || ["version=1.0.0"]
        "@jenkins+ build typetalk-plugin version=1.0.0 env=stage" || ["version=1.0.0", "env=stage"]
    }

    @Unroll
    def "create BuildExecutor : #message - with query string"() {
        setup:
        setUpProject()
        req.reader >> createMockReader(message)
        req.queryString >> "project=typetalk-plugin"

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == BuildExecutor
        executor.project == "typetalk-plugin"
        executor.parameters == parameters

        where:
        message                 || parameters
        "@jenkins+ build"       || []
        "@jenkins+ build 1.0.0" || ["1.0.0"]
    }

    def "create noSuchProjectExecutor"() {
        setup:
        req.reader >> createMockReader("@jenkins+")
        req.queryString >> "project=dummy"

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == NoSuchProjectExecutor
        executor.project == "dummy"
    }

    def "create ListExecutor"() {
        setup:
        req.reader >> createMockReader("@jenkins+ list")

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == ListExecutor
    }

    @Unroll
    def "create HelpExecutor : #message"() {
        setup:
        req.reader >> createMockReader(message)

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == HelpExecutor

        where:
        message                || result
        "@jenkins+"            || true
        "@jenkins+ help"       || true
        "@jenkins+ help build" || true
        "@jenkins+ build"      || true
    }

    def "create HelpExecutor : #message - with query string"() {
        setup:
        setUpProject()
        req.reader >> createMockReader(message)
        req.queryString >> "project=typetalk-plugin"

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == HelpExecutor

        where:
        message                || result
        "@jenkins+"            || true
        "@jenkins+ help"       || true
        "@jenkins+ help build" || true
    }

    def "create UndefinedExecutor"() {
        setup:
        req.reader >> createMockReader("@jenkins+ dummy")

        when:
        def executor = WebhookExecutorFactory.create(req, res)

        then:
        executor.class == UndefinedExecutor
    }

    // --- helper method ---

    def createMockReader(message) {
        def resource = this.class.getResource("webhook-request.json")
        def template = new SimpleTemplateEngine().createTemplate(resource).make([message: message])

        new BufferedReader(new StringReader(template.toString()))
    }

    def setUpProject() {
        j.createFreeStyleProject("typetalk-plugin")
    }

}
