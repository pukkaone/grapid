package organization

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Assigns version to all projects.
 */
class ProjectVersionPlugin implements Plugin<Project> {

  private static final Logger LOGGER = Logging.getLogger(ProjectVersionPlugin)

  private static Git open(Project project) {
    Repository repository = new FileRepositoryBuilder()
        .readEnvironment()
        .findGitDir(project.projectDir)
        .build()
    return new Git(repository)
  }

  private static String readVersion(Project project) {
    String version = 'UNKNOWN'
    try {
      open(project).withCloseable { git ->
        Status status = git.status().call()
        version = git.describe().call()
        if (!status.clean || version ==~ /.*-\d+-g[0-9a-f]+/) {
          // Assign version for work in progress.
          version = '999-SNAPSHOT'
        }
      }
    } catch (GitAPIException | IOException e) {
      LOGGER.error 'Failed to read version', e
    }

    LOGGER.quiet "Version ${version}"
    return version
  }

  @Override
  void apply(Project project) {
    String version = readVersion(project)
    project.allprojects { subProject ->
      subProject.version = version
    }
  }
}
