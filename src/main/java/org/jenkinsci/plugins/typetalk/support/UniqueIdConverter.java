package org.jenkinsci.plugins.typetalk.support;

import hudson.model.User;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSetList;
import hudson.scm.ChangeLogSet;
import hudson.tasks.Mailer;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.typetalk.TypetalkUniqueIdProperty;

import java.util.*;
import java.util.stream.Collectors;

public class UniqueIdConverter {

    public String changeSetsToAuthorUniqueIds(List<ChangeLogSet<?>> changeSets) {
        Set<String> uniqueIds = changeSets.stream()
                .filter(GitChangeSetList.class::isInstance)
                .map(GitChangeSetList.class::cast)
                .map(this::gitChangeSetListToUniqueIds)
                .reduce(Collections.emptySet(), this::unionSets);
        return uniqueIds.stream()
                .map(uniqueId -> "@" + uniqueId)
                .collect(Collectors.joining(" "));
    }

    Set<String> gitChangeSetListToUniqueIds(GitChangeSetList gitChangeSetList) {
        return gitChangeSetList.getLogs().stream()
                .map(this::gitChangeSetToUniqueId)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }

    String gitChangeSetToUniqueId(GitChangeSet gitChangeSet) {
        return getUniqueIdFromName(gitChangeSet)
                .orElse(getUniqueIdFromEmail(gitChangeSet)
                        .orElse(""));
    }

    Optional<String> getUniqueIdFromName(GitChangeSet gitChangeSet) {
        return getUniqueIdFromProperty(User.get(gitChangeSet.getAuthorName(), false, Collections.emptyMap()));
    }

    /**
     * Return a user's unique id at random when some users have same email address
     */
    Optional<String> getUniqueIdFromEmail(GitChangeSet gitChangeSet) {
        return User.getAll().stream()
                .filter(user -> isSameAddress(gitChangeSet, user))
                .map(this::getUniqueIdFromProperty)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    Optional<String> getUniqueIdFromProperty(User user) {
        if (user == null) {
            return Optional.empty();
        }

        TypetalkUniqueIdProperty property = user.getProperty(TypetalkUniqueIdProperty.class);
        if (property == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(property.getUniqueId());
    }

    boolean isSameAddress(GitChangeSet gitChangeSet, User user) {
        return gitChangeSet.getAuthorEmail().equals(user.getProperty(Mailer.UserProperty.class).getAddress());
    }

    Set<String> unionSets(Set<String> set1, Set<String> set2) {
        HashSet<String> result = new HashSet<>();
        result.addAll(set1);
        result.addAll(set2);

        return result;
    }

}
