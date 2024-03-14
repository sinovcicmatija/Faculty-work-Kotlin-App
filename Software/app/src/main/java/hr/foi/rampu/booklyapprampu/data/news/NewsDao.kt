package hr.foi.rampu.booklyapprampu.data.news

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {
    @Query("SELECT * FROM news")
     fun getAllNews(): LiveData<List<News>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(news: News)

    @Query("DELETE FROM news")
    suspend fun deleteAllNews()
    @Query("SELECT COUNT(*) FROM news")
    suspend fun getCount(): Int
}
