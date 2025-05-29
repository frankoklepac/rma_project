package hr.ferit.frankoklepac.rma_project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.frankoklepac.rma_project.R
import hr.ferit.frankoklepac.rma_project.model.Game
import hr.ferit.frankoklepac.rma_project.ui.theme.BoneWhite
import hr.ferit.frankoklepac.rma_project.ui.theme.DefaultBackground
import hr.ferit.frankoklepac.rma_project.ui.theme.NavigationBackground
import hr.ferit.frankoklepac.rma_project.ui.theme.PrimaryBtnColour
import hr.ferit.frankoklepac.rma_project.ui.theme.SoftGolden
import hr.ferit.frankoklepac.rma_project.viewmodel.MatchDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    navController: NavController,
    gameId: String,
    onBackClick: () -> Unit,
    viewModel: MatchDetailsViewModel = viewModel()
) {
    val game by viewModel.game
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val context = LocalContext.current

    LaunchedEffect(gameId) {
        viewModel.loadGame(context, gameId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DefaultBackground)
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
                text = "Match Details",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = SoftGolden
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
            } else if (game == null) {
                Text(
                    text = "Game not found",
                    color = BoneWhite,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${game?.yourChampion?.let { "$it" } ?: "Unknown"} vs ${game?.enemyChampion ?: "Unknown"}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BoneWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (game?.result == "Win") "Victory" else "Defeat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        ),
                        color = if (game?.result == "Win") SoftGolden else Color.Red,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Kills: ${game?.kills ?: 0}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp
                            ),
                            color = BoneWhite
                        )
                        Text(
                            text = "Assists: ${game?.assists ?: 0}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp
                            ),
                            color = BoneWhite
                        )
                        Text(
                            text = "Deaths: ${game?.deaths ?: 0}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp
                            ),
                            color = BoneWhite
                        )
                    }

                    val kdaDisplay = if (game?.deaths == 0) {
                        "Perfect!"
                    } else {
                        val kda = calculateKDA(game?.kills ?: 0, game?.deaths ?: 1, game?.assists ?: 0)
                        "%.2f".format(kda)
                    }
                    Text(
                        text = "KDA: $kdaDisplay",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        color = BoneWhite,
                        textAlign = TextAlign.Center
                    )

                    val dateText = game?.timestamp?.toDate()?.let {
                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it)
                    } ?: "Unknown Date"
                    Text(
                        text = "Played on: $dateText",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        color = BoneWhite
                    )

                    Text(
                        text = "Game Notes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = SoftGolden
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 300.dp)
                            .wrapContentHeight()
                            .border(1.dp, Color.Gray, MaterialTheme.shapes.medium)
                            .verticalScroll(rememberScrollState()),
                        color = BoneWhite.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = game?.notes?.ifEmpty { "No notes" } ?: "No notes",
                            modifier = Modifier.padding(8.dp),
                            color = BoneWhite,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { navController.navigate("editGame/$gameId") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBtnColour
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = "Edit Game",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.deleteGame(context, gameId) {
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = "Delete Game",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateKDA(kills: Int, deaths: Int, assists: Int): Float {
    val denominator = if (deaths == 0) 1 else deaths
    return (kills + assists).toFloat() / denominator
}