import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.SessionManager
import hr.foi.rampu.booklyapprampu.data.book.BookViewModel

class RecommendedFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var recommendedBooksAdapter: RecommendedBooksAdapter
    private lateinit var bookViewModel: BookViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommended, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager.getInstance(requireContext())
        recyclerView = view.findViewById(R.id.recyclerViewRecommended)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recommendedBooksAdapter = RecommendedBooksAdapter()
        recyclerView.adapter = recommendedBooksAdapter

        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        val userId = sessionManager.getUserId()
        val readBooks = sessionManager.getReadBooksForUser(userId)

        bookViewModel.allBooks.observe(viewLifecycleOwner) { allBooks ->
            allBooks?.let {
                val randomGenre = sessionManager.getRandomGenreFromReadBooks(readBooks)
                randomGenre?.let { genre ->
                    Log.d("RecommendedFragment", "Genre: $genre") // Log the extracted genre

                    // Now that we have the genre, filter the books based on it
                    val booksWithGenre = allBooks.filter { it.bookGenre == genre }

                    // Submit the filtered list to the adapter
                    recommendedBooksAdapter.submitList(booksWithGenre)
                } ?: Log.d("RecommendedFragment", "No genre found in read books.")
            }
        }
    }
}
