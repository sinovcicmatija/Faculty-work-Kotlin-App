package hr.foi.rampu.booklyapprampu.fragments.loginAndRegister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.user.User
import hr.foi.rampu.booklyapprampu.data.user.UserViewModel
import android.util.Log
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel
    lateinit var addUser: EditText
    lateinit var addPass: EditText
    lateinit var addEm: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val addUsr: Button = view.findViewById(R.id.addUsr)
        addUser = view.findViewById(R.id.addUsername)
        addPass = view.findViewById(R.id.addPassword)
        addEm = view.findViewById(R.id.addEmail)


        addUsr.setOnClickListener {
            if (addUser.text.isNullOrEmpty() || addPass.text.isNullOrEmpty() || addEm.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Molimo popunite sva polja!", Toast.LENGTH_LONG)
                    .show()
            } else {
                insertDataToDatabase()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

        }

        return view
    }

    private fun insertDataToDatabase() {

        val user = User(0, addUser.text.toString(), addPass.text.toString(), addEm.text.toString())
        mUserViewModel.addUser(user)
        Toast.makeText(requireContext(), "Uspješna registracija!", Toast.LENGTH_LONG).show()
        Log.d(
            "RegisterFragment",
            "User in database - Username: ${user.username}, Password: ${user.password}, Email: ${user.email}"
        )
    }


}

