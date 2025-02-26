package com.horllymobile.androidmoview.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.horllymobile.androidmoview.data.Movie
import com.horllymobile.androidmoview.ui.SearchBar
import com.horllymobile.androidmoview.viewmodel.MovieListViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    modifier: Modifier = Modifier,
    movieListViewModel: MovieListViewModel,
    onViewMovie: (Int) -> Unit
) {
    val movieListScreenUIState by movieListViewModel.movieListScreenUIState.collectAsState()
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    val currentParams = mutableMapOf(
        "page" to "1",
        "api_key" to "a8c97dbe46d64455234f67f0b0d9dbec"
    )

    val scrollState =  rememberLazyListState()



    LaunchedEffect(movieListScreenUIState.movies == null) {
        scope.launch {
            movieListViewModel.getPopularMovies(
                params = currentParams
            )
        }
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val result = movieListScreenUIState.movies?.results
                Log.d("result", "$result")
                Log.d("lastVisibleItemIndex", "$lastVisibleItemIndex")
                if (result != null) {
                    Log.d("result", "${result.size}")
                    if (lastVisibleItemIndex != null && lastVisibleItemIndex >= result.size - 1) {
                        Log.d("currentParams", "$currentParams")
                        currentParams["page"] = (currentParams.getValue("page").toInt() + 1).toString()
                        Log.d("currentParams", "$currentParams")
                        movieListViewModel.loadMoreMovies(
                            currentParams
                        )
                    }
                }
            }
    }

    if (movieListScreenUIState.movies == null) {
        LoadingScreen()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    actions = {
                        SearchBar(
                            onValueChange = {
                                searchQuery = it
                                if (searchQuery.isNotEmpty()) {
                                    currentParams["query"] = searchQuery
                                    scope.launch {
                                        movieListViewModel.searchMovie(currentParams)
                                    }

                                } else {
                                    currentParams.remove("query")
                                    scope.launch {
                                        movieListViewModel.getPopularMovies(
                                            params = currentParams
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                state = scrollState
            ) {
                val movies = movieListScreenUIState.movies?.results
                if (movies != null) {
                    items(movies) { movie ->
                        MovieCard(
                            movie = movie,
                            onViewMovie = {
                                onViewMovie(movie.id)
                            }
                        )
                    }
                }
                if (movieListScreenUIState.isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieCard(
    modifier: Modifier = Modifier,
    movie: Movie,
    onViewMovie: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(500.dp)
            .clickable { onViewMovie() }
        ,
        shape = RoundedCornerShape(16.dp)

    ) {
        Column {
            GlideImage(
                model = "https://image.tmdb.org/t/p/w500/${movie.poster_path}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentScale = ContentScale.FillBounds,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        movie.title,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "Rate icon", modifier = Modifier.size(18.dp))
                    Text("${movie.vote_average}", style = MaterialTheme.typography.bodyLarge)
                }
            }
            if (movie.tagline != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(movie.tagline.uppercase(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Language, contentDescription = "Language icon", modifier = Modifier.size(18.dp))
                    Text(movie.original_language.uppercase(), style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = "Visibility icon", modifier = Modifier.size(18.dp))
                    Text("${movie.popularity}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}