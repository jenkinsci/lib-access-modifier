package org.kohsuke.accmod.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.kohsuke.accmod.Restricted;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Enforces the {@link Restricted} access modifier annotations.
 *
 * @author Kohsuke Kawaguchi
 */
@Mojo(name="enforce", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class EnforcerMojo extends AbstractMojo {
    /**
     * The maven project.
     */
    @Parameter(property = "project", readonly = true)
    protected MavenProject project;

    /**
     * If true, skip running the checker entirely.
     */
    @Parameter(property = "access-modifier-checker.skip", defaultValue = "false")
    private boolean skip = false;

    /**
     * If false, print warnings about violations but do not fail the build.
     */
    @Parameter(property = "access-modifier-checker.failOnError", defaultValue = "true")
    private boolean failOnError = true;

    /**
     * Optional properties to also make available to restriction checkers.
     */
    @Parameter
    private Properties properties;

    @Override
    @SuppressFBWarnings(value = {
            "URLCONNECTION_SSRF_FD",
            "PATH_TRAVERSAL_IN",
            "DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"
    })
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping access modifier checks");
            return;
        }
        try {
            File outputDir = new File(project.getBuild().getOutputDirectory());

            List<URL> dependencies = new ArrayList<>();
            for (Artifact a : project.getArtifacts())
                dependencies.add(a.getFile().toURI().toURL());
            URL outputURL = outputDir.toURI().toURL();
            dependencies.add(outputURL);
            getLog().debug("inspecting\n" + dependencies.stream().map(URL::toString).collect(Collectors.joining("\n")));

            final boolean[] failed = new boolean[1];
            Checker checker = new Checker(new URLClassLoader(dependencies.toArray(new URL[0]), getClass().getClassLoader()),
                new ErrorListener() {
                    @Override
                    public void onError(Throwable t, Location loc, String msg) {
                        String locMsg = loc+" "+msg;
                        if (failOnError) {
                            getLog().error(locMsg, t);
                        } else {
                            getLog().warn(locMsg, t);
                        }
                        failed[0] = true;
                    }

                    @Override
                    public void onWarning(Throwable t, Location loc, String msg) {
                        getLog().warn(loc+" "+msg,t);
                    }
                }, properties != null ? properties : new Properties(), getLog());

            // If there is a restriction list in the inspected module itself, load it as well:
            for (String prefix : new String[] {"META-INF/services/annotations/", "META-INF/annotations/"}) {
                URL local = new URL(outputURL, prefix + Restricted.class.getName());
                InputStream self = null;
                try {
                    self = local.openStream();
                    getLog().debug("loaded local index " + local);
                } catch (IOException e) {
                    getLog().debug("could not load local index " + local, e);
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
