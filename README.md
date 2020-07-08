# gpr-for-gradle (Gradle Github Packages Plugin)

Currently this only supports the Gradle Kotlin-DSL. Here's a [Groovy alternative](https://github.com/jarnoharno/gradle-github-packages-plugin).

Have you ever tried to use GitHub Packages for your Java project, just to be disappointed by the fact that there isn't a public repo and everything requires credentials? Are disgusted by the idea of adding more than one line of code per repository to your build.gradle? 

You'll still need to [generate a personal access token](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages), but at least now your build.gradle will look clean!

## Usage

### Add the plugin

(Not published yet)

Using the plugins DSL:

```kotlin
plugins {
    id("io.github.0ffz.github-packages") version "x.x.x"
}
```

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