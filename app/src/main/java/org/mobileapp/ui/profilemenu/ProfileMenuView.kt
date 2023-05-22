package org.mobileapp.ui.profilemenu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.mobileapp.ui.profile.ProfileView
import org.mobileapp.ui.settings.SettingsView

@ExperimentalFoundationApi
@Composable
fun ProfileMenuView(
    navigateToAuthScreen: () -> Unit,
) {
    val tabTitles = listOf("Settings", "Profile", "Leaderboard")
    val pagerState = rememberPagerState(initialPage = 1) {3}
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } },
                    text = { Text(text = title) }
                )
            }
        }
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> SettingsView(navigateToAuthScreen = navigateToAuthScreen)
                1 -> ProfileView()
            }
        }
    }
}

@Composable
fun SwipeableTab(onSwiped: (Boolean) -> Unit, content: @Composable BoxScope.() -> Unit) {
    var isSwipeToTheLeft by remember { mutableStateOf(false) }
    val dragState = rememberDraggableState(onDelta = { delta ->
        isSwipeToTheLeft = delta > 0
    })

    Box(modifier = Modifier
        .fillMaxSize()
        .draggable(
            state = dragState,
            orientation = Orientation.Horizontal,
            onDragStopped = {
                onSwiped(isSwipeToTheLeft)
            }),
        content = { content(this) })
}