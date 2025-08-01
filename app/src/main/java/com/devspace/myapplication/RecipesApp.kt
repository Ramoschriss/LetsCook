package com.devspace.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RecipesApp() {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "recipesList") {
        composable(route = "recipesList") {
            RecipesListScreen(navController)
        }

        composable(route = "recipesDetail") {
            RecipesDetailScreen()
        }

    }

}