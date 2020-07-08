package io.github.offz

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

private lateinit var githubUsername: String
private lateinit var githubPassword: String

class GithubPackagesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        githubUsername = (properties["gpr.user"] ?: System.getenv("GITHUB_ACTOR")).toString()
        githubPassword = (properties["gpr.key"] ?: System.getenv("GITHUB_TOKEN")).toString()

        //TODO no idea how to get this to work with Groovy
        /*extensions.add(
            typeOf<RepositoryHandler.(name: String) -> Unit>(),
            "io.github.offz.githubPackage"
        ) {
            io.github.offz.githubPackage(name)
        }*/
    }
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
        url = URI("https://maven.pkg.github.com/$name")
        this.name = name
        this.packageTemplate(name)
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