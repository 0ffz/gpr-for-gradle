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
            Action { applyBaseTemplate(name) }
        }
    }
}

private fun sendInstructions(): Nothing = error(
    """
        Error adding GitHub package repo. Try using your own token:
        1. Generate a token at 
            https://github.com/settings/tokens/new?scopes=read:packages&description=GPR%20for%20Gradle
        2. Open your global gradle.properties file at
            ${Paths.get(System.getProperty("user.home"), ".gradle/gradle.properties")}
        3. Add username and token:
            gpr.user=<GITHUB NAME>
            gpr.key=<GENERATED TOKEN>
        4. You may need to restart your IDE
        For more info see https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages
    """.trimIndent()
)

private fun MavenArtifactRepository.applyBaseTemplate(name: String) {
    url = URI("https://maven.pkg.github.com/$name")
    this.name = name.replace('/', '-')

    //TODO try to find a more specific exception for failing to authenticate with maven
    try {
        this.packageTemplate(name)
    } catch (e: Exception) {
        sendInstructions()
    }
}

/** The template applied to all packages when created. Modified by [githubPackages]. */
private var packageTemplate: MavenArtifactRepository.(name: String) -> Unit = {
    credentials {
        // if either the name or password are unset, use a default encoded personal access token on a blank
        // machine account. Check readme for more info.
        if (githubPassword == null || githubUsername == null) {
            username = "token"
            password =
                "\u0037\u0066\u0066\u0036\u0030\u0039\u0033\u0066\u0032\u0037\u0033\u0036\u0033\u0037\u0064\u0036\u0037\u0066\u0038\u0030\u0034\u0039\u0062\u0030\u0039\u0038\u0039\u0038\u0066\u0034\u0066\u0034\u0031\u0064\u0062\u0033\u0064\u0033\u0038\u0065"
        } else {
            username = githubUsername
            password = githubPassword
        }
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
    init: MavenArtifactRepository.() -> Unit = {}
) = maven() {
    applyBaseTemplate(name)
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
