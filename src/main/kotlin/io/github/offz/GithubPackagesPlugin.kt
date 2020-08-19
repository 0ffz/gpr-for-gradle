package io.github.offz

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.typeOf
import java.net.URI
import java.nio.file.Paths

private lateinit var currentProject: Project

private val githubUsername: String? by lazy {
    currentProject.properties["gpr.user"]?.toString() ?: System.getenv("GITHUB_ACTOR")
}
private val githubPassword: String? by lazy {
    currentProject.properties["gpr.key"]?.toString() ?: System.getenv("GITHUB_TOKEN")
}

class GithubPackagesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        currentProject = this
        //TODO allow for configuring the action further (do they use Closures for this in groovy?)
        extensions.add(
            typeOf<(name: String) -> Action<MavenArtifactRepository>>(),
            "githubPackage"
        ) { name ->
            Action { applyBaseTemplate(name) }
        }
        extensions.add(
            typeOf<(name: String) -> Action<MavenArtifactRepository>>(),
            "githubPackagePublish"
        ) { name ->
            Action { applyBaseTemplate(name, verifyCredentialsSet = false) }
        }
    }
}

private fun sendInstructions(): Nothing = error(
    """You must specify gpr.user and gpr.key properties in your global gradle properties file.
        See https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages
        The file is at ${Paths.get(System.getProperty("user.home"), ".gradle/gradle.properties")}        
    """.trimIndent()
)

private fun MavenArtifactRepository.applyBaseTemplate(name: String, verifyCredentialsSet: Boolean = true) {
    url = URI("https://maven.pkg.github.com/$name")
    this.name = name.replace('/', '-')
    if (verifyCredentialsSet) {
        githubUsername ?: sendInstructions()
        githubPassword ?: sendInstructions()
    }
    this.packageTemplate(name)
}

/** The template applied to all packages when created. Modified by [githubPackages]. */
private var packageTemplate: MavenArtifactRepository.(name: String) -> Unit = {
    credentials {
        username = githubUsername
        password = githubPassword
    }
}

/**
 * Adds a new repository for the package with url defined as `https://maven.pkg.github.com/`[name].
 *
 * Credentials will be applied automatically, defaulting as follows:
 *
 * ```kotlin
 * username = (properties["gpr.user"] ?: System.getenv("GITHUB_ACTOR")).toString()
 * password = (properties["gpr.key"] ?: System.getenv("GITHUB_TOKEN")).toString()
 * ```
 *
 * You may then optionally modify it as a regular maven repository:
 *
 * ```kotlin
 * githubPackage("owner/repo"){
 *     name = "anotherName"
 *     credentials {
 *         username = "name"
 *         password = "token"
 *     }
 * }
 * ```
 *
 * @param verifyCredentialsSet Whether to send an error message if credentials are not set.
 *
 * These changes will only be applied to the current package, while [githubPackages] would apply them to all
 * the following packages.
 */
fun RepositoryHandler.githubPackage(
    name: String,
    verifyCredentialsSet: Boolean = true,
    init: MavenArtifactRepository.() -> Unit = {}
) =
    maven() {
        applyBaseTemplate(name, verifyCredentialsSet)
        this.init()
    }

/**
 * Modifies the default template used for packages. Example to change credentials:
 *
 * ```kotlin
 * githubPackages {
 *     credentials {
 *         username = "name"
 *         password = "token"
 *     }
 * }
 * ```
 *
 * All packages defined afterwards will apply this template. To apply to a single package use [githubPackage].
 * */
fun githubPackages(new: MavenArtifactRepository.(name: String) -> Unit) {
    packageTemplate = new
}