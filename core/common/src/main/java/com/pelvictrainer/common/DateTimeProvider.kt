package com.pelvictrainer.common


import java.time.LocalDateTime



interface DateTimeProvider {


    fun now(): LocalDateTime

}