package com.example.budgetbuddy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivityHomeBinding
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.InvitationsViewModel
import com.example.budgetbuddy.viewmodels.NewGroupViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Clase del activity principal el cual se configura la navegacion y ademas se cargan datos correspondientes al usuario
 * que inicio sesion. Ademas se añaden eventos para cargar los amigos e invitaciones del usuario.
 * */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var invitationsViewModel: InvitationsViewModel
    private lateinit var friendsViewModel: FriendsViewModel
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        invitationsViewModel = ViewModelProvider(this)[InvitationsViewModel::class.java]
        friendsViewModel = ViewModelProvider(this)[FriendsViewModel::class.java]

        //Se cargan el usuario actual asi como las invitaciones que tenga y sus amigos
        lifecycleScope.launch {
            viewModel.loadCurrentUser()
            viewModel.firebaseUser.value?.uid?.let {
                invitationsViewModel.loadInvitations(it)
                friendsViewModel.loadFriends(it)
            }
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //Diferences pestañas del menu de navagacion
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_groups, R.id.nav_friends, R.id.nav_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setupWithNavController(
            navController
        )
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).background = null
        //Se añade evento de navagacion al boton flotante del centro
        findViewById<FloatingActionButton>(R.id.floatingBtn).setOnClickListener {
            navController.navigate(R.id.nav_to_newGroup)
        }
    }

    /**
     * Metodo que sirve para navegar al fragmento de grupos
     * */
    fun goToGroups() {
        navController.navigate(R.id.nav_groups)
    }

    /**
     * Metodo que sirve para navegar al fragmento de group overview
     * */
    fun goToGroupOverview() {
        navController.navigate(R.id.nav_groups_to_overview)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.home_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}