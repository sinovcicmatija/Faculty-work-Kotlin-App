package hr.foi.rampu.booklyapprampu.fragments.book

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.data.book.Book
import com.bumptech.glide.Glide
import hr.foi.rampu.booklyapprampu.R

interface BookClickListener {
    fun onBookClick(book: Book)
}

@Suppress("DEPRECATION")
class BookAdapter(private val context: Context, private val bookClickListener: BookClickListener) :
    PagedListAdapter<Book, BookAdapter.BookViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        if (book != null) {
            holder.bind(book, bookClickListener)


            Glide.with(holder.itemView)
                .load(book.bookImageUrl)
                .placeholder(R.drawable.placeholder_book)
                .error(R.drawable.default_book_cover)
                .into(holder.imageViewBook)
        }
    }


    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewBook: ImageView = itemView.findViewById(R.id.imageViewBook)
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = itemView.findViewById(R.id.textViewAuthor)
        val genreTextView: TextView = itemView.findViewById(R.id.textViewGenre)

        fun bind(book: Book, clickListener: BookClickListener) {
            titleTextView.text = book.bookName
            authorTextView.text = book.bookAuthor
            genreTextView.text = book.bookGenre


            itemView.setOnClickListener {
                clickListener.onBookClick(book)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }
}
