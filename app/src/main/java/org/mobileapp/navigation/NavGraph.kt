package org.mobileapp.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.mobileapp.ui.game.GameView
import org.mobileapp.ui.permission.PermissionView
import org.mobileapp.ui.map.MapView
import org.mobileapp.ui.login.LoginView
import org.mobileapp.ui.profilemenu.ProfileMenuView

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PermissionsScreen.route,
    ) {

        composable(
            route = Screen.PermissionsScreen.route
        ) {
            PermissionView(navigateToLoginScreen = {
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
            }, navigateToGameScreen = { sid, pid, type, lat, lon ->
                navController.navigate(Screen.GameScreen.route + "/$type/$lat/$lon/$sid/$pid")
            })
        }

        composable(
            route = Screen.GameScreen.route + "/{type}/{lat}/{lon}/{sid}/{pid}"
        ) {
            val gameType = it.arguments?.getString("type")
            val latitude = it.arguments?.getString("lat")
            val longitude = it.arguments?.getString("lon")
            val stageId = it.arguments?.getString("sid")
            val playerId = it.arguments?.getString("pid")

            //BackHandler(true) { }

            GameView(
                stageId!!, playerId!!, gameType!!, latitude!!.toDouble(), longitude!!.toDouble(),
                { navController.popBackStack() }
            )
        }
    }
}