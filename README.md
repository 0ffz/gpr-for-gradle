# gpr-for-gradle (Gradle Github Packages Plugin) 

[![Gradle Plugin Portal](https://badgen.net/maven/v/metadata-url/https/plugins.gradle.org/m2/io/github/0ffz/github-packages/io.github.0ffz.github-packages.gradle.plugin/maven-metadata.xml?label=gradlePluginPortal)](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages)

Currently this only supports the Gradle Kotlin-DSL. Here's a [Groovy alternative](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages).

Have you ever tried to use GitHub Packages for your Java project, just to be disappointed by the fact that there isn't a public repo and everything requires credentials? Are disgusted by the idea of adding more than one line of code per repository to your build.gradle? 

You'll still need to [generate a personal access token](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages), but at least now your build.gradle will look clean!

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

# Plans

- Make extension functions work nicely with Groovy (feel free to open a PR for it)