package org.jenkinsci.plugins.typetalk.support

import hudson.model.User
import hudson.plugins.git.GitChangeSet
import hudson.plugins.git.GitChangeSetList
import hudson.tasks.Mailer
import org.jenkinsci.plugins.typetalk.TypetalkUniqueIdProperty
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification
import spock.lang.Unroll

class UniqueIdConverterSpec extends Specification {

    @Rule JenkinsRule j

    def converter = new UniqueIdConverter()

    def setup() {
        (1..3).each {
            setupUser("name-$it", "email-$it@example.com", "unique-$it")
        }
    }

    @Unroll
    def "gitChangeSetListToUniqueIds"() {
        setup:
        def changeLogSet = new GitChangeSetList(null, null, names.collect { makeGitChangeSet(it, '') })

        expect:
        converter.gitChangeSetListToUniqueIds(changeLogSet) == uniqueIds as Set

        where:
        names                          || uniqueIds
        ['name-1', 'name-2', 'name-1'] || ['unique-1', 'unique-2']
        ['name-1', 'dummy']            || ['unique-1']
    }

    @Unroll
    def "gitChangeSetToUniqueId : #name - #email"() {
        setup:
        def gitChangeSet = makeGitChangeSet(name, email)

        expect:
        converter.gitChangeSetToUniqueId(gitChangeSet) == uniqueId

        where:
        name     | email                 || uniqueId
        'name-1' | 'dummy@example.com'   || 'unique-1'
        'dummy'  | 'email-2@example.com' || 'unique-2'
        'none'   | 'none@example.com'    || ''
    }

    @Unroll
    def "getUniqueIdFromName : #name"() {
        setup:
        def gitChangeSet = makeGitChangeSet(name, '')

        expect:
        converter.getUniqueIdFromName(gitChangeSet) == uniqueId

        where:
        name     || uniqueId
        'name-1' || Optional.of('unique-1')
        'none'   || Optional.empty()
    }

    @Unroll
    def "getUniqueIdFromEmail : #email"() {
        setup:
        setupUser('empty', 'empty@example.com', null)
        def gitChangeSet = makeGitChangeSet('', email)

        expect:
        converter.getUniqueIdFromEmail(gitChangeSet) == uniqueId

        where:
        email                 || uniqueId
        'email-1@example.com' || Optional.of('unique-1')
        'none@example.com'    || Optional.empty()
        'empty@example.com'   || Optional.empty()
    }

    // --- helper method ---

    def setupUser(name, email, uniqueId) {
        def user = User.get(name)
        user.addProperty(new Mailer.UserProperty(email))
        user.addProperty(new TypetalkUniqueIdProperty(uniqueId))
    }

    def makeGitChangeSet(name, email) {
        def gitChangeSet = new GitChangeSet([], false)
        gitChangeSet.committer = name
        gitChangeSet.committerEmail = email

        gitChangeSet
    }

}
