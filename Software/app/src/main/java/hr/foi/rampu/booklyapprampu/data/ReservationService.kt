package hr.foi.rampu.booklyapprampu.data


import android.app.IntentService
import android.content.Intent
import android.content.Context
import hr.foi.rampu.booklyapprampu.data.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReservationService : IntentService("ReservationService") {

    override fun onHandleIntent(intent: Intent?) {
        val userId = intent?.getIntExtra("userId", -1) ?: -1
        val bookISBN = intent?.getStringExtra("bookISBN") ?: ""

        if (userId != -1 && bookISBN.isNotEmpty()) {

            checkAndRemoveReservation(userId, bookISBN)
        }
    }

    private fun checkAndRemoveReservation(userId: Int, bookISBN: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val sessionManager = SessionManager.getInstance(applicationContext)
            val reservedBooks = sessionManager.getReservedBooksForUser(userId)

            for (reservedBook in reservedBooks) {
                val bookDetails = reservedBook.split(",")
                if (bookDetails.size >= 5) {
                    val bookISBNFromReserved = bookDetails[0]
                    val reservationTimestamp = bookDetails[4].toLongOrNull()

                    if (bookISBN == bookISBNFromReserved && reservationTimestamp != null) {

                        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000
                        val currentTimeMillis = System.currentTimeMillis()

                        if (currentTimeMillis - reservationTimestamp >= oneWeekInMillis) {

                            reservedBooks.remove(reservedBook)
                            sessionManager.updateReservedBooksForUser(userId, reservedBooks)
                            break
                        }
                    }
                }
            }
        }
    }
}
