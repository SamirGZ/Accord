package com.example.accord

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accord.ui.theme.AccentLavender
import com.example.accord.ui.theme.BackgroundViolet
import com.example.accord.ui.theme.ChipUnselected
import com.example.accord.ui.theme.MutedText
import com.example.accord.ui.theme.PrimaryPurple

@Composable
fun LibraryScreen(viewModel: LibraryViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundViolet)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "The Collection",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        FilterRow(
            selected = state.selectedFilter,
            onSelect = viewModel::selectFilter
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentLavender)
                }
            }

            state.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.errorMessage ?: "Something went wrong",
                            color = MutedText,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::loadPerfumes,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            state.visiblePerfumes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No perfumes match this filter",
                        color = MutedText
                    )
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.visiblePerfumes, key = { it.id }) { perfume ->
                        PerfumeCard(
                            perfume = perfume,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.78f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: ScentFilter,
    onSelect: (ScentFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScentFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            Text(
                text = filter.label,
                color = if (isSelected) Color.White else AccentLavender,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) PrimaryPurple else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryPurple else ChipUnselected,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
