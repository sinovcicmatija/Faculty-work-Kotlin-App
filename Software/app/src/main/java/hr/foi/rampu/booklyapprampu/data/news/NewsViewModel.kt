package hr.foi.rampu.booklyapprampu.data.news

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    private val repository = NewsRepository(newsDao)

    val allNews: LiveData<List<News>> = liveData {
        emitSource(repository.getAllNews())
    }

    fun loadNewsFromJson(context: Context) {
        viewModelScope.launch {
            if(repository.isDatabaseEmpty()) {
                repository.insertNewsFromJson(context, "vijesti")
            }
        }
    }
}




