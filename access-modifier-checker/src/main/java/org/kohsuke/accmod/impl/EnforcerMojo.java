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
import java.util.Properties;

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

    /**
     * If true, skip running the checker entirely.
     * @parameter expression="${access-modifier-checker.skip}" default-value="false"
     */
    private boolean skip;

    /**
     * If false, print warnings about violations but do not fail the build.
     * @parameter expression="${access-modifier-checker.failOnError}" default-value="true"
     */
    private boolean failOnError = true;

    /**
     * Optional properties to also make available to restriction checkers.
     * @parameter
     */
    private Properties properties;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping access modifier checks");
            return;
        }
        try {
            File outputDir = new File(project.getBuild().getOutputDirectory());

            List<URL> dependencies = new ArrayList<URL>();
            for (Artifact a : (Collection<Artifact>)project.getArtifacts())
                dependencies.add(a.getFile().toURI().toURL());
            URL outputURL = outputDir.toURI().toURL();
            dependencies.add(outputURL);

            final boolean[] failed = new boolean[1];
            Checker checker = new Checker(new URLClassLoader(dependencies.toArray(new URL[dependencies.size()]), getClass().getClassLoader()),
                new ErrorListener() {
                    public void onError(Throwable t, Location loc, String msg) {
                        String locMsg = loc+" "+msg;
                        if (failOnError) {
                            getLog().error(locMsg, t);
                        } else {
                            getLog().warn(locMsg, t);
                        }
                        failed[0] = true;
                    }

                    public void onWarning(Throwable t, Location loc, String msg) {
                        getLog().warn(loc+" "+msg,t);
                    }
                }, properties != null ? properties : new Properties(), getLog());

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
            checker.check(outputDir);
            if (failed[0]) {
                String message = "Access modifier checks failed. See the details above";
                if (failOnError) {
                    throw new MojoFailureException(message);
                } else {
                    getLog().warn(message);
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to enforce @Restricted constraints",e);
        }
    }
}
