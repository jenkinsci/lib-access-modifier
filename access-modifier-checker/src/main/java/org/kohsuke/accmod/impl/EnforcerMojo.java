package org.kohsuke.accmod.impl;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.kohsuke.accmod.Restricted;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Enforces the {@link Restricted} access modifier annotations.
 *
 * @author Kohsuke Kawaguchi
 * @goal enforce
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @author Kohsuke Kawaguchi
 */
public class EnforcerMojo extends AbstractMojo {
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File outputDir = new File(project.getBuild().getOutputDirectory());

            List<URL> dependencies = new ArrayList<URL>();
            for (Artifact a : (Collection<Artifact>)project.getArtifacts())
                dependencies.add(a.getFile().toURI().toURL());
            URL outputURL = outputDir.toURI().toURL();
            dependencies.add(outputURL);

            Checker checker = new Checker(new URLClassLoader(dependencies.toArray(new URL[dependencies.size()]), getClass().getClassLoader()));

            {// if there's restriction list in the inspected module itself, load it as well
                InputStream self = null;
                try {
                    self = new URL(outputURL, "META-INF/annotations/" + Restricted.class.getName()).openStream();
                } catch (IOException e) {
                }
                if (self!=null)
                    checker.loadRestrictions(self, true);
            }

            // perform checks
            final boolean[] failed = new boolean[1];
            checker.setErrorListener(new ErrorListener() {
                public void onError(Throwable t, Location loc, String msg) {
                    getLog().error(loc+" "+msg,t);
                    failed[0] = true;
                }

                public void onWarning(Throwable t, Location loc, String msg) {
                    getLog().warn(loc+" "+msg,t);
                }
            });
            checker.check(outputDir);
            if (failed[0])
                throw new MojoFailureException("Access modifier checks failed. See the details above");
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to enforce @Restricted constraints",e);
        }
    }
}
