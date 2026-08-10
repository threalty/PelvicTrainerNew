pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PelvicTrainer"

include(":app")
include(":domain")
include(":data")

// Core модули
include(":core:common")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")

// Feature модули (без дублей!)
include(":feature:training")
include(":feature:calendar")
include(":feature:statistics")
include(":feature:achievements")
include(":feature:settings")
include(":feature:onboarding")
include(":feature:workouts")
