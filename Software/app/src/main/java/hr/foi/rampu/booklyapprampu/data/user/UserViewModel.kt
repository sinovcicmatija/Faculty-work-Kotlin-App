package hr.foi.rampu.booklyapprampu.data.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserViewModel(application: Application): AndroidViewModel(application) {

    val readallData: LiveData<List<User>>
    private val repository: UserRepository
    val userDao: UserDao




    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readallData = repository.readAllData
        this.userDao = userDao
    }
    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addUser(user)
        }
    }

    suspend fun getUser(userName: String): User? {
            return repository.getUser(userName)
        }


}


