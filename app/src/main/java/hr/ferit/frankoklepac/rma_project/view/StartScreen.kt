package hr.ferit.frankoklepac.rma_project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.frankoklepac.rma_project.viewmodel.StartViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.res.painterResource
import hr.ferit.frankoklepac.rma_project.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import hr.ferit.frankoklepac.rma_project.ui.theme.NavigationBackground
import hr.ferit.frankoklepac.rma_project.ui.theme.PrimaryBtnColour
import hr.ferit.frankoklepac.rma_project.ui.theme.SecondaryBtnColour
import hr.ferit.frankoklepac.rma_project.ui.theme.SoftGolden


@Composable
fun StartScreen(
    onLoginClick: () -> Unit,
    onRegisterClick : () -> Unit,
    viewModel: StartViewModel = viewModel()
) {

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
            Text(
                text = "LoL Game Journal",
                style = typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                ),
                color = SoftGolden
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    .padding(0.dp, 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Button(onClick = onLoginClick,
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBtnColour
                    ),
                    shape = MaterialTheme.shapes.medium) {
                    Text(
                        text = "Login",
                        fontSize = 18.sp,
                        color = Color(0xFFFFFFFF),
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onRegisterClick,
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryBtnColour
                    ),
                    shape = MaterialTheme.shapes.medium) {
                    Text(
                        text = "Register",
                        fontSize = 18.sp,
                        color = Color(0xFFFFFFFF),
                    )
                }
            }
        }
    }
}
