package org.jenkinsci.plugins.typetalk.webhookaction.executorimpl

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class BuildExecutorSpec extends Specification {

    @Rule JenkinsRule j = new JenkinsRule()

    @Unroll
    def "execute : #job"() {
        setup:
        j.createFreeStyleProject("hello-typetalk-plugin")

        def req = Mock(HttpServletRequest)
        def res = Mock(HttpServletResponse)
        def writer = new StringWriter()
        res.writer >> new PrintWriter(writer)
        def executor = new BuildExecutor(req, res, job)

        when:
        executor.execute()

        then:
        writer.toString() == result

        where:
        job                     || result
        "hello-typetalk-plugin" || "'hello-typetalk-plugin' has been scheduled\n"
        "dummy"                 || "'dummy' is not found\n"
   }
}
