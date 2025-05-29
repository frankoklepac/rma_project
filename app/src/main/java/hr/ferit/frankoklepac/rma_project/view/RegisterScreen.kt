package hr.ferit.frankoklepac.rma_project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
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
import hr.ferit.frankoklepac.rma_project.viewmodel.RegisterViewModel
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.PasswordVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading

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
                text = "Register",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = viewModel.username.value,
                    onValueChange = { viewModel.username.value = it },
                    label = { Text("Username") },
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

                OutlinedTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.value = it },
                    label = { Text("Email") },
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

                OutlinedTextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.value = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.run {
                        outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.confirmPassword.value,
                    onValueChange = { viewModel.confirmPassword.value = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BoneWhite, shape = MaterialTheme.shapes.medium)
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.run {
                        outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        viewModel.register(context) {
                            navController.navigate("login")
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBtnColour
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = if (isLoading) "Registering..." else "Register",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}