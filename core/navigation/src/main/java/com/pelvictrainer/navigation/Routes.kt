package com.pelvictrainer.navigation


sealed class Routes(
    val route: String
) {

    data object Onboarding : Routes(
        route = "onboarding"
    )


    data object Training : Routes(
        route = "training"
    )


    data object Statistics : Routes(
        route = "statistics"
    )


    data object Settings : Routes(
        route = "settings"
    )

}