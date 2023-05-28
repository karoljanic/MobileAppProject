package org.mobileapp.navigation

import org.mobileapp.navigation.ScreenIdentifiers.GAME_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.LOGIN_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.MAP_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.PERMISSIONS_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.PROFILE_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.TOURNAMENT_SCREEN

sealed class Screen(val route: String) {
    object PermissionsScreen: Screen(PERMISSIONS_SCREEN)
    object LoginScreen: Screen(LOGIN_SCREEN)
    object ProfileScreen: Screen(PROFILE_SCREEN)
    object MapScreen: Screen(MAP_SCREEN)
    object TournamentScreen: Screen(TOURNAMENT_SCREEN)
    object GameScreen: Screen(GAME_SCREEN)
}