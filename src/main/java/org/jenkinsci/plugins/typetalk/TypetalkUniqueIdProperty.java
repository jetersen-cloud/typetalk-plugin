package org.jenkinsci.plugins.typetalk;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Per user property that is unique id for mention.
 */
public class TypetalkUniqueIdProperty extends UserProperty {

    private final String uniqueId;

    @DataBoundConstructor
    public TypetalkUniqueIdProperty(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Extension
    public static final class DescriptorImpl extends UserPropertyDescriptor {

        @Override
        @NonNull
        public String getDisplayName() {
            return "Typetalk Unique ID";
        }

        @Override
        public UserProperty newInstance(User user) {
            return new TypetalkUniqueIdProperty(null);
        }
    }

}
