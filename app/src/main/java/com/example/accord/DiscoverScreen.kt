package com.example.accord

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.accord.ui.theme.AccentLavender
import com.example.accord.ui.theme.BackgroundViolet
import com.example.accord.ui.theme.CardPurple
import com.example.accord.ui.theme.ChipUnselected
import com.example.accord.ui.theme.MutedText
import com.example.accord.ui.theme.PrimaryPurple

@Composable
fun DiscoverScreen(viewModel: DiscoverViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundViolet)
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        if (state.showingResults) {
            ResultsContent(
                state = state,
                onRestart = viewModel::restart,
                onRetry = viewModel::loadCatalog
            )
        } else {
            SurveyContent(
                state = state,
                onSelect = viewModel::toggleOption,
                onContinue = viewModel::continueSurvey
            )
        }
    }
}

@Composable
private fun SurveyContent(
    state: DiscoverUiState,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val question = state.currentQuestion

    Column(modifier = Modifier.fillMaxSize()) {
        ProgressHeader(
            progress = state.progress,
            label = "${state.questionIndex + 1}/${state.totalQuestions}"
        )

        Text(
            text = "SCENT SURVEY",
            color = AccentLavender,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
        )
        Text(
            text = question.prompt,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            lineHeight = 34.sp
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.options.forEach { option ->
                OptionCard(
                    option = option,
                    selected = option.id in state.selectedIds,
                    multiSelect = question.type == QuestionType.MULTI,
                    onClick = { onSelect(option.id) }
                )
            }
        }

        Button(
            onClick = onContinue,
            enabled = state.canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                disabledContainerColor = ChipUnselected,
                disabledContentColor = MutedText
            )
        ) {
            Text(
                text = "CONTINUE  →",
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
private fun ProgressHeader(progress: Float, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(50)),
            color = AccentLavender,
            trackColor = ChipUnselected
        )
        Text(
            text = label,
            color = MutedText,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun OptionCard(
    option: SurveyOption,
    selected: Boolean,
    multiSelect: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        if (selected) PrimaryPurple.copy(alpha = 0.55f) else CardPurple,
        label = "optionBg"
    )
    val border by animateColorAsState(
        if (selected) AccentLavender else ChipUnselected,
        label = "optionBorder"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = option.subtitle,
                color = if (selected) Color.White.copy(alpha = 0.86f) else MutedText,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        SelectionIndicator(selected = selected, multiSelect = multiSelect)
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean, multiSelect: Boolean) {
    val shape = if (multiSelect) RoundedCornerShape(5.dp) else CircleShape
    Box(
        modifier = Modifier
            .size(22.dp)
            .border(2.dp, if (selected) AccentLavender else MutedText, shape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(shape)
                    .background(AccentLavender)
            )
        }
    }
}

@Composable
private fun ResultsContent(
    state: DiscoverUiState,
    onRestart: () -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ProgressHeader(progress = 1f, label = "${state.totalQuestions}/${state.totalQuestions}")
        Text(
            text = "SCENT SURVEY",
            color = AccentLavender,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
        )
        Text(
            text = "Your matches",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif
        )

        when {
            state.isMatching || state.isLoadingCatalog -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentLavender)
                }
            }

            state.errorMessage != null && state.matches.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.errorMessage ?: "Something went wrong", color = MutedText)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 18.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.matches.firstOrNull()?.let { top ->
                        PerfumeCard(
                            perfume = top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.85f)
                        )
                    }
                    state.matches.drop(1).forEach { perfume ->
                        MatchRow(perfume)
                    }
                }
            }
        }

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "START OVER",
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
private fun MatchRow(perfume: Perfume) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardPurple)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = perfume.image_url,
            contentDescription = perfume.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = perfume.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = perfume.brand.orEmpty().ifBlank { "Unknown brand" },
                color = MutedText,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
