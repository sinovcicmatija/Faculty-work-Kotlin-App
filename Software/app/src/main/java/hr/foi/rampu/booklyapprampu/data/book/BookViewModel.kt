package hr.foi.rampu.booklyapprampu.data.book


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository
    val pagedBooks: LiveData<PagedList<Book>>
    val recommendations: LiveData<List<Book>>
    val allBooks: LiveData<List<Book>>


    init {
        val bookDao = BookDatabase.getDatabase(application).bookDao()
        repository = BookRepository(bookDao)
        pagedBooks = repository.getBooksPaged()
        recommendations = MutableLiveData()
        allBooks = repository.allBooks


    }

    fun loadDataFromCsv(context: Context) {
        val maxBooksToLoad = 100
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteAllBooks()
                repository.insertBooksFromCsv(context, maxBooksToLoad)
            }
        }
    }


}
