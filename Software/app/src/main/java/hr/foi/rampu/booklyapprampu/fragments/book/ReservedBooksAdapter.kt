// ReservedBooksAdapter.kt
package hr.foi.rampu.booklyapprampu.fragments.book
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.SessionManager
import hr.foi.rampu.booklyapprampu.data.book.Book

class ReservedBooksAdapter (private val showExtendButton: Boolean = false, private val sessionManager: SessionManager, private val context: Context) : ListAdapter<Book, ReservedBooksAdapter.ReservedBookViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservedBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ReservedBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservedBookViewHolder, position: Int) {
        val reservedBook = getItem(position)

        holder.extendButton.visibility = if (showExtendButton) View.VISIBLE else View.GONE
        if (showExtendButton) {
            val timeRemainingTextView: TextView =
                holder.itemView.findViewById(R.id.textViewTimeRemaining)
            timeRemainingTextView.visibility = View.VISIBLE

            val reservedBooks = sessionManager.getReservedBooksForUser(sessionManager.getUserId())
            val bookReservationDetails =
                reservedBooks.find { it.startsWith(reservedBook.bookISBN + ",") }
            bookReservationDetails?.let {
                val details = it.split(",").map { it.trim() }
                if (details.size > 5) {
                    val reservationTimestamp = details[5].toLong()
                    timeRemainingTextView.text = calculateTimeLeft(reservationTimestamp)
                } else {
                    timeRemainingTextView.text = "Nije dostupno vrijeme"
                }
            }

        }

        holder.titleTextView.text = reservedBook.bookName
        holder.authorTextView.text = reservedBook.bookAuthor
        holder.genreTextView.text = reservedBook.bookGenre
        holder.returnButton.visibility = View.VISIBLE
        holder.returnButton.setOnClickListener {
            val userId = sessionManager.getUserId()
            val bookISBN = reservedBook.bookISBN
            val bookName = reservedBook.bookName
            val bookAuthor = reservedBook.bookAuthor
            val bookImageUrl = reservedBook.bookImageUrl
            val bookGenre = reservedBook.bookGenre


            sessionManager.addReadBook(userId,bookISBN, bookName, bookAuthor, bookImageUrl, bookGenre)
            sessionManager.removeReservation(userId, bookISBN)


            submitList(sessionManager.getReservedBooksForUser(userId).mapNotNull {
                val bookAttributes = it.split(",")
                if (bookAttributes.size >= 4) {
                    Book(0, bookAttributes[1], bookAttributes[2], 0, "", bookAttributes[3], "", bookAttributes[0], bookAttributes[4], 0)
                } else {
                    null
                }
            })
            Toast.makeText(context, "Knjiga uspješno vraćena!", Toast.LENGTH_SHORT).show()
        }

        Glide.with(holder.itemView.context)
            .load(reservedBook.bookImageUrl)
            .placeholder(R.drawable.placeholder_book)
            .error(R.drawable.default_book_cover)
            .into(holder.imageViewBook)
    }

    private fun calculateTimeLeft(reservationTimestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val reservationEndTime = reservationTimestamp + (7 * 24 * 60 * 60 * 1000)
        val timeLeftMillis = reservationEndTime - currentTime

        return if (timeLeftMillis <= 0) {
            "Time expired"
        } else {
            val daysLeft = timeLeftMillis / (24 * 60 * 60 * 1000)
            val hoursLeft = (timeLeftMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
            String.format("%d dana %02d sata rezervacije preostalo", daysLeft, hoursLeft)
        }
    }
    inner class ReservedBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = itemView.findViewById(R.id.textViewAuthor)
        val imageViewBook: ImageView = itemView.findViewById(R.id.imageViewBook)
        val genreTextView: TextView = itemView.findViewById(R.id.textViewGenre)
        val returnButton: Button = itemView.findViewById(R.id.btnReturnBook)
        val extendButton: Button = itemView.findViewById(R.id.btnExtend)

        init {
            extendButton.setOnClickListener {
                val position = adapterPosition
                val book = getItem(position)

                extendReservation(book)
            }
        }
    }
    private fun extendReservation(book: Book) {
        val userId = sessionManager.getUserId()
        val bookISBN = book.bookISBN

        val currentReservedBooks = sessionManager.getReservedBooksForUser(userId).toMutableList()
        var alreadyExtended = false

        val updatedReservedBooks = currentReservedBooks.map {
            if (it.startsWith(bookISBN)) {
                val attributes = it.split(",")
                if (attributes.size > 5 && attributes[5].toBoolean()) {
                    alreadyExtended = true
                    it
                } else {
                    val newReservationTimestamp = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
                    "$bookISBN,${attributes[1]},${attributes[2]},${attributes[3]},${attributes[4]},$newReservationTimestamp,true"
                }
            } else {
                it
            }
        }

        if (alreadyExtended) {
            Toast.makeText(context, "Rezervacija već produžena!", Toast.LENGTH_SHORT).show()
        } else {
            sessionManager.updateReservedBooksForUser(userId, updatedReservedBooks)

            submitList(updatedReservedBooks.mapNotNull {
                val bookAttributes = it.split(",")
                if (bookAttributes.size >= 4) {
                    Book(0, bookAttributes[1], bookAttributes[2], 0, "", bookAttributes[3], "", bookAttributes[0],  bookAttributes[4], 0)
                } else {
                    null
                }
            })

            Toast.makeText(context, "Rezervacija produžena za tjedan dana!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }
}
