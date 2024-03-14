package hr.foi.rampu.booklyapprampu.fragments.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.SessionManager
import hr.foi.rampu.booklyapprampu.data.book.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActualFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservedBooksAdapter: ReservedBooksAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_actual, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewReservedBooks)
        reservedBooksAdapter = ReservedBooksAdapter(true, sessionManager, requireContext(),)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = reservedBooksAdapter

        updateReservedBooks()

        return view
    }

    private fun updateReservedBooks() {
        val userId = sessionManager.getUserId()
        CoroutineScope(Dispatchers.Main).launch {
            val reservedBookList = sessionManager.getReservedBooksForUser(userId)
                .mapNotNull {
                    val bookAttributes = it.split(",")
                    if (bookAttributes.size >= 4) {
                        Book(0, bookAttributes[1], bookAttributes[2], 0, "", bookAttributes[3], "", bookAttributes[0], bookAttributes[4], 0)
                    } else {
                        null
                    }
                }

            reservedBooksAdapter.submitList(reservedBookList)
        }
    }
}
