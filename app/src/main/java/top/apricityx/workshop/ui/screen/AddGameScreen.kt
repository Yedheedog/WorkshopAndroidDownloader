package top.apricityx.workshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import top.apricityx.workshop.AddGameUiState
import top.apricityx.workshop.data.SteamGame
import top.apricityx.workshop.ui.component.GameShowcaseCard
import top.apricityx.workshop.ui.component.MessageTone
import top.apricityx.workshop.ui.component.ScreenSummaryCard
import top.apricityx.workshop.ui.component.SectionHeading
import top.apricityx.workshop.ui.component.WorkshopCenteredState
import top.apricityx.workshop.ui.component.WorkshopLoadingBlock
import top.apricityx.workshop.ui.component.WorkshopMessageBanner
import top.apricityx.workshop.ui.component.WorkshopPanelCard
import top.apricityx.workshop.ui.component.WorkshopButton
import top.apricityx.workshop.ui.component.WorkshopOutlinedTextField
import top.apricityx.workshop.ui.theme.workshopListContentPadding

@Composable
fun AddGameScreen(
    state: AddGameUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDirectAppIdChange: (String) -> Unit,
    onAddById: () -> Unit,
    onAddGame: (SteamGame) -> Unit,
    onOpenGame: (SteamGame) -> Unit,
    onRetryFeaturedLoad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = workshopListContentPadding(topExtra = 20.dp, bottomExtra = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenSummaryCard(
                title = "Add Game",
                subtitle = "Search for a game, enter its GameID, or quickly add one from popular Workshop games.",
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        item {
            WorkshopPanelCard {
                Text(
                    text = "Search games",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WorkshopOutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        label = { Text("Search games") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    WorkshopButton(onClick = onSearch, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Search")
                    }
                }
            }
        }

        item {
            WorkshopPanelCard {
                Text(
                    text = "Enter GameID manually",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WorkshopOutlinedTextField(
                        value = state.directAppIdText,
                        onValueChange = onDirectAppIdChange,
                        label = { Text("Enter GameID manually") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    WorkshopButton(onClick = onAddById, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Add")
                    }
                }
            }
        }

        state.message?.let { message ->
            item {
                WorkshopMessageBanner(
                    message = message,
                    tone = if (message.contains("失败") || message.contains("超时") || message.contains("failed", ignoreCase = true) || message.contains("timed out", ignoreCase = true)) MessageTone.Error else MessageTone.Info,
                )
            }
        }

        if (state.isSearching) {
            item {
                WorkshopLoadingBlock(label = "Searching for Workshop-enabled games…")
            }
        }

        if (state.searchResults.isNotEmpty()) {
            item {
                SectionHeading(
                    title = "Search Results",
                    subtitle = "Add a result to your library or open its Workshop page right away.",
                )
            }

            items(state.searchResults, key = { "search-${it.appId}" }) { game ->
                GameShowcaseCard(
                    game = game,
                    primaryActionLabel = "Add to Library",
                    onPrimaryAction = { onAddGame(game) },
                    secondaryActionLabel = "Open Now",
                    onSecondaryAction = { onOpenGame(game) },
                )
            }
        } else if (state.searchQuery.isNotBlank() && !state.isSearching && !state.searchRequestFailed) {
            item {
                WorkshopCenteredState(
                    title = "No Results Found",
                    message = "Try a shorter search term, or enter the game's AppID directly.",
                )
            }
        }

        item {
            SectionHeading(
                title = "Popular Workshop Games",
            )
        }

        if (state.isLoadingFeatured) {
            item {
                WorkshopLoadingBlock(label = "Loading popular Workshop games…")
            }
        } else if (state.featuredGames.isEmpty() && !state.featuredErrorMessage.isNullOrBlank()) {
            item {
                WorkshopCenteredState(
                    title = "Failed to Load",
                    message = state.featuredErrorMessage,
                    actionLabel = "Retry",
                    onAction = onRetryFeaturedLoad,
                )
            }
        } else if (state.featuredGames.isEmpty()) {
            item {
                WorkshopCenteredState(
                    title = "No Popular Games Available",
                    message = "Try again later, or add a game using Search or its GameID.",
                )
            }
        } else {
            items(state.featuredGames, key = { "featured-${it.appId}" }) { game ->
                GameShowcaseCard(
                    game = game,
                    primaryActionLabel = "Add to Library",
                    onPrimaryAction = { onAddGame(game) },
                    secondaryActionLabel = "Open Now",
                    onSecondaryAction = { onOpenGame(game) },
                )
            }
        }
    }
}
