package com.example.projekat.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projekat.Kviz.ui.quiz
import com.example.projekat.auth.AuthStore
import com.example.projekat.auth.logIn.login
import com.example.projekat.auth.profile.profile
import com.example.projekat.cats.details.catDetails
import com.example.projekat.cats.list.cats
import com.example.projekat.leaderBoard.list.leaderboard
import com.example.projekat.photos.gallery.photoGallery
import com.example.projekat.photos.grid.photoGrid
import kotlinx.coroutines.launch

@Composable
fun CatNavigation(authStore: AuthStore) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val isProfileEmpty by authStore.isEmpty.collectAsState(initial = true)
    var startDest by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            startDest = if (isProfileEmpty) "login" else "cats"
        }
    }

    if (startDest == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else {
        NavHost(
            navController = navController,
            startDestination = startDest!!,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = spring(),
                    initialOffsetX = { it },
                )
            },
            exitTransition = { scaleOut(targetScale = 0.75f) },
            popEnterTransition = { scaleIn(initialScale = 0.75f) },
            popExitTransition = { slideOutHorizontally { it } },
        ) {

            login(
                route = "login",
                onCreate = {
                    navController.navigate(route = "cats")
                }
            )

            cats(
                route = "cats",
                onCatClick = {
                    navController.navigate(route = "cats/$it")
                },
                onKvizClick = {
                    navController.navigate(route = "quiz")
                },
                onProfileClick = {
                    navController.navigate(route = "profile")
                },
                onLeaderBoardClick = {
                    navController.navigate(route = "leaderBoard")
                }
            )

            catDetails(
                route = "cats/{catId}",
//                arguments = listOf(
//                    navArgument(name = "catId") {
//                        nullable = false
//                        type = NavType.StringType
//                    }
//                ),
                onGalleryClick = {
                    navController.navigate(route = "cats/grid/$it")
                },
                onClose = {
                    navController.popBackStack()
                },
                navController = navController,
            )

            photoGrid(
                route = "cats/grid/{catId}",
                arguments = listOf(
                    navArgument(name = "catId") {
                        nullable = false
                        type = NavType.StringType
                    }
                ),
                onPhotoClick = {
                    navController.navigate(route = "photo/$it")
                },
                onClose = {
                    navController.popBackStack()
                },
            )

            photoGallery(
                route = "photo/{catId}",
                arguments = listOf(
                    navArgument(name = "catId") {
                        nullable = false
                        type = NavType.StringType
                    }
                ),
                onClose = {
                    navController.navigateUp()
                },
            )

            leaderboard(
                route = "leaderBoard",
                onCatalogClick = {
                    navController.navigate(route = "cats")
                },
                onProfileClick = {
                    navController.navigate(route = "profile")
                },
                onKvizClick = {
                    navController.navigate(route = "quiz")
                },
            )

            profile(
                route = "profile",
                onCatsClick = {
                    navController.navigate(route = "cats")
                },
                onKvizClick = {
                    navController.navigate(route = "quiz")
                },
                onLeaderboardClick = {
                    navController.navigate(route = "leaderboard")
                },
            )

            quiz(
                route = "quiz",
                onQuizCompleted = {
                    navController.navigate(route = "cats")
                },
                onClose = {
                    navController.navigateUp()
                }
            )


        }
    }
}

inline val SavedStateHandle.catId: String
    get() = checkNotNull(get("catId")) { "catId is mandatory" }

inline val SavedStateHandle.photoId: String
    get() = checkNotNull(get("photoId")) { "photoId is mandatory" }