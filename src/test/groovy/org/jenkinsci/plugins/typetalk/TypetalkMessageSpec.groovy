package org.jenkinsci.plugins.typetalk

import hudson.model.AbstractBuild
import hudson.model.Result
import org.jenkinsci.plugins.typetalk.api.TypetalkMessage
import spock.lang.Specification
import spock.lang.Unroll

class TypetalkMessageSpec extends Specification {

	@Unroll
	def convertFromResult() {
		setup:
		def build = makeMockBuild(result, previousResult)

		when:
		def typetalkResult = TypetalkMessage.convertFromResult(build)

		then:
		typetalkResult.emoji == emoji
		typetalkResult.message.contains(message)

		where:
		previousResult | result           || emoji                           | message
		Result.SUCCESS | Result.SUCCESS   || TypetalkMessage.Emoji.SMILEY     | 'success'
		Result.SUCCESS | Result.UNSTABLE  || TypetalkMessage.Emoji.CRY        | 'unstable'
		Result.SUCCESS | Result.FAILURE   || TypetalkMessage.Emoji.RAGE       | 'failure'
		Result.SUCCESS | Result.ABORTED   || TypetalkMessage.Emoji.ASTONISHED | 'aborted'
		Result.SUCCESS | Result.NOT_BUILT || TypetalkMessage.Emoji.ASTONISHED | 'Not built'

		Result.FAILURE | Result.SUCCESS   || TypetalkMessage.Emoji.SMILEY     | 'recovery'
		Result.FAILURE | Result.UNSTABLE  || TypetalkMessage.Emoji.CRY        | 'unstable'
		Result.FAILURE | Result.FAILURE   || TypetalkMessage.Emoji.RAGE       | 'failure'
		Result.FAILURE | Result.ABORTED   || TypetalkMessage.Emoji.ASTONISHED | 'aborted'
		Result.FAILURE | Result.NOT_BUILT || TypetalkMessage.Emoji.ASTONISHED | 'Not built'
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
