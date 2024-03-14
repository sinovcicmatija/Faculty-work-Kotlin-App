import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.SessionManager
import hr.foi.rampu.booklyapprampu.data.book.Book
import hr.foi.rampu.booklyapprampu.fragments.book.ReadBooksAdapter

class ReadFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var readBooksAdapter: ReadBooksAdapter
    private lateinit var rvReadBooks: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_read, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())

        rvReadBooks = view.findViewById(R.id.recyclerViewReadBooks)
        rvReadBooks.layoutManager = LinearLayoutManager(requireContext())
        readBooksAdapter = ReadBooksAdapter()
        rvReadBooks.adapter = readBooksAdapter

        val userId = sessionManager.getUserId()
        val readBooks = sessionManager.getReadBooksForUser(userId)
        val bookList = readBooks.mapNotNull {
            val bookAttributes = it.split(",")
            if (bookAttributes.size >= 5) {
                Book(0, bookAttributes[1], bookAttributes[2], 0, "", bookAttributes[3], "", bookAttributes[0], bookAttributes[4], 0)
            } else {
                null
            }
        }
        Log.d("ReadFragment", "Book List: $bookList")
        readBooksAdapter.submitList(bookList)
    }
}
