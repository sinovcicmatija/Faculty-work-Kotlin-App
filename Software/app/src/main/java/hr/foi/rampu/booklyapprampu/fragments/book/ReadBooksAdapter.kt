package hr.foi.rampu.booklyapprampu.fragments.book
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.book.Book

class ReadBooksAdapter : ListAdapter<Book, ReadBooksAdapter.ReadBookViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ReadBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReadBookViewHolder, position: Int) {
        val readBook = getItem(position)
        Log.d("ReadBooksAdapter", "Book genre: ${readBook.bookGenre}")
        holder.titleTextView.text = readBook.bookName
        holder.authorTextView.text = readBook.bookAuthor
        holder.genreTextView.text = readBook.bookGenre

        Glide.with(holder.itemView.context)
            .load(readBook.bookImageUrl)
            .placeholder(R.drawable.placeholder_book)
            .error(R.drawable.default_book_cover)
            .into(holder.imageViewBook)
    }

    inner class ReadBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = itemView.findViewById(R.id.textViewAuthor)
        val imageViewBook: ImageView = itemView.findViewById(R.id.imageViewBook)
        val genreTextView: TextView = itemView.findViewById(R.id.textViewGenre)
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
