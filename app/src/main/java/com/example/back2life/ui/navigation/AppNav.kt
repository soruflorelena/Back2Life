package com.example.back2life.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.back2life.ui.screens.*

@Composable
fun AppNav(start: String) {
    val nav = rememberNavController()
    AppNavHost(nav, start)
}

@Composable
fun AppNavHost(nav: NavHostController, start: String) {
    NavHost(navController = nav, startDestination = start) {
        composable(route = Ruta.Auth.camino) { AuthScreen(onAuthed = { nav.navigate(Ruta.Feed.camino) { popUpTo(Ruta.Auth.camino) { inclusive = true } } }) }
        composable(route = Ruta.Feed.camino) {
            FeedScreen(
                onCreate = { nav.navigate(Ruta.CrearPost.camino) },
                onOpen = { postId -> nav.navigate(Ruta.PostDetalle.crear(postId)) },
                onProfile = { nav.navigate(Ruta.Perfil.camino) }
            )
        }
        composable(route = Ruta.Perfil.camino) {
            PerfilScreen(
                onBack = { nav.popBackStack() },
                onLogout = { nav.navigate(Ruta.Auth.camino) { popUpTo(0) { inclusive = true } } }
            )
        }
        composable(route = Ruta.CrearPost.camino) { CrearPostScreen(onCreated = { postId -> nav.navigate(Ruta.PostDetalle.crear(postId)) { popUpTo(Ruta.CrearPost.camino) { inclusive = true } } }, onBack = { nav.popBackStack() }) }
        composable(route = Ruta.PostDetalle.camino, arguments = listOf(navArgument("postId") { type = NavType.StringType })) { backStack ->
            PostDetalleScreen(postId = backStack.arguments?.getString("postId") ?: "", onBack = { nav.popBackStack() })
        }
    }
}