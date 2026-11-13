package top.apricityx.workshop.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.apricityx.workshop.AppFrontendMode
import top.apricityx.workshop.AppThemeMode
import top.apricityx.workshop.DownloadSettingsRepository
import top.apricityx.workshop.SettingsUiState
import top.apricityx.workshop.SteamLoginDialogMode
import top.apricityx.workshop.SteamLoginInputMode
import top.apricityx.workshop.SteamLanguagePreference
import top.apricityx.workshop.accountListItems
import top.apricityx.workshop.canSwitchSteamLoginInputMode
import top.apricityx.workshop.displayName
import top.apricityx.workshop.isSteamConfirmationChallenge
import top.apricityx.workshop.steam.protocol.SteamGuardChallengeType
import top.apricityx.workshop.update.UpdateSource
import top.apricityx.workshop.ui.component.MessageTone
import top.apricityx.workshop.ui.component.WorkshopButton
import top.apricityx.workshop.ui.component.WorkshopMessageBanner
import top.apricityx.workshop.ui.component.WorkshopOutlinedButton
import top.apricityx.workshop.ui.component.WorkshopOutlinedTextField
import top.apricityx.workshop.ui.component.WorkshopPopupMenu
import top.apricityx.workshop.ui.component.WorkshopPopupMenuItem
import top.apricityx.workshop.ui.component.WorkshopSlider
import top.apricityx.workshop.ui.component.WorkshopSwitch
import top.apricityx.workshop.ui.component.WorkshopTextButton
import top.apricityx.workshop.ui.component.WorkshopTransparentGlassDialog
import top.apricityx.workshop.ui.theme.isLiquidGlassFrontendEnabled
import top.apricityx.workshop.ui.theme.workshopChromePadding
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onOpenSteamLoginDialog: () -> Unit,
    onDismissSteamLoginDialog: () -> Unit,
    onUpdateSteamLoginUsername: (String) -> Unit,
    onUpdateSteamLoginPassword: (String) -> Unit,
    onUpdateSteamLoginRefreshToken: (String) -> Unit,
    onUpdateSteamGuardCode: (String) -> Unit,
    onSwitchSteamLoginInputMode: (SteamLoginInputMode) -> Unit,
    onSubmitSteamLogin: () -> Unit,
    onOpenRuntimeLog: () -> Unit,
    onShareRuntimeLogBundle: () -> Unit,
    onExportRuntimeLogBundle: () -> Unit,
    onSwitchToAnonymousSteamAccount: () -> Unit,
    onSetActiveSteamAccount: (String) -> Unit,
    onReauthenticateSteamAccount: (String) -> Unit,
    onRemoveSteamAccount: (String) -> Unit,
    onFrontendModeSelected: (AppFrontendMode) -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onSteamLanguagePreferenceSelected: (SteamLanguagePreference) -> Unit,
    onOpenBaiduTranslationApiKeyScreen: () -> Unit,
    onAutoCheckUpdatesChanged: (Boolean) -> Unit,
    onPreferredUpdateSourceSelected: (UpdateSource) -> Unit,
    onManualCheckUpdates: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onThreadCountChange: (String) -> Unit,
    onConcurrentTaskCountChange: (String) -> Unit,
    onModUpdateConcurrentCheckCountChange: (String) -> Unit,
    onAllowSteamAuthenticatedCleartextHttpChanged: (Boolean) -> Unit,
    onExperimentalWorkshopDirectAccessChanged: (Boolean) -> Unit,
    onAutoRenameModFilesToModNameChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSliderInteracting by remember { mutableStateOf(false) }
    val useLightSettingsText =
        MaterialTheme.colorScheme.background.luminance() < 0.35f &&
            MaterialTheme.colorScheme.onSurface.luminance() < 0.45f
    val settingsColorScheme = if (useLightSettingsText) {
        MaterialTheme.colorScheme.copy(
            primary = Color(0xFFAED6FF),
            secondary = Color(0xFFFFCCB3),
            tertiary = Color(0xFF9EE6D7),
            onBackground = Color(0xFFF1F7FF),
            onSurface = Color(0xFFF1F7FF),
            onSurfaceVariant = Color(0xFFBED1E2),
            onPrimary = Color(0xFFF1F7FF),
            onSecondary = Color(0xFFF1F7FF),
            onTertiary = Color(0xFFF1F7FF),
        )
    } else {
        MaterialTheme.colorScheme
    }

    MaterialTheme(colorScheme = settingsColorScheme) {
        Column(
            modifier = modifier
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = !isSliderInteracting,
                )
                .padding(horizontal = 16.dp)
                .workshopChromePadding(topExtra = 16.dp, bottomExtra = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val steamAccountItems = state.steamAuthState.accountListItems()
            val selectedSteamAccount = steamAccountItems.firstOrNull { it.isActive } ?: steamAccountItems.first()
            SettingsSectionCard {
                Text("Steam Accounts", style = MaterialTheme.typography.titleLarge)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WorkshopButton(
                        onClick = onOpenSteamLoginDialog,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Add Account")
                    }
                    if (selectedSteamAccount.accountId != null) {
                        SteamAccountActionsButton(
                            modifier = Modifier.weight(1f),
                            onReauthenticate = { onReauthenticateSteamAccount(selectedSteamAccount.accountId) },
                            onRemove = { onRemoveSteamAccount(selectedSteamAccount.accountId) },
                        )
                    }
                }
//            Text(
//                "Steam 登录过程摘要会直接写入运行Logs；需要排查时，请在下方“Logs”区域查看、分享或导出。",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//            )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Current Account",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    SettingsChoiceDropdown(
                        selectedOption = selectedSteamAccount,
                        options = steamAccountItems,
                        optionLabel = { it.accountName },
                        onOptionSelected = { account ->
                            account.accountId?.let(onSetActiveSteamAccount)
                                ?: onSwitchToAnonymousSteamAccount()
                        },
                    )

                    if (state.steamAuthState.accounts.isEmpty()) {
                        Text(
                            "当前没有已保存的 Steam Accounts。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (selectedSteamAccount.accountId == null) {
                        WorkshopMessageBanner(
                            message = "You are browsing anonymously. Some content that requires sign-in, age confirmation, or specific visibility permissions may not appear.",
                            tone = MessageTone.Info,
                        )
                    } else {
                        Text(
                            text = selectedSteamAccount.statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            SettingsSectionCard {
            Text("Logs", style = MaterialTheme.typography.titleLarge)
            Text(
                "如果下载器工作时出现问题，建议导出Logs包并发送到邮箱：Apricityx@qq.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
//            Text(
//                text = "Logs目录：${state.runtimeLogDirectoryPath.ifBlank { "未初始化" }}",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//            )
//            Text(
//                text = "最近一份运行Logs：${state.latestRuntimeLogPath ?: "还没有生成"}",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                maxLines = 3,
//                overflow = TextOverflow.Ellipsis,
//            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WorkshopOutlinedButton(
                    onClick = onOpenRuntimeLog,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("查看最新Logs")
                }
                WorkshopOutlinedButton(
                    onClick = onShareRuntimeLogBundle,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("分享Logs包")
                }
            }
            WorkshopButton(
                onClick = onExportRuntimeLogBundle,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("导出Logs包")
            }
            }

            SettingsSectionCard {
            Text("Translation Settings", style = MaterialTheme.typography.titleLarge)
            Text(
                "Description translation uses Baidu AI text translation. An AppID and API key are required.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (state.baiduTranslationApiKeyConfigured) {
                    "An AppID and API key are configured. Description translation will use Baidu AI text translation."
                } else {
                    "No AppID or API key is configured. Follow the setup guide if you want to enable translation."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            WorkshopOutlinedButton(
                onClick = onOpenBaiduTranslationApiKeyScreen,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    if (state.baiduTranslationApiKeyConfigured) {
                        "Configure Baidu AI Translation Credentials"
                    } else {
                        "Add Baidu AI Translation Credentials"
                    },
                )
            }
            }

            SettingsSectionCard {
            Text("Appearance", style = MaterialTheme.typography.titleLarge)
            Text(
                "The new interface uses AndroidLiquidGlass containers and navigation. The classic interface remains available. Theme changes take effect immediately.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text("Interface Style", style = MaterialTheme.typography.titleMedium)

            SettingsChoiceDropdown(
                selectedOption = state.selectedFrontendMode,
                options = AppFrontendMode.entries,
                optionLabel = AppFrontendMode::displayName,
                onOptionSelected = onFrontendModeSelected,
            )

            Text("Color Theme", style = MaterialTheme.typography.titleMedium)

            SettingsChoiceDropdown(
                selectedOption = state.selectedThemeMode,
                options = AppThemeMode.entries,
                optionLabel = AppThemeMode::displayName,
                onOptionSelected = onThemeModeSelected,
            )
            }

            SettingsSectionCard {
            Text("Language Preference", style = MaterialTheme.typography.titleLarge)
            Text(
                "影响添加游戏时的 Steam 商店搜索，以及浏览模组时的工坊列表Language Preference。默认使用简体中文。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SettingsChoiceDropdown(
                selectedOption = state.selectedSteamLanguagePreference,
                options = SteamLanguagePreference.entries,
                optionLabel = SteamLanguagePreference::displayName,
                onOptionSelected = onSteamLanguagePreferenceSelected,
            )
            }

            SettingsSectionCard {
            Text("App Updates", style = MaterialTheme.typography.titleLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Check for Updates on Startup", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Check for the latest release in the background on startup. No message is shown when no update is available.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WorkshopSwitch(
                    checked = state.autoCheckUpdatesEnabled,
                    onCheckedChange = onAutoCheckUpdatesChanged,
                )
            }

            Text("Preferred Update Source", style = MaterialTheme.typography.titleMedium)
            Text(
                "Use the selected source first when downloading APKs. Metadata checks automatically fall back to other mirrors and finally to the official GitHub link.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsChoiceDropdown(
                    selectedOption = state.preferredUpdateSource,
                    options = state.availableUpdateSources,
                    optionLabel = { it.displayName },
                    onOptionSelected = onPreferredUpdateSourceSelected,
                    modifier = Modifier.weight(1f),
                )
                WorkshopButton(
                    onClick = onManualCheckUpdates,
                    enabled = !state.updateCheckInProgress,
                ) {
                    if (state.updateCheckInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                    Text(
                        text = if (state.updateCheckInProgress) {
                            "Checking for updates…"
                        } else {
                            "Check for Updates Now"
                        },
                    )
                }
            }

            Text("当前版本：${state.currentVersionText}", style = MaterialTheme.typography.bodyMedium)

            Text("Latest Check Result", style = MaterialTheme.typography.titleMedium)
            Text(
                text = state.updateStatusSummary.ifBlank { "No update check has been performed yet." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            }

            SettingsSectionCard {
            Text("Download and Check Settings", style = MaterialTheme.typography.titleLarge)
            Text(
                "More threads can improve download speed, but also increase the load on your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Allow HTTP Requests with Steam Login Cookies", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "应用已全局放开明文 HTTP 以兼容部分工坊 CDN。关闭时会拦截绑定 Steam Accounts的 HTTP 请求；开启存在登录态经明文链路泄露的风险。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WorkshopSwitch(
                    checked = state.allowSteamAuthenticatedCleartextHttp,
                    onCheckedChange = onAllowSteamAuthenticatedCleartextHttpChanged,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Experimental Direct Workshop Access", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Use selected Steam Community routing rules to try accessing Workshop directly without a network accelerator.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WorkshopSwitch(
                    checked = state.experimentalWorkshopDirectAccessEnabled,
                    onCheckedChange = onExperimentalWorkshopDirectAccessChanged,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Automatically Rename Mod Files", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "When enabled, newly downloaded single-file items are exported using the item title while preserving the original file extension. If the title cannot be detected, the original filename is kept.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WorkshopSwitch(
                    checked = state.autoRenameModFilesToModNameEnabled,
                    onCheckedChange = onAutoRenameModFilesToModNameChanged,
                )
            }

            SettingsDiscreteSlider(
                title = "Threads per Download",
                value = sliderSettingValue(
                    input = state.downloadThreadCountInput,
                    savedValue = state.savedDownloadThreadCount,
                    minValue = DownloadSettingsRepository.MIN_DOWNLOAD_THREADS,
                    maxValue = DownloadSettingsRepository.MAX_DOWNLOAD_THREADS,
                ),
                minValue = DownloadSettingsRepository.MIN_DOWNLOAD_THREADS,
                maxValue = DownloadSettingsRepository.MAX_DOWNLOAD_THREADS,
                supportingText = "范围 ${DownloadSettingsRepository.MIN_DOWNLOAD_THREADS} - ${DownloadSettingsRepository.MAX_DOWNLOAD_THREADS}",
                onValueChange = { onThreadCountChange(it.toString()) },
                onValueChangeFinished = onSave,
                onInteractionActiveChange = { isSliderInteracting = it },
            )

            SettingsDiscreteSlider(
                title = "Concurrent Downloads",
                value = sliderSettingValue(
                    input = state.concurrentDownloadTaskCountInput,
                    savedValue = state.savedConcurrentDownloadTaskCount,
                    minValue = DownloadSettingsRepository.MIN_CONCURRENT_DOWNLOAD_TASKS,
                    maxValue = DownloadSettingsRepository.MAX_CONCURRENT_DOWNLOAD_TASKS,
                ),
                minValue = DownloadSettingsRepository.MIN_CONCURRENT_DOWNLOAD_TASKS,
                maxValue = DownloadSettingsRepository.MAX_CONCURRENT_DOWNLOAD_TASKS,
                supportingText = "范围 ${DownloadSettingsRepository.MIN_CONCURRENT_DOWNLOAD_TASKS} - ${DownloadSettingsRepository.MAX_CONCURRENT_DOWNLOAD_TASKS}",
                onValueChange = { onConcurrentTaskCountChange(it.toString()) },
                onValueChangeFinished = onSave,
                onInteractionActiveChange = { isSliderInteracting = it },
            )

            SettingsDiscreteSlider(
                title = "Concurrent Mod Update Checks",
                value = sliderSettingValue(
                    input = state.modUpdateConcurrentCheckCountInput,
                    savedValue = state.savedModUpdateConcurrentCheckCount,
                    minValue = DownloadSettingsRepository.MIN_MOD_UPDATE_CONCURRENT_CHECKS,
                    maxValue = DownloadSettingsRepository.MAX_MOD_UPDATE_CONCURRENT_CHECKS,
                ),
                minValue = DownloadSettingsRepository.MIN_MOD_UPDATE_CONCURRENT_CHECKS,
                maxValue = DownloadSettingsRepository.MAX_MOD_UPDATE_CONCURRENT_CHECKS,
                supportingText = "范围 ${DownloadSettingsRepository.MIN_MOD_UPDATE_CONCURRENT_CHECKS} - ${DownloadSettingsRepository.MAX_MOD_UPDATE_CONCURRENT_CHECKS}",
                onValueChange = { onModUpdateConcurrentCheckCountChange(it.toString()) },
                onValueChangeFinished = onSave,
                onInteractionActiveChange = { isSliderInteracting = it },
            )
            }

            state.message?.let {
                WorkshopMessageBanner(
                    message = it,
                    tone = MessageTone.Success,
                )
            }

            SettingsSectionCard {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("关于", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Developer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "apricityx",
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenExternalUrl(primaryDeveloperUrl) },
                )
                Text(
                    text = "ZJustin117",
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenExternalUrl(secondaryDeveloperUrl) },
                )
                Text(
                    "Repository:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = repositoryUrl,
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenExternalUrl(repositoryUrl) },
                )
                Text(
                    "This software originally began as a mobile port of a Slay the Spire mod loader. If you are interested in Slay the Spire, check out my other project:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = slayTheAmethystModdedUrl,
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenExternalUrl(slayTheAmethystModdedUrl) },
                )
                Text(
                    "If this project helps you, please consider starring it on GitHub. It means a lot! If you encounter a problem, feel free to open an issue!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                }
            }
        }
    }

    state.steamAuthState.loginDialogState?.let { dialogState ->
        SteamLoginDialog(
            state = dialogState,
            onDismiss = onDismissSteamLoginDialog,
            onUsernameChange = onUpdateSteamLoginUsername,
            onPasswordChange = onUpdateSteamLoginPassword,
            onRefreshTokenChange = onUpdateSteamLoginRefreshToken,
            onGuardCodeChange = onUpdateSteamGuardCode,
            onSwitchInputMode = onSwitchSteamLoginInputMode,
            onSubmit = onSubmitSteamLogin,
        )
    }
}

