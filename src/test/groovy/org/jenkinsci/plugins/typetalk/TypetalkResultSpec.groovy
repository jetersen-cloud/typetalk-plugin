package org.jenkinsci.plugins.typetalk

import hudson.model.AbstractBuild
import hudson.model.Result
import spock.lang.Specification
import spock.lang.Unroll

class TypetalkResultSpec extends Specification {

	@Unroll
	def convert() {
		setup:
		def build = makeMockBuild(result, previousResult)

		when:
		def typetalkResult = TypetalkResult.convert(build)

		then:
		typetalkResult.emoji == emoji
		typetalkResult.message.contains(message)

		where:
		previousResult | result           || emoji                           | message
		Result.SUCCESS | Result.SUCCESS   || TypetalkResult.Emoji.SMILEY     | 'success'
		Result.SUCCESS | Result.UNSTABLE  || TypetalkResult.Emoji.CRY        | 'unstable'
		Result.SUCCESS | Result.FAILURE   || TypetalkResult.Emoji.RAGE       | 'failure'
		Result.SUCCESS | Result.ABORTED   || TypetalkResult.Emoji.ASTONISHED | 'aborted'
		Result.SUCCESS | Result.NOT_BUILT || TypetalkResult.Emoji.ASTONISHED | 'Not built'

		Result.FAILURE | Result.SUCCESS   || TypetalkResult.Emoji.SMILEY     | 'recovery'
		Result.FAILURE | Result.UNSTABLE  || TypetalkResult.Emoji.CRY        | 'unstable'
		Result.FAILURE | Result.FAILURE   || TypetalkResult.Emoji.RAGE       | 'failure'
		Result.FAILURE | Result.ABORTED   || TypetalkResult.Emoji.ASTONISHED | 'aborted'
		Result.FAILURE | Result.NOT_BUILT || TypetalkResult.Emoji.ASTONISHED | 'Not built'
	}

	def makeMockBuild(Result result, Result previousResult) {
		def build = Mock(AbstractBuild)

		build.result >> result
		build.previousBuild >> {
			def previousBuild = Mock(AbstractBuild)
			previousBuild.result >> previousResult

			return previousBuild
		}

		return build
	}
}
