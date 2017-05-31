package org.kohsuke.accmod.impl;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestrictedElementImplTest {

    @Test
    public void basic() throws Exception {

        assertThat(new RestrictedElementImpl(false, "hudson/util/TimeUnit2")
                           .isSameClass(createLocation("hudson.util.TimeUnit2$4"))).isTrue();

        assertThat(new RestrictedElementImpl(false, "hudson/util/TimeUnit2.someMethod();")
                           .isSameClass(createLocation("hudson.util.TimeUnit2$4"))).isTrue();

        assertThat(new RestrictedElementImpl(false, "hudson/util/XStream2.addCriticalField(Ljava/lang/Class;Ljava/lang/String;)V")
                           .isSameClass(createLocation("jenkins.model.Jenkins"))).isFalse();

        assertThat(new RestrictedElementImpl(false, "jenkins/model/Jenkins.expandVariablesForDirectory(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; ")
                           .isSameClass(createLocation("jenkins.model.Jenkins$DescriptorImpl"))).isTrue();
    }

    private Location createLocation(String value) {
        Location location = mock(Location.class);
        when(location.getClassName()).thenReturn(value);
        return location;
    }
}