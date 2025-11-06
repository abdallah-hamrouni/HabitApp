package com.example.healthtracker.Repositrory


import com.example.healthtracker.Dao.UserDao
import com.example.healthtracker.models.UserEntity
import com.example.healthtracker.Repositrory.UserRepository
import com.example.healthtracker.databese.AppDatabase




class UserRepository(private val userDao: UserDao) {

    suspend fun findUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun insertUser(name: String, email: String, password: String) {
        val user = UserEntity(name = name, email = email, password = password)
        userDao.insertUser(user)
    }

    suspend fun getUser(email: String, password: String): UserEntity? {
        return userDao.getUserByEmail(email)?.takeIf { it.password == password }
    }
}
