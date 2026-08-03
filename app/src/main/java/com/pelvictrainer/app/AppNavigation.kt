package com.pelvictrainer.app


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pelvictrainer.feature.TrainingScreen
import com.pelvictrainer.feature.TrainingSettingsScreen



@Composable
fun AppNavigation(

    navController: NavHostController

) {



    NavHost(

        navController = navController,

        startDestination = "settings"

    ) {



        composable(

            route = "settings"

        ) {



            TrainingSettingsScreen(

                onStartTraining = { preset ->



                    navController.navigate(

                        "training/${preset.id}"

                    )


                }

            )


        }





        composable(

            route = "training/{presetId}"

        ) { entry ->



            val presetId =

                entry.arguments

                    ?.getString("presetId")





            if (presetId != null) {



                TrainingScreen(

                    presetId = presetId

                )


            }


        }


    }


}