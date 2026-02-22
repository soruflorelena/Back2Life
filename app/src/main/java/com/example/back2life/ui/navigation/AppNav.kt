package com.example.back2life.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.back2life.ui.screens.*

@Composable
fun AppNav(start: String) {
    val nav = rememberNavController()
    AppNavHost(nav, start)
}

@Composable
fun AppNavHost(nav: NavHostController, start: String) {
    NavHost(navController = nav, empezarDestino = start) {
        composable(Route.Auth.path) {
            AuthScreen(
                onAuthed = { nav.navigate(Route.Feed.path) { popUpTo(Route.Auth.path) { inclusive = true } } }
            )
        }
        composable(Route.Feed.path) {
            FeedScreen(
                onCreate = { nav.navigate(Route.CreatePost.path) },
                onOpen = { postId -> nav.navigate(Route.PostDetail.create(postId)) },
                onLogout = { nav.navigate(Route.Auth.path) { popUpTo(Route.Feed.path) { inclusive = true } } }
            )
        }
        composable(Route.CreatePost.path) {
            CrearPostPantalla(
                onCreated = { postId ->
                    nav.navigate(Ruta.PostDetalle.crear(postId)) { popUpTo(Ruta.CrearPost.path) { inclusive = true } }
                },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            route = Ruta.PostDetalle.path,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStack ->
            val postId = backStack.arguments?.getString("postId") ?: ""
            PostDetallePantalla(postId = postId, onBack = { nav.popBackStack() })
        }
    }
}