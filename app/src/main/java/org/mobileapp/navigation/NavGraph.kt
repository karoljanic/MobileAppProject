package org.mobileapp.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import org.mobileapp.ui.GameView
import org.mobileapp.ui.permission.PermissionsView
import org.mobileapp.ui.map.MapView
import org.mobileapp.ui.login.LoginView
import org.mobileapp.ui.profilemenu.ProfileMenuView

@Composable
@ExperimentalFoundationApi
@ExperimentalAnimationApi
fun NavGraph(navController: NavHostController) {

    AnimatedNavHost(navController = navController,
        //startDestination = Screen.PermissionsScreen.route,
        startDestination = Screen.MapScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {

        composable(
            route = Screen.PermissionsScreen.route
        ) {
            PermissionsView(navigateToLoginScreen = {
                navController.navigate(Screen.LoginScreen.route)
            })
        }

        composable(
            route = Screen.LoginScreen.route
        ) {
            LoginView(navigateToMapScreen = {
                navController.navigate(Screen.MapScreen.route) {
                    popUpTo(Screen.PermissionsScreen.route) {
                        inclusive = true
                    }
                }
            })

            BackHandler(true) { }
        }

        composable(
            route = Screen.ProfileScreen.route
        ) {
            ProfileMenuView(navigateToAuthScreen = {
                navController.navigate(Screen.LoginScreen.route)
            })
        }

        composable(
            route = Screen.MapScreen.route
        ) {
            MapView(navigateToProfileScreen = {
                navController.navigate(Screen.ProfileScreen.route)
            }, navigateToGameScreen = {
                navController.navigate(Screen.GameScreen.route)
            })

            BackHandler(true) { }
        }

        composable(
            route = Screen.GameScreen.route
        ) {
            GameView()
        }
    }
}