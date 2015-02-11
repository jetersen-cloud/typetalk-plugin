package org.jenkinsci.plugins.typetalk

import hudson.model.AbstractBuild
import hudson.model.Result
import org.jenkinsci.plugins.typetalk.support.Emoji
import org.jenkinsci.plugins.typetalk.support.ResultSupport
import spock.lang.Specification
import spock.lang.Unroll

class TypetalkMessageSpec extends Specification {

	@Unroll
	def convertFromResult() {
		setup:
		def build = makeMockBuild(result, previousResult)

		when:
		def typetalkResult = new ResultSupport().convertBuildToMessage(build)

		then:
		typetalkResult.emoji == emoji
		typetalkResult.message.contains(message)

		where:
		previousResult | result           || emoji            | message
		Result.SUCCESS | Result.SUCCESS   || Emoji.SMILEY     | 'success'
		Result.SUCCESS | Result.UNSTABLE  || Emoji.CRY        | 'unstable'
		Result.SUCCESS | Result.FAILURE   || Emoji.RAGE       | 'failure'
		Result.SUCCESS | Result.ABORTED   || Emoji.ASTONISHED | 'aborted'
		Result.SUCCESS | Result.NOT_BUILT || Emoji.ASTONISHED | 'Not built'

		Result.FAILURE | Result.SUCCESS   || Emoji.SMILEY     | 'recovery'
		Result.FAILURE | Result.UNSTABLE  || Emoji.CRY        | 'unstable'
		Result.FAILURE | Result.FAILURE   || Emoji.RAGE       | 'failure'
		Result.FAILURE | Result.ABORTED   || Emoji.ASTONISHED | 'aborted'
		Result.FAILURE | Result.NOT_BUILT || Emoji.ASTONISHED | 'Not built'
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
