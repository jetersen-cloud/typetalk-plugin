package org.jenkinsci.plugins.typetalk

import hudson.model.AbstractBuild
import hudson.model.Result
import org.jenkinsci.plugins.typetalk.support.Emoji
import org.jenkinsci.plugins.typetalk.support.ResultSupport
import spock.lang.Specification
import spock.lang.Unroll

class ResultSupportSpec extends Specification {

	@Unroll
	def "convertBuildToMessage : #previousResult to #result"() {
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

	def "convertBuildToMessage : previousBuildIsBuilding"() {
		setup:
		def build = makeMockBuild_previousBuildIsBuilding()

		when:
		def typetalkResult = new ResultSupport().convertBuildToMessage(build)

		then:
		true // NPE is not thrown
		typetalkResult.emoji == Emoji.SMILEY
		typetalkResult.message.contains('success')
	}

	def makeMockBuild_previousBuildIsBuilding() {
		def build = Mock(AbstractBuild)

		build.result >> Result.SUCCESS
		build.previousBuild >> {
			def previousBuild = Mock(AbstractBuild)

			previousBuild.building >> true
			previousBuild.previousBuild >> {
				def morePreviousBuild = Mock(AbstractBuild)
				morePreviousBuild.result >> Result.SUCCESS

				return morePreviousBuild
			}

			return previousBuild
		}

		return build
	}

}
