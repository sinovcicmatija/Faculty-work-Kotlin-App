package hr.foi.rampu.booklyapprampu.fragments.loginAndRegister

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import hr.foi.rampu.booklyapprampu.R
import android.widget.EditText
import android.widget.Toast
import hr.foi.rampu.booklyapprampu.data.user.UserViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import hr.foi.rampu.booklyapprampu.data.SessionManager


lateinit var username: EditText
lateinit var userpassword: EditText
private lateinit var mUserViewModel: UserViewModel
private lateinit var sessionManager: SessionManager
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        sessionManager = SessionManager.getInstance(requireContext())

        val registerButton: Button = view.findViewById(R.id.registerButton)
        val loginButton: Button = view.findViewById(R.id.btnLogin)
        username = view.findViewById(R.id.userName)
        userpassword = view.findViewById(R.id.userPassword)

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        loginButton.setOnClickListener {
            if (username.text.toString().isNotEmpty() && userpassword.text.toString().isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = mUserViewModel.getUser(username.text.toString())
                    if (user != null && user.password == userpassword.text.toString()) {

                        Toast.makeText(requireContext(), "Dobrodošli!", Toast.LENGTH_LONG).show()
                        sessionManager.saveUser(mUserViewModel.userDao, username.text.toString())
                        findNavController().navigate(R.id.action_loginFragment_to_homeScreenActivity)

                            requireActivity().finish()

                    } else {

                        Toast.makeText(requireContext(), "Neispravni login podaci", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Unesite korisničko ime i lozinku", Toast.LENGTH_SHORT).show()
            }

        }



        return view
    }

}



