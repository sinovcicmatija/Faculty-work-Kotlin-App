package hr.foi.rampu.booklyapprampu.activities

import ReadFragment
import RecommendedFragment
import WaitingListFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.SessionManager
import hr.foi.rampu.booklyapprampu.databinding.HomeScreenBinding
import hr.foi.rampu.booklyapprampu.fragments.book.ActualFragment
import hr.foi.rampu.booklyapprampu.fragments.book.CatalogFragment
import hr.foi.rampu.booklyapprampu.fragments.news.NewsFragment


class HomeScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: HomeScreenBinding
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        val toogle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.catalog -> openFragment(CatalogFragment())
                R.id.news -> openFragment(NewsFragment())
                R.id.waiting_list -> openFragment(WaitingListFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(CatalogFragment())

        sessionManager = SessionManager.getInstance(this)

        val headerView = binding.navigationDrawer.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.userTextName)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.userTextEmail)

        userNameTextView.text = sessionManager.getUserName()
        userEmailTextView.text = sessionManager.getUserEmail()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_aktualno -> {
                openFragment(ActualFragment())
                binding.navigationDrawer.setCheckedItem(R.id.nav_aktualno)
            }

            R.id.nav_procitao -> {
                openFragment(ReadFragment())
                binding.navigationDrawer.setCheckedItem(R.id.nav_procitao)
            }

            R.id.nav_preporuceno -> {
                openFragment(RecommendedFragment())
                binding.navigationDrawer.setCheckedItem(R.id.nav_preporuceno)
            }

            R.id.nav_logout -> {
                logoutUser()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutUser() {
        val loginIntent = Intent(this, MainActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        if (fragment !is CatalogFragment) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }
}
