package com.horllymobile.androidmoview.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.horllymobile.androidmoview.ui.screens.MovieListScreen
import com.horllymobile.androidmoview.ui.screens.MovieScreen
import com.horllymobile.androidmoview.viewmodel.MovieListViewModel
import com.horllymobile.androidmoview.viewmodel.MovieViewModel

sealed class NavRoutes(val route: String) {
    data object Movies : NavRoutes("movies")
    data object Movie : NavRoutes("movie")
}


@Composable
fun MainUI(
    modifier: Modifier = Modifier
) {
    val navigationController = rememberNavController()
    val movieListViewModel: MovieListViewModel = viewModel()
    val movieViewModel: MovieViewModel = viewModel()

    NavHost(navigationController, startDestination = NavRoutes.Movies.route, modifier = modifier) {
        composable(NavRoutes.Movies.route) {
            MovieListScreen(
                movieListViewModel = movieListViewModel,
                onViewMovie = { movieId ->
                    movieViewModel.updateMovieId(movieId)
                    navigationController.navigate(NavRoutes.Movie.route)
                }
            )
        }
        composable(NavRoutes.Movie.route) {
            MovieScreen(
                movieViewModel = movieViewModel,
                onBack = {
                    movieViewModel.updateMovie(null)
                    if (navigationController.currentBackStackEntry?.destination?.route != null) {
                        navigationController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    var value by rememberSaveable {
        mutableStateOf("")
    }
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                value = it
                onValueChange(value)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(50.dp).fillMaxWidth(),
            placeholder = { Text("Search Movie") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "") },
            trailingIcon = if (value.isNotEmpty()) {
                {
                    IconButton(onClick = {
                        value = ""
                        onValueChange(value)
                    })  {
                        Icon(Icons.Filled.Close, contentDescription = "")
                    }
                }
            } else null
        )
    }
}