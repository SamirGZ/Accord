package com.example.accord

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accord.ui.theme.AccentLavender
import com.example.accord.ui.theme.BackgroundViolet
import com.example.accord.ui.theme.CardPurple
import com.example.accord.ui.theme.MutedText
import com.example.accord.ui.theme.PrimaryPurple

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as AppCompatActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundViolet)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "ACCORD",
            color = AccentLavender,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 3.sp
        )
        Text(
            text = "Good day.",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        SurveyCard(
            onTakeSurvey = {
                BottomNavHelper.navigateTo(activity, DiscoverPage::class.java)
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "See all",
                color = AccentLavender,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        BottomNavHelper.navigateTo(activity, LibraryPage::class.java)
                    }
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        FeaturedRow(
            state = state,
            onRetry = viewModel::loadFeatured
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SurveyCard(onTakeSurvey: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardPurple)
            .padding(20.dp)
    ) {
        Text(
            text = "Find your new scent",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onTakeSurvey,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = "TAKE THE SURVEY",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )
        }
    }
}

@Composable
private fun FeaturedRow(
    state: HomeUiState,
    onRetry: () -> Unit
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentLavender)
            }
        }

        state.errorMessage != null -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage ?: "Something went wrong",
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Retry")
                }
            }
        }

        else -> {
            LazyRow(
                modifier = Modifier.height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(state.featured, key = { it.id }) { perfume ->
                    PerfumeCard(
                        perfume = perfume,
                        showNotes = false,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(0.78f)
                    )
                }
            }
        }
    }
}