@Composable
private fun SettingsSectionCard(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val isLiquidFrontend = isLiquidGlassFrontendEnabled()
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isLiquidFrontend) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.22f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            },
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = if (isLiquidFrontend) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
private fun SettingsDiscreteSlider(
    title: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    supportingText: String,
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    onInteractionActiveChange: ((Boolean) -> Unit)? = null,
) {
    val clampedValue = value.coerceIn(minValue, maxValue)
    var sliderValue by remember(minValue, maxValue) {
        mutableFloatStateOf(clampedValue.toFloat())
    }
    var committedValue by remember(minValue, maxValue) {
        mutableStateOf(clampedValue)
    }
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val displayValue = sliderValue.roundToInt().coerceIn(minValue, maxValue)

    LaunchedEffect(clampedValue) {
        if (clampedValue != committedValue) {
            committedValue = clampedValue
            sliderValue = clampedValue.toFloat()
        }
    }
    LaunchedEffect(displayValue) {
        if (displayValue == committedValue) {
            return@LaunchedEffect
        }
        delay(180)
        if (displayValue != committedValue) {
            committedValue = displayValue
            latestOnValueChange(displayValue)
            latestOnValueChangeFinished?.invoke()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = displayValue.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        WorkshopSlider(
            value = sliderValue,
            onValueChange = {
                val nextValue = it.coerceIn(minValue.toFloat(), maxValue.toFloat())
                sliderValue = nextValue
            },
            modifier = Modifier.fillMaxWidth(),
            valueRange = minValue.toFloat()..maxValue.toFloat(),
            steps = (maxValue - minValue - 1).coerceAtLeast(0),
            onInteractionActiveChange = {
                onInteractionActiveChange?.invoke(it)
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = minValue.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = maxValue.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun sliderSettingValue(
    input: String,
    savedValue: Int,
    minValue: Int,
    maxValue: Int,
): Int =
    input.toIntOrNull()?.coerceIn(minValue, maxValue)
        ?: savedValue.coerceIn(minValue, maxValue)

@Composable
private fun <T> SettingsChoiceDropdown(
    selectedOption: T,
    options: Iterable<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        WorkshopOutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = optionLabel(selectedOption),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand Options",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        WorkshopPopupMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                WorkshopPopupMenuItem(
                    text = { Text(optionLabel(option)) },
                    reserveLeadingSpace = true,
                    leadingIcon = {
                        if (option == selectedOption) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
private fun SteamAccountActionsButton(
    modifier: Modifier = Modifier,
    onReauthenticate: () -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        WorkshopOutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("操作")
        }
        WorkshopPopupMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            WorkshopPopupMenuItem(
                text = { Text("重新认证") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                    )
                },
                onClick = {
                    expanded = false
                    onReauthenticate()
                },
            )
            WorkshopPopupMenuItem(
                text = { Text("删除") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                },
                onClick = {
                    expanded = false
                    onRemove()
                },
            )
        }
    }
}

@Composable
private fun SteamLoginDialog(
    state: top.apricityx.workshop.SteamLoginDialogUiState,
    onDismiss: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRefreshTokenChange: (String) -> Unit,
    onGuardCodeChange: (String) -> Unit,
    onSwitchInputMode: (SteamLoginInputMode) -> Unit,
    onSubmit: () -> Unit,
) {
    val isTokenMode = state.inputMode == SteamLoginInputMode.RefreshToken
    val isConfirmationChallenge = state.challengeType.isSteamConfirmationChallenge()

    WorkshopTransparentGlassDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (state.mode == SteamLoginDialogMode.Reauthenticate) {
                    "重新认证 Steam"
                } else {
                    "登录 Steam"
                },
            )
        },
        dismissOnClickOutside = false,
        buttons = {
            WorkshopOutlinedButton(onClick = onDismiss, enabled = !state.isSubmitting) {
                Text("关闭")
            }
            WorkshopButton(
                onClick = onSubmit,
                enabled = !state.isSubmitting && !state.isPollingConfirmation,
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                }
                Text(
                    when {
                        isTokenMode -> if (state.mode == SteamLoginDialogMode.Reauthenticate) {
                            "导入令牌"
                        } else {
                            "令牌登录"
                        }

                        state.challengeType == SteamGuardChallengeType.EmailCode ||
                            state.challengeType == SteamGuardChallengeType.DeviceCode ->
                            "提交验证码"

                        isConfirmationChallenge -> "继续等待"

                        else -> if (state.mode == SteamLoginDialogMode.Reauthenticate) {
                            "重新认证"
                        } else {
                            "登录"
                        }
                    },
                )
            }
        },
    ) {
        when {
            isTokenMode -> {
                Text(
                    if (state.mode == SteamLoginDialogMode.Reauthenticate) {
                        "可直接粘贴新的 Steam Refresh Token 完成重新认证。"
                    } else {
                        "如果你已经拿到 Steam Refresh Token，也可以直接导入，不必继续等待手机确认。"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WorkshopOutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    label = {
                        Text(
                            if (state.mode == SteamLoginDialogMode.Reauthenticate) {
                                "账号显示名"
                            } else {
                                "账号显示名（可选）"
                            },
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state.mode != SteamLoginDialogMode.Reauthenticate,
                )
                WorkshopOutlinedTextField(
                    value = state.refreshToken,
                    onValueChange = onRefreshTokenChange,
                    label = { Text("Refresh Token") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            }

            state.challengeType == SteamGuardChallengeType.EmailCode ||
                state.challengeType == SteamGuardChallengeType.DeviceCode -> {
                Text(
                    state.challengeMessage ?: "请输入 Steam Guard 验证码。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WorkshopOutlinedTextField(
                    value = state.guardCode,
                    onValueChange = onGuardCodeChange,
                    label = { Text("验证码") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }

            isConfirmationChallenge -> {
                Text(
                    state.challengeMessage ?: "请在 Steam 手机 App 中完成确认，应用会自动继续等待结果。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state.isPollingConfirmation) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                        Text(
                            "正在等待 Steam 确认…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            else -> {
                WorkshopOutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    label = { Text("账号名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state.mode != SteamLoginDialogMode.Reauthenticate,
                )
                WorkshopOutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text("密码") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }

        if (state.canSwitchSteamLoginInputMode()) {
            WorkshopTextButton(
                onClick = {
                    onSwitchInputMode(
                        if (isTokenMode) {
                            SteamLoginInputMode.Credentials
                        } else {
                            SteamLoginInputMode.RefreshToken
                        },
                    )
                },
            ) {
                Text(
                    if (isTokenMode) {
                        if (state.mode == SteamLoginDialogMode.Reauthenticate) {
                            "改用密码重新认证"
                        } else {
                            "改用账号密码登录"
                        }
                    } else {
                        "改为输入令牌登录"
                    },
                )
            }
        }

        state.errorMessage?.takeIf(String::isNotBlank)?.let { message ->
            WorkshopMessageBanner(
                message = message,
                tone = MessageTone.Error,
            )
        }
        Text(
            text = "登录过程摘要会写入运行Logs，可在设置页Logs区域导出Logs包。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun sourceDescription(source: UpdateSource): String =
    when (source) {
        UpdateSource.GH_PROXY_COM -> "默认优先源，适合下载 GitHub 附件。"
        UpdateSource.GH_PROXY_VIP -> "支持元数据和下载代理，适合作为备用源。"
        UpdateSource.GH_LLKK -> "支持元数据和下载代理，可作为另一条回退线路。"
        UpdateSource.GH_PROXY_NET -> "自动回退源，不在设置里手动选择。"
        UpdateSource.OFFICIAL -> "官方 GitHub 直连地址。"
    }

private fun steamLanguagePreferenceDescription(
    preference: SteamLanguagePreference,
): String =
    when (preference) {
        SteamLanguagePreference.SimplifiedChinese -> "添加游戏和工坊浏览会优先按中文界面与中文偏好请求。"
        SteamLanguagePreference.English -> "添加游戏和工坊浏览会优先按英文界面与英文偏好请求。"
    }

private const val repositoryUrl = "https://github.com/Apricityx/WorkshopAndroidDownloader"
private const val primaryDeveloperUrl = "https://github.com/Apricityx"
private const val secondaryDeveloperUrl = "https://github.com/ZJustin117"
private const val slayTheAmethystModdedUrl =
    "https://github.com/ModinMobileSTS/SlayTheAmethystModded"
