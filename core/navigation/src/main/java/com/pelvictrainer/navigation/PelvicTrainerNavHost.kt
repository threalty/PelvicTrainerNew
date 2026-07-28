package com.pelvictrainer.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun PelvicTrainerNavHost() {

    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = Routes.Training.route
    ) {


        composable(
            route = Routes.Training.route
        ) {

            TrainingScreen()

        }


        composable(
            route = Routes.Statistics.route
        ) {

            StatisticsScreen()

        }


        composable(
            route = Routes.Settings.route
        ) {

            SettingsScreen()

        }

    }

}



@Composable
private fun TrainingScreen() {

    androidx.compose.material3.Text(
        text = "Pelvic Trainer\nTraining screen"
    )

}



@Composable
private fun StatisticsScreen() {

    androidx.compose.material3.Text(
        text = "Statistics screen"
    )

}



@Composable
private fun SettingsScreen() {

    androidx.compose.material3.Text(
        text = "Settings screen"
    )

}