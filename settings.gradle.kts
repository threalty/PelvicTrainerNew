pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PelvicTrainer"

include(":app")

include(":core:common")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")

include(":domain")

include(":data")
include(":feature:calendar")
include(":feature:onboarding")
include(":feature:training")
include(":feature:statistics")
include(":feature:settings")
include(":feature:calendar")
