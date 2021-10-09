package org.jenkinsci.plugins.typetalk

import org.apache.http.HttpStatus
import org.jenkinsci.plugins.typetalk.api.Typetalk
import spock.lang.Ignore
import spock.lang.Specification

class TypetalkSpec extends Specification {

	def config = new Properties()

	def setup() {
		config.load(getClass().getResourceAsStream("/config.properties"))
	}

	// If you want to test, copy 'config.properties.tmpl' to 'config.properties' and modify it.
	@Ignore("not post normally")
	def postMessage() {
		setup:
		Typetalk typetalk = new Typetalk(config['client.id'] as String, config['client.secret'] as String)

		expect:
		typetalk.postMessage(config['topic.id'] as long, "TypetalkSpec : ${new Date()}", null) == HttpStatus.SC_OK
	}

}
