package org.jenkinsci.plugins.typetalk.webhookaction;

public class NoSuchProjectException extends RuntimeException {

    private String project;

    public String getProject() {
        return project;
    }

    public NoSuchProjectException(String project) {
        this.project = project;
    }

}
