package top.apricityx.workshop.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.apricityx.workshop.data.WorkshopBrowseItem
import top.apricityx.workshop.data.WorkshopRequiredItem

@Composable
fun DownloadDependencyWarningDialog(
    item: WorkshopBrowseItem,
    requiredItems: List<WorkshopRequiredItem>,
    onDismissRequest: () -> Unit,
    onDownloadAllWithDependencies: () -> Unit,
    onDownloadOnlyCurrent: () -> Unit,
) {
    WorkshopDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Some Dependencies Are Missing") },
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
            ) {
                WorkshopButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDownloadAllWithDependencies,
                ) {
                    Text("Download All Dependencies and Item")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                ) {
                    WorkshopTextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    WorkshopOutlinedButton(onClick = onDownloadOnlyCurrent) {
                        Text("Download Item Only")
                    }
                }
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("“${item.title}” has ${requiredItems.size} Workshop dependencies that have not been downloaded.")
            Text("You can download all dependencies first, or download only this item.")
            Text(
                text = requiredItems.joinToString(separator = "\n") { "• ${it.title}" },
            )
        }
    }
}
