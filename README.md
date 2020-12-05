# gpr-for-gradle (Gradle Github Packages Plugin) 

[![Gradle Plugin Portal](https://badgen.net/maven/v/metadata-url/https/plugins.gradle.org/m2/io/github/0ffz/github-packages/io.github.0ffz.github-packages.gradle.plugin/maven-metadata.xml?label=gradlePluginPortal)](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages)  

GitHub Packages introduced features for hosting Maven packages for free, but these require credentials even for public ones.

This project helps keep your gradle project cleaner when adding GitHub Packages, and gives simple instructions for users to set up a personal access token to read packages. 
It will also automatically add these permissions when running your build in a GitHub Workflow.

This project is aimed at the Gradle Kotlin-DSL but basic support is present [for Groovy](#Groovy). Consider this [Groovy alternative](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages) as well.

## Usage

### Add the plugin

Using the plugins DSL:

```kotlin
plugins {
    id("io.github.0ffz.github-packages") version "1.x.x"
}
```

<details>
<summary>Using legacy plugin application: </summary>
<p>

```kotlin
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("gradle.plugin.io.github.0ffz:gpr-for-gradle:1.x.x")
  }
}

apply(plugin = "io.github.0ffz.github-packages")
```
</p>
</details>

[You may find it on Gradle's plugin plugin portal](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages) 

### Simple use case

Add your `gpr.user` and `gpr.key` to your global gradle.properties (as described in GitHub's guide)

Everything automatically works for github actions' `GITHUB_ACTOR` and `GITHUB_TOKEN`, unless manually changing the username and password.

Add the GitHub repo to the repositories block:

```kotlin
repositories {
    githubPackage("owner/repo")
}
```

### Modifying default username/key

Use the `githubPackages` blocks above `repositories` to edit the template applied to every package below. Example to change credentials of every repo:

```kotlin
githubPackages {
    credentials {
        username = "name"
        password = "token"
    }
}

repositories {
    githubPackage("owner/repo") //now with different credentials!
}
```

You may also modify each package declaration as you would a regular maven repository:

```kotlin
repositories {
    githubPackage("owner/repo"){
        name = "anotherName"
        credentials {
            username = "name"
            password = "token"
        }
    }
}
```

### Groovy

Within Groovy, you may add a package as follows: 

```groovy
repositories {
    maven githubPackage.invoke("owner/repo")
}
```

There is currently no support for customizing the username, token, etc... within Groovy, but works well if you just need to add public packages.

# Plans

- Make extension functions work nicely with Groovy (feel free to open a PR for it)
- Archive this project if Github Packages improve support for maven
