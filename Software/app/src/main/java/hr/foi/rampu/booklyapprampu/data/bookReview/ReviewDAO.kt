package hr.foi.rampu.booklyapprampu.data.bookReview

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.foi.rampu.booklyapprampu.data.bookReview.Review

@Dao
interface ReviewDao {
    @Query("SELECT * FROM review WHERE isbn = :isbn")
    fun getReviewsForBook(isbn: String): List<Review>

    @Query("SELECT * FROM review WHERE userId = :userId AND isbn = :isbn LIMIT 1")
    fun getUserReviewForBook(userId: Int, isbn: String): Review?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReview(review: Review)
}