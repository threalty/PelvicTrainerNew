package com.pelvictrainer.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun PelvicTrainerNavigation(
    startDestination: String = Routes.Onboarding.route
) {

    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        composable(
            route = Routes.Onboarding.route
        ) {

            androidx.compose.material3.Text(
                text = "Onboarding"
            )

        }


        composable(
            route = Routes.Training.route
        ) {

            androidx.compose.material3.Text(
                text = "Training"
            )

        }


        composable(
            route = Routes.Statistics.route
        ) {

            androidx.compose.material3.Text(
                text = "Statistics"
            )

        }


        composable(
            route = Routes.Settings.route
        ) {

            androidx.compose.material3.Text(
                text = "Settings"
            )

        }

    }

}