package top.apricityx.workshop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.apricityx.workshop.BaiduTranslationApiKeyUiState
import top.apricityx.workshop.R
import top.apricityx.workshop.ui.component.MessageTone
import top.apricityx.workshop.ui.component.WorkshopMessageBanner
import top.apricityx.workshop.ui.component.WorkshopPanelCard
import top.apricityx.workshop.ui.component.WorkshopButton
import top.apricityx.workshop.ui.component.WorkshopOutlinedButton
import top.apricityx.workshop.ui.component.WorkshopOutlinedTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import top.apricityx.workshop.ui.theme.workshopChromePadding

@Composable
fun BaiduTranslationApiKeyScreen(
    state: BaiduTranslationApiKeyUiState,
    onAppIdChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onSave: () -> Unit,
    onTestTranslation: () -> Unit,
    onOpenApiKeyGuide: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .workshopChromePadding(topExtra = 16.dp, bottomExtra = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WorkshopPanelCard {
            Text("Baidu AI Translation Credentials", style = MaterialTheme.typography.titleLarge)
            Text(
                text = if (state.hasSavedCredentials) {
                    "Complete credentials are already saved. Saving changes will overwrite them; clearing the fields and saving will remove the local configuration."
                } else {
                    "Enter both the AppID and API key for Baidu AI text translation. Saving empty fields will clear the corresponding local settings."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            WorkshopOutlinedTextField(
                value = state.appIdInput,
                onValueChange = onAppIdChange,
                label = { Text("AppID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            WorkshopOutlinedTextField(
                value = state.apiKeyInput,
                onValueChange = onApiKeyChange,
                label = { Text("API Key") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            WorkshopButton(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }

            WorkshopOutlinedButton(
                onClick = onTestTranslation,
                enabled = !state.isTesting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp,
                    )
                    Text("Testing Translation…")
                } else {
                    Text("Test Translation")
                }
            }
        }

        WorkshopPanelCard {
            Text("Sample Text", style = MaterialTheme.typography.titleMedium)
            Text(
                text = state.sampleSourceText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            state.testResultText?.let { testResultText ->
                Text("Test Result", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = testResultText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            state.testFailureReason?.let { testFailureReason ->
                Text(
                    text = "Reason for Failure",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = testFailureReason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        state.message?.let { message ->
            WorkshopMessageBanner(
                message = message,
                tone = MessageTone.Success,
            )
        }

        BaiduTranslationTutorialCard(
            onOpenApiKeyGuide = onOpenApiKeyGuide,
        )
    }
}

@Composable
private fun BaiduTranslationTutorialCard(
    onOpenApiKeyGuide: () -> Unit,
) {
    WorkshopPanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Setup Guide", style = MaterialTheme.typography.titleLarge)
            WorkshopOutlinedButton(
                onClick = onOpenApiKeyGuide,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open Baidu Translation Platform")
            }

            baiduTutorialSteps.forEachIndexed { index, step ->
                BaiduTranslationTutorialStep(
                    stepNumber = index + 1,
                    step = step,
                    topPadding = if (index == 0) 0.dp else 4.dp,
                )
            }
        }
    }
}

@Composable
private fun BaiduTranslationTutorialStep(
    stepNumber: Int,
    step: BaiduTutorialStep,
    topPadding: Dp,
) {
    Column(
        modifier = Modifier.padding(top = topPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "$stepNumber. ${step.text}",
            style = MaterialTheme.typography.titleMedium,
        )

        step.note?.let { note ->
            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        step.imageResId?.let { imageResId ->
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = step.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

private data class BaiduTutorialStep(
    val text: String,
    val imageResId: Int? = null,
    val note: String? = null,
)

private val baiduTutorialSteps = listOf(
    BaiduTutorialStep(
        text = "Open the Baidu Translation Platform.",
        note = "Platform URL: `https://fanyi-api.baidu.com/product/13`",
    ),
    BaiduTutorialStep(
        text = "Tap “Get Started”.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_02,
    ),
    BaiduTutorialStep(
        text = "Enter your personal information as prompted.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_03,
    ),
    BaiduTutorialStep(
        text = "Complete identity verification.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_04,
    ),
    BaiduTutorialStep(
        text = "After verification, return to the console and tap “Activate Now”.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_05,
    ),
    BaiduTutorialStep(
        text = "Select “Large Language Model Text Translation”.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_06,
    ),
    BaiduTutorialStep(
        text = "Enter some sample content and submit the application.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_07,
    ),
    BaiduTutorialStep(
        text = "Return to the main page and tap “Developer Information”.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_08,
    ),
    BaiduTutorialStep(
        text = "Make a note of your AppID.",
    ),
    BaiduTutorialStep(
        text = "Open “API Key Management” and create a new API key. You can choose any name.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_10,
    ),
    BaiduTutorialStep(
        text = "Make a note of the new API key.",
        imageResId = R.drawable.baidu_ai_text_tutorial_step_10_result,
    ),
    BaiduTutorialStep(
        text = "回到 App，把 AppID 和 API Key 填进上面的配置区后Save即可使用。",
    ),
)
