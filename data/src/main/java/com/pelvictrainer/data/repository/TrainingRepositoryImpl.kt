package com.pelvictrainer.data.repository


import com.pelvictrainer.data.mapper.toDomain
import com.pelvictrainer.data.mapper.toEntity

import com.pelvictrainer.database.dao.TrainingDao

import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class TrainingRepositoryImpl @Inject constructor(
    private val dao: TrainingDao
) : TrainingRepository {



    override fun getSessions():
            Flow<List<TrainingSession>> {


        return dao.getSessions()
            .map { list ->

                list.map {
                    it.toDomain()
                }

            }

    }




    override suspend fun saveSession(
        session: TrainingSession
    ) {


        dao.insertSession(
            session.toEntity()
        )


    }




    override suspend fun deleteAll() {

        dao.deleteAll()

    }




    override suspend fun startSession() {

    }




    override suspend fun completeSession(
        session: TrainingSession
    ) {


        saveSession(session)


    }


}