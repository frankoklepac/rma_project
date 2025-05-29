package hr.ferit.frankoklepac.rma_project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.frankoklepac.rma_project.R
import hr.ferit.frankoklepac.rma_project.ui.theme.BoneWhite
import hr.ferit.frankoklepac.rma_project.ui.theme.NavigationBackground
import hr.ferit.frankoklepac.rma_project.ui.theme.PrimaryBtnColour
import hr.ferit.frankoklepac.rma_project.ui.theme.SoftGolden
import hr.ferit.frankoklepac.rma_project.viewmodel.NewGameViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import hr.ferit.frankoklepac.rma_project.ui.theme.DarkRed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: NewGameViewModel = viewModel()
) {
    val game by viewModel.game
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val context = LocalContext.current

    var selectedResult by remember { mutableStateOf(game.result) }

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
                text = "New Game",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    OutlinedTextField(
                        value = game.yourChampion,
                        onValueChange = { newValue: String ->
                            viewModel.updateGame("yourChampion", newValue)
                        },
                        label = { Text("Your Champion") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                        colors = TextFieldDefaults.run {
                            outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    OutlinedTextField(
                        value = game.enemyChampion,
                        onValueChange = { newValue: String ->
                            viewModel.updateGame("enemyChampion", newValue)
                        },
                        label = { Text("Enemy Champion") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                        colors = TextFieldDefaults.run {
                            outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                selectedResult = "Win"
                                viewModel.updateGame("result", "Win") },
                            modifier = Modifier
                                .width(140.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if(selectedResult == "Win" ) Color(0xFFFFE4B5)
                                else SoftGolden,
                                contentColor = Color.Black
                            ),
                            shape = MaterialTheme.shapes.medium
                        )  {
                            Text(
                                text = "Win",
                                fontSize = 16.sp
                            )
                        }
                        Button(
                            onClick = {
                                selectedResult = "Lose"
                                viewModel.updateGame("result", "Lose") },
                            modifier = Modifier
                                .width(140.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedResult == "Lose") Color(0xFFFFE4B5) else DarkRed,
                                contentColor = if (selectedResult == "Lose") Color.Black else Color.White
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = "Lose",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(
                            value = game.kills.toString(),
                            onValueChange = {
                                viewModel.updateGame("kills", it.toIntOrNull() ?: 0)
                            },
                            label = { Text("Kills") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                                .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.run {
                                outlinedTextFieldColors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = Color.Gray,
                                    unfocusedLabelColor = Color.Gray
                                )
                            }
                        )
                        OutlinedTextField(
                            value = game.assists.toString(),
                            onValueChange = {
                                viewModel.updateGame("assists", it.toIntOrNull() ?: 0)
                            },
                            label = { Text("Assists") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.run {
                                outlinedTextFieldColors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = Color.Gray,
                                    unfocusedLabelColor = Color.Gray
                                )
                            }
                        )
                        OutlinedTextField(
                            value = game.deaths.toString(),
                            onValueChange = {
                                viewModel.updateGame("deaths", it.toIntOrNull() ?: 0)
                            },
                            label = { Text("Deaths") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                                .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.run {
                                outlinedTextFieldColors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = Color.Gray,
                                    unfocusedLabelColor = Color.Gray
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    OutlinedTextField(
                        value = game.notes,
                        onValueChange = { newValue: String ->
                            viewModel.updateGame("notes", newValue)
                        },
                        label = { Text("Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
                            .height(150.dp),
                        singleLine = false,
                        colors = TextFieldDefaults.run {
                            outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.saveGame(context) {
                                navController.popBackStack()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBtnColour
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = if (isLoading) "Saving..." else "Add Game",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}