# gpr-for-gradle (Gradle Github Packages Plugin) 

[![Gradle Plugin Portal](https://badgen.net/maven/v/metadata-url/https/plugins.gradle.org/m2/io/github/0ffz/github-packages/io.github.0ffz.github-packages.gradle.plugin/maven-metadata.xml?label=gradlePluginPortal)](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages)  

GitHub Packages introduced features for hosting Maven packages for free, but these require credentials even for public ones.

This grade plugin allows you to add a maven repository from GitHub Packages in one line. It also comes with a default personal access token, allowing the use of public repositories without any extra setup (see [Notes on automatic authentication](#notes-on-automatic-authentication)).

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


### Groovy

Within Groovy, you may add a package repository as follows: 

```groovy
repositories {
    maven githubPackage.invoke("owner/repo")
   // Or for all packages under the owner/org
    maven githubPackage.invoke("owner")
}
```

There is currently no support for further customization in Groovy.

### Kotlin

Add the GitHub repo to the repositories block:

```kotlin
repositories {
    githubPackage("owner/repo")
    // Or for all packages under the owner/org
    githubPackage("owner")
}
```

#### Modifying default username/key

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

## Notes on automatic authentication

It appears sharing a PAT is currently the encouraged solution, as seen by [this post](https://github.community/t/download-from-github-package-registry-without-authentication/14407/38) from a staff member on the GitHub community forms. In a worst case scenario, the plugin will send a detailed message explaining exactly how to set up a token.

### Manual token setup

1. Generate a token at https://github.com/settings/tokens/new?scopes=read:packages&description=GPR%20for%20Gradle
2. Open your global gradle.properties file at `~/.gradle/gradle.properties`
3. Add username and token:
    ```ini
    gpr.user=<GITHUB NAME>
    gpr.key=<GENERATED TOKEN>
   ```
4. You may need to restart your IDE

For more info see this [GitHub docs](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages) page.

`GITHUB_ACTOR` and `GITHUB_TOKEN` will be used within GitHub workflows, unless the username and password are manually changed.

# Plans

- Make extension functions work nicely with Groovy (feel free to open a PR for it)
- Archive this project if Github Packages improve support for maven
