package com.horllymobile.androidmoview.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.horllymobile.androidmoview.viewmodel.MovieViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel,
    onBack: () -> Unit
) {
    val movieScreenUIState by movieViewModel.movieScreenUIState.collectAsState()

    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    LaunchedEffect(movieScreenUIState.movie == null) {
        val params = mutableMapOf(
            "api_key" to "a8c97dbe46d64455234f67f0b0d9dbec"
        )
        scope.launch {
            movieViewModel.getMovie(
                id = movieScreenUIState.id,
                params = params
            )
        }
    }

    if (movieScreenUIState.movie == null) {
        LoadingScreen()
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(movieScreenUIState.movie!!.title)
                    },
                    actions = {

                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back arrow")
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { _ ->
            val movie = movieScreenUIState.movie
            if (movie != null) {
                Column(
                    modifier = Modifier
                        .verticalScroll(state = scrollState)
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .height(600.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GlideImage(
                            model = "https://image.tmdb.org/t/p/w500/${movie.poster_path}",
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(movie.overview, style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        if (movie.budget != null) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Budget: ${movie.budget.formatCurrency()}")
                            }
                            HorizontalDivider()
                        }
                        if (movie.revenue != null) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Revenue: ${movie.revenue.formatCurrency()}")
                            }
                            HorizontalDivider()
                        }
                        if (movie.release_date.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Release Date: ${movie.release_date.formatDate()}")
                            }
                            HorizontalDivider()
                        }
                        if (movie.runtime != null) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Runtime: ")
                                Spacer(modifier = Modifier.width(5.dp))
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,

                                ) {
                                    Text("${movie.runtime} min")
                                }
                             }
                            HorizontalDivider()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Please wait...")
            Spacer(modifier = Modifier.width(10.dp))
            CircularProgressIndicator(
                strokeWidth = 1.dp,
                modifier = Modifier.size(10.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

fun Int.formatCurrency(): String? {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    return currencyFormat.format(this)
}

fun String.formatDate(pattern: String = "dd MMM, yyyy"): String {
    return try {
        Log.d("formatDate", this)
        val dateFormat = SimpleDateFormat(pattern, Locale("en", "NG"))
        val date: Date = dateFormat.parse(this) ?: return this
        dateFormat.format(date)
    } catch (e: Exception) {
        this  // Return original if parsing fails
    }
}