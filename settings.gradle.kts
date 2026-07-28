pluginManagement {

    repositories {

        google()

        mavenCentral()

        gradlePluginPortal()

    }

}


dependencyResolutionManagement {

    repositoriesMode.set(
        RepositoriesMode.FAIL_ON_PROJECT_REPOS
    )


    repositories {

        google()

        mavenCentral()

    }

}


rootProject.name = "PelvicTrainer"

include(":core:navigation")
include(":core:common")
include(":core:datastore")
include(":core:database")
include(":core:network")
include(":core:designsystem")
include(":data")
include(":feature:training")

include(":domain")

include(":app")