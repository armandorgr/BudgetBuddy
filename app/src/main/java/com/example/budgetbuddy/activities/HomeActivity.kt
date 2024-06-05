package com.example.budgetbuddy.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivityHomeBinding
import com.example.budgetbuddy.viewmodels.FriendsViewModel
import com.example.budgetbuddy.viewmodels.HomeViewModel
import com.example.budgetbuddy.viewmodels.InvitationsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Clase del activity principal el cual se configura la navegación y además se cargan datos correspondientes al usuario
 * que inicio sesión. Además se añaden eventos para cargar los amigos e invitaciones del usuario.
 * La forma de trabajar con las notificaciones y solicitar permisos al usuario para ello fue consultado aquí:
 * https://www.youtube.com/watch?v=q6TL2RyysV4
 *
 * @author Armando Guzmán
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
        /*
        * La configuración tanto del action bar como del Bottom navigation fueron obtenidos de esta fuente:
        * https://www.youtube.com/watch?v=0x5kmLY16qE&t=486s
        * El modo de cargar los viewModel para que su funcionamiento dure tanto como dure el Activity se obtuvo de este fuente:
        * https://stackoverflow.com/questions/46268768/viewmodel-for-fragment-instead-accessing-activity-viewmodel
        * */
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        invitationsViewModel = ViewModelProvider(this)[InvitationsViewModel::class.java]
        friendsViewModel = ViewModelProvider(this)[FriendsViewModel::class.java]

        //Se le piden permisos al usuario para mostrar notificaciones
        requestNotificationPermission()

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
                R.id.nav_home, R.id.nav_groups, R.id.nav_friends, R.id.nav_profile, R.id.nav_invitations
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
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.home_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_manu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return item.onNavDestinationSelected(findNavController(R.id.home_nav_host_fragment))
    }

    /**
     * Método que sirve para solicitar al usuario su permiso para mostrar notificaciones
     * */
    private fun requestNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if(!hasPermission){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}