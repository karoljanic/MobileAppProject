package org.mobileapp.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainView() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "map") {
        composable("profile") {ProfileView()}
        composable("map") {MapView(onProfileClicked = {navController.navigate("profile")})}
    }


}