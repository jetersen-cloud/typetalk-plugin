package org.jenkinsci.plugins.typetalk.webhookaction

import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.HelpExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.ListExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import spock.lang.Specification
import spock.lang.Unroll

class WebhookExecutorFactorySpec extends Specification {

    def req = Mock(StaplerRequest)
    def res = Mock(StaplerResponse)

    @Unroll
    def "create BuildExecutor : #message"() {
        when:
        def executor = WebhookExecutorFactory.create(req, res, message)

        then:
        executor.class == BuildExecutor
        executor.job == "typetalk-plugin"
        executor.parameters == parameters

        where:
        message                                                   || parameters
        "@jenkins+ build typetalk-plugin"                         || []
        "@jenkins+ build typetalk-plugin 1.0.0"                   || ["1.0.0"]
        "@jenkins+ build typetalk-plugin version=1.0.0"           || ["version=1.0.0"]
        "@jenkins+ build typetalk-plugin version=1.0.0 env=stage" || ["version=1.0.0", "env=stage"]
    }

    def "create ListExecutor"() {
        when:
        def executor = WebhookExecutorFactory.create(req, res, "@jenkins+ list")

        then:
        executor.class == ListExecutor
    }

    def "create HelpExecutor"() {
        when:
        def executor = WebhookExecutorFactory.create(req, res, "@jenkins+ help")

        then:
        executor.class == HelpExecutor
    }

    def "create UndefinedExecutor"() {
        when:
        def executor = WebhookExecutorFactory.create(req, res, "@jenkins+ dummy")

        then:
        executor.class == UndefinedExecutor
    }

}
