package hr.foi.rampu.booklyapprampu.data.user

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

     suspend fun getUser(userName: String): User? {
        return userDao.getUser(userName)
    }


}