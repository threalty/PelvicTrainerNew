package com.pelvictrainer.feature.training


import kotlinx.coroutines.delay


class TimerEngine {


    suspend fun start(

        seconds: Int,

        onTick: (Int) -> Unit

    ) {


        var current = seconds



        while (current > 0) {


            delay(1000)



            current--



            onTick(current)

        }

    }

}