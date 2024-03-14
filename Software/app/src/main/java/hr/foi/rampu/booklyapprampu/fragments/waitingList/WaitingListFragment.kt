import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.book.BookViewModel
import hr.foi.rampu.booklyapprampu.data.SessionManager
import hr.foi.rampu.booklyapprampu.data.book.Book

import hr.foi.rampu.booklyapprampu.fragments.waitingList.WaitingListAdapter

class WaitingListFragment : Fragment() {

    private lateinit var rvWaitingListBooks: RecyclerView
    private lateinit var waitingListAdapter: WaitingListAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_waiting_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())

        rvWaitingListBooks = view.findViewById(R.id.recyclerViewWaitingList)
        rvWaitingListBooks.layoutManager = LinearLayoutManager(requireContext())
        waitingListAdapter = WaitingListAdapter()
        rvWaitingListBooks.adapter = waitingListAdapter

        val userId = sessionManager.getUserId()
        val waitingListBooks = sessionManager.getWaitingListForUser(userId)
        val bookList = waitingListBooks.mapNotNull {
            val bookAttributes = it.split(",")
            if (bookAttributes.size >= 5) {
               Book(0, bookAttributes[1], bookAttributes[2], 0, "", bookAttributes[3], "", bookAttributes[0], bookAttributes[4], 0)
            } else {
                null
            }
        }
        Log.d("ReadFragment", "Book List: $bookList")
        waitingListAdapter.submitList(bookList)
    }
}
