package hr.foi.rampu.booklyapprampu.data.news

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



class NewsRepository(private val newsDao: NewsDao) {
    suspend fun insertNewsFromJson(context: Context, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = getJsonDataFromRaw(context, fileName)
                val gson = Gson()
                val listNewsType = object : TypeToken<List<News>>() {}.type
                val newsList: List<News> = gson.fromJson(jsonString, listNewsType)
                newsList.forEach { news ->
                    insertNews(news)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getJsonDataFromRaw(context: Context, fileName: String): String? {
        return try {
            val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
            context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun getAllNews(): LiveData<List<News>> {
        return newsDao.getAllNews()
    }

    suspend fun insertNews(news: News) {
        newsDao.insert(news)
    }
    suspend fun isDatabaseEmpty(): Boolean {
        return newsDao.getCount() == 0
    }

}
