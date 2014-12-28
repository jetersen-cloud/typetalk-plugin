package org.jenkinsci.plugins.typetalk.webhookaction

import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.BuildExecutor
import org.jenkinsci.plugins.typetalk.webhookaction.executorimpl.UndefinedExecutor
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class WebhookExecutorFactorySpec extends Specification {

    @Unroll
    def "create with message : #message"() {
        setup:
        def req = Mock(HttpServletRequest)
        def res = Mock(HttpServletResponse)

        when:
        def executor = WebhookExecutorFactory.create(req, res, message)

        then:
        executor.class == result

        where:
        message                           || result
        "@jenkins+ dummy"                 || UndefinedExecutor
        "@jenkins+ build typetalk-plugin" || BuildExecutor
    }
}
