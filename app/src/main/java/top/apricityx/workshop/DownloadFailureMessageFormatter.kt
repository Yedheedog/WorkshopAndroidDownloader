package top.apricityx.workshop

private const val STEAM_CDN_UNAUTHORIZED_MESSAGE = "Steam CDN request failed: 401"

internal fun formatDownloadFailureMessage(
    rawMessage: String,
    gameTitle: String,
    hasBoundAccount: Boolean,
    ownershipStatus: SteamAppOwnershipStatus,
): String {
    val normalizedMessage = rawMessage.trim().ifBlank { "Download failed." }
    if (!normalizedMessage.contains(STEAM_CDN_UNAUTHORIZED_MESSAGE)) {
        return normalizedMessage
    }

    if (!hasBoundAccount) {
        return "Steam CDN returned 401. This item may require signing in with a Steam account that owns the game."
    }

    return when (ownershipStatus) {
        SteamAppOwnershipStatus.NotOwned -> {
            val resolvedGameTitle = gameTitle.ifBlank { "the corresponding game" }
            "Steam CDN returned 401. The linked account does not appear to own “$resolvedGameTitle”. This item may require an account that owns the game."
        }

        SteamAppOwnershipStatus.Owned -> normalizedMessage
        SteamAppOwnershipStatus.Unknown -> "Steam CDN returned 401. This item may require signing in with a Steam account that owns the game."
    }
}
