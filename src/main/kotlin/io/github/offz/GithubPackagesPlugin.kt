package io.github.offz

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.typeOf
import java.net.URI
import java.nio.file.Paths

private lateinit var githubUsername: String
private lateinit var githubPassword: String

class GithubPackagesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        githubUsername = (properties["gpr.user"] ?: System.getenv("GITHUB_ACTOR") ?: sendInstructions()).toString()
        githubPassword = (properties["gpr.key"] ?: System.getenv("GITHUB_TOKEN") ?: sendInstructions()).toString()

        //TODO allow for configuring the action further (do they use Closures for this in groovy?)
        extensions.add(
            typeOf<(name: String) -> Action<MavenArtifactRepository>>(),
            "githubPackage"
        ) { name ->
            Action {
                applyBaseTemplate(name)
            }
        }
    }
}

private fun sendInstructions(): Nothing {
    error(
        """You must specify gpr.user and gpr.key properties in your global gradle properties file.
            See https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages
            The file is at ${Paths.get(System.getProperty("user.home"), ".gradle/gradle.properties")}        
        """.trimIndent()
    )
}

private fun MavenArtifactRepository.applyBaseTemplate(name: String) {
    url = URI("https://maven.pkg.github.com/$name")
    this.name = name.replace('/', '-')
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
 * These changes will only be applied to the current package, while [githubPackages] would apply them to all
 * the following packages.
 */
fun RepositoryHandler.githubPackage(name: String, init: MavenArtifactRepository.() -> Unit = {}) =
    maven() {
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