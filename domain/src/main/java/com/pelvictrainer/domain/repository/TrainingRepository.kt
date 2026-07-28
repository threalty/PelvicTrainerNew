package com.pelvictrainer.domain.repository


import com.pelvictrainer.domain.model.TrainingSession
import kotlinx.coroutines.flow.Flow


interface TrainingRepository {


    fun getSessions():
            Flow<List<TrainingSession>>



    suspend fun saveSession(
        session: TrainingSession
    )



    suspend fun deleteAll()



    suspend fun startSession()



    suspend fun completeSession(
        session: TrainingSession
    )



}