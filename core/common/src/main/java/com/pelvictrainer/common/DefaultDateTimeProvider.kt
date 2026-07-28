package com.pelvictrainer.common


import java.time.LocalDateTime


import javax.inject.Inject



class DefaultDateTimeProvider @Inject constructor() :
    DateTimeProvider {



    override fun now(): LocalDateTime {

        return LocalDateTime.now()

    }

}