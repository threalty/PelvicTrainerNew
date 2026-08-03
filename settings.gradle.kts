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



rootProject.name = "PelvicTrainer2"



include(

    ":app",

    ":domain",

    ":core:database",

    ":feature:training"

)