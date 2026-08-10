package com.pelvictrainer.domain.analytics

interface CrashReporter {
    fun logException(throwable: Throwable)
    fun log(message: String)
    fun setUserId(userId: String)
    fun setUserAttribute(key: String, value: String)
}