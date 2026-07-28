package com.pelvictrainer.designsystem.theme


import androidx.compose.foundation.isSystemInDarkTheme


import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme


import androidx.compose.runtime.Composable



private val LightScheme = lightColorScheme(

    primary = PelvicBlue,

    secondary = PelvicGreen,

    error = PelvicRed,

    background = PelvicBackgroundLight,

    surface = PelvicSurfaceLight

)



private val DarkScheme = darkColorScheme(

    primary = PelvicBlue,

    secondary = PelvicGreen,

    error = PelvicRed,

    background = PelvicBackgroundDark,

    surface = PelvicSurfaceDark

)



@Composable
fun PelvicTrainerTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),

    content: @Composable () -> Unit

) {


    MaterialTheme(

        colorScheme = if (darkTheme) {

            DarkScheme

        } else {

            LightScheme

        },


        typography = PelvicTypography,


        content = content

    )

}