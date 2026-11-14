package top.apricityx.workshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.apricityx.workshop.LibraryErrorUiState
import top.apricityx.workshop.data.SteamGame
import top.apricityx.workshop.ui.component.GameShowcaseCard
import top.apricityx.workshop.ui.component.MessageTone
import top.apricityx.workshop.ui.component.ScreenSummaryCard
import top.apricityx.workshop.ui.component.SectionHeading
import top.apricityx.workshop.ui.component.WorkshopCenteredState
import top.apricityx.workshop.ui.component.WorkshopLoadingBlock
import top.apricityx.workshop.ui.component.WorkshopMessageBanner
import top.apricityx.workshop.ui.theme.workshopChromePadding
import top.apricityx.workshop.ui.theme.workshopListContentPadding

@Composable
fun LibraryScreen(
    games: List<SteamGame>,
    isLoading: Boolean,
    message: String?,
    error: LibraryErrorUiState?,
    onRetry: () -> Unit,
    onOpenGame: (SteamGame) -> Unit,
    onRemoveGame: (SteamGame) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading && games.isEmpty() -> WorkshopLoadingBlock(
            label = "",
            modifier = modifier.workshopChromePadding(topExtra = 24.dp, bottomExtra = 24.dp),
        )

        error != null && games.isEmpty() -> WorkshopCenteredState(
            title = "Failed to Load Game Library",
            message = buildString {
                append(error.reason)
                if (error.showAcceleratorHint) {
                    append("\n\nYour network may not support direct access to Steam Workshop. Try a VPN or network accelerator, then retry.")
                }
            },
            actionLabel = "Retry",
            onAction = onRetry,
            modifier = modifier.workshopChromePadding(topExtra = 24.dp, bottomExtra = 24.dp),
        )

        games.isEmpty() -> WorkshopCenteredState(
            title = "Your Game Library Is Empty",
            message = message ?: "Tap + in the top-right corner to add a Workshop-enabled game.",
            modifier = modifier.workshopChromePadding(topExtra = 24.dp, bottomExtra = 24.dp),
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = workshopListContentPadding(topExtra = 20.dp, bottomExtra = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            if (isLoading) {
                item {
                    WorkshopMessageBanner(
                        message = "Refreshing game library…",
                        tone = MessageTone.Info,
                    )
                }
            }

            message?.let { info ->
                item {
                    WorkshopMessageBanner(
                        message = info,
                        tone = MessageTone.Info,
                    )
                }
            }

            item {
                SectionHeading(
                    title = "Your Games",
                    subtitle = "Tap “View Workshop” to browse that game's Workshop items.",
                )
            }

            items(games, key = { it.appId.toString() }) { game ->
                GameShowcaseCard(
                    game = game,
                    primaryActionLabel = "View Workshop",
                    onPrimaryAction = { onOpenGame(game) },
                    secondaryActionLabel = "Remove from Library",
                    onSecondaryAction = { onRemoveGame(game) },
                )
            }
        }
    }
}
