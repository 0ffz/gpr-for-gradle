plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.12.0"
//    `maven-publish`
}

group = "io.github.0ffz"
version = "1.0.0"

repositories {
    jcenter()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

pluginBundle {
    vcsUrl = "https://github.com/0ffz/gpr-for-gradle"
    tags = listOf("github", "github-packages", "dependency", "maven", "repository")
}

gradlePlugin {
    plugins {
        register("github-packages") {
            id = "io.github.0ffz.github-packages"
            implementationClass = "io.github.0ffz.GithubPackagesPlugin"
            description = "Cleanly add Github Packages maven repos with credentials in global gradle.properties or env variable (for Github Actions)"
        }
    }
}

/*publishing {
    repositories {
        maven("build/repository")
    }
}*/
