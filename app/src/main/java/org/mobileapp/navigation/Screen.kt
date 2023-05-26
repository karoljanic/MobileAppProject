package org.mobileapp.navigation

import org.mobileapp.navigation.ScreenIdentifiers.GAME_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.LOGIN_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.MAP_SCREEN
import org.mobileapp.navigation.ScreenIdentifiers.PROFILE_SCREEN

sealed class Screen(val route: String) {
    object LoginScreen: Screen(LOGIN_SCREEN)
    object ProfileScreen: Screen(PROFILE_SCREEN)
    object MapScreen: Screen(MAP_SCREEN)

    object GameScreen: Screen(GAME_SCREEN)
}