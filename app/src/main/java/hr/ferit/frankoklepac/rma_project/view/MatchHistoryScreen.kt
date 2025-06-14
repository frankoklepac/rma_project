package hr.ferit.frankoklepac.rma_project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.frankoklepac.rma_project.R
import hr.ferit.frankoklepac.rma_project.model.Game
import hr.ferit.frankoklepac.rma_project.ui.theme.BoneWhite
import hr.ferit.frankoklepac.rma_project.ui.theme.DarkRed
import hr.ferit.frankoklepac.rma_project.ui.theme.NavigationBackground
import hr.ferit.frankoklepac.rma_project.ui.theme.SoftGolden
import hr.ferit.frankoklepac.rma_project.ui.theme.PrimaryBtnColour
import hr.ferit.frankoklepac.rma_project.viewmodel.MatchHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHistoryScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: MatchHistoryViewModel = viewModel()
) {
    val games by viewModel.games
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadGames(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(NavigationBackground),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back Icon",
                    modifier = Modifier
                        .height(44.dp)
                        .width(44.dp)
                        .clickable { onBackClick() }
                )
            }
            Text(
                text = stringResource(R.string.match_history),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = SoftGolden
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else if (games.isEmpty()) {
                Text(
                    text = stringResource(R.string.match_history_empty),
                    color = BoneWhite,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(games) { game ->
                        Spacer(modifier = Modifier.height(35.dp))
                        GameTile(game = game, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun GameTile(game: Game, navController: NavController) {
    val dateText = game.timestamp?.toDate()?.let {
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it)
    } ?: "Unknown Date"

    val kdaDisplay = if (game.deaths == 0) {
        "Perfect!"
    } else {
        val kda = calculateKDA(game.kills, game.deaths, game.assists)
        "%.2f".format(kda)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 5.dp,
                color = if (game.result == "Win") SoftGolden else DarkRed,
                shape = MaterialTheme.shapes.medium
            )
            .clickable {
                navController.navigate("gameDetails/${game.id}")
            },
        colors = CardDefaults.cardColors(
            containerColor = BoneWhite.copy(alpha = 0.9f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${game.yourChampion} vs ${game.enemyChampion}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "KDA: $kdaDisplay",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.End
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(end = 8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color.Gray.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = dateText,
                        modifier = Modifier.padding(8.dp),
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .clickable {
                            navController.navigate("gameDetails/${game.id}")
                        },
                    shape = MaterialTheme.shapes.small,
                    color = PrimaryBtnColour
                ) {
                    Text(
                        text = stringResource(R.string.details),
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun calculateKDA(kills: Int, deaths: Int, assists: Int): Float {
    val denominator = if (deaths == 0) 1 else deaths
    return (kills + assists).toFloat() / denominator
}