package org.mobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.mobileapp.settings.Settings.initPreferences
import org.mobileapp.ui.LoginView
import org.mobileapp.ui.MainView
import org.mobileapp.ui.MapView
import org.mobileapp.ui.ProfileView
import org.mobileapp.ui.theme.MobileAppTheme
import org.mobileapp.viewmodel.LoginViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    //private val mainViewModel : ViewModel by viewModels()
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPreferences()

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        // set the path for osmdroid's files
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        setContent {
            MobileAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            val viewModel = viewModel<LoginViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getLoggedInUser() != null) {
                                    navController.navigate("map")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.loginWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onLoginResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isLoginSuccessful) {
                                if(state.isLoginSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("map")
                                    viewModel.reset()
                                }
                            }

                            LoginView(
                                model = state,
                                onLoginClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.login()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable("profile") { ProfileView(
                            userData = googleAuthUiClient.getLoggedInUser(),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.logOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("login")
                                }
                            }) }
                        composable("map") { MapView(onProfileClicked = { navController.navigate("profile") }) }
                    }
                }
            }
        }
    }
}