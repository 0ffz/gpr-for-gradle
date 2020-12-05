plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.12.0"
    `maven-publish`
}

group = "io.github.0ffz"
version = "1.1.0"

repositories {
    jcenter()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

pluginBundle {
    website = "https://github.com/0ffz/gpr-for-gradle"
    vcsUrl = "https://github.com/0ffz/gpr-for-gradle.git"
    tags = listOf("github", "github-packages", "dependency", "maven", "repository")
}

gradlePlugin {
    plugins {
        register("github-packages") {
            displayName = "GitHub Packages for gradle"
            id = "io.github.0ffz.github-packages"
            implementationClass = "io.github.offz.GithubPackagesPlugin"
            description = "Cleanly add Github Packages maven repos with credentials in global gradle.properties or env variable (for Github Actions)"
        }
    }
}

/*publishing {
    repositories {
        maven("build/repository")
    }
}*/
