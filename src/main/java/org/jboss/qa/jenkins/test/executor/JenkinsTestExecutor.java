package org.jboss.qa.jenkins.test.executor;

import org.jboss.qa.jenkins.test.executor.beans.Workspace;
import org.jboss.qa.jenkins.test.executor.phase.cleanup.CleanUpPhase;
import org.jboss.qa.jenkins.test.executor.phase.download.DownloadPhase;
import org.jboss.qa.jenkins.test.executor.phase.runtimeconfiguration.RuntimeConfigurationPhase;
import org.jboss.qa.jenkins.test.executor.phase.maven.MavenPhase;
import org.jboss.qa.jenkins.test.executor.phase.start.StartPhase;
import org.jboss.qa.jenkins.test.executor.phase.staticconfiguration.StaticConfigurationPhase;
import org.jboss.qa.jenkins.test.executor.phase.stop.StopPhase;
import org.jboss.qa.phaser.InstanceRegistry;
import org.jboss.qa.phaser.PhaseTreeBuilder;
import org.jboss.qa.phaser.Phaser;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JenkinsTestExecutor {

	public static final File WORKSPACE = new File("target");

	public static void main(String[] args) throws Exception {
		if (args.length != 1 || args[0] == null || args[0].isEmpty()) {
			throw new Exception("Expected a name of a job class with definition of a Jenkins test execution!");
		}

		// Load job class
		Class<?> jobClass = Class.forName(args[0]);

		// Set default workspace
		InstanceRegistry.insert(new Workspace(WORKSPACE));

		// Create phase-tree
		PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder
				.addPhase(new DownloadPhase())
				.next()
				.addPhase(new StaticConfigurationPhase())
				.addPhase(new StartPhase())
				.addPhase(new RuntimeConfigurationPhase())
				.addPhase(new MavenPhase())
				.addPhase(new StopPhase())
				.addPhase(new CleanUpPhase());

		// Run the Phaser
		new Phaser(builder.build(), jobClass).run();
	}
}
