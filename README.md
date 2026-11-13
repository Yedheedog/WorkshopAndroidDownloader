<p align="center">
  <img src="./app/src/main/res/mipmap-nodpi/ic_launcher.png" alt="WorkshopOnAndroid app icon" width="96" height="96" />
</p>

<h1 align="center">WorkshopOnAndroid</h1>

<p align="center">
  An Android app for browsing and downloading Steam Workshop content.
</p>

<p align="center">
  Browse games that support Steam Workshop, inspect item details, and download public items anonymously or use a signed-in Steam account for content that requires authentication.
</p>

<p align="center">
  <img alt="Android API 31+" src="https://img.shields.io/badge/Android-API%2031%2B-34A853?style=flat-square&logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Android-7f52ff?style=flat-square&logo=kotlin" />
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose" />
  <img alt="Steam Workshop" src="https://img.shields.io/badge/Steam%20Workshop-Login%20Aware-1b2838?style=flat-square&logo=steam" />
</p>

<p align="center">
  <a href="#highlights">Highlights</a> •
  <a href="#quick-start">Quick Start</a> •
  <a href="#building-from-source">Build</a> •
  <a href="#release-automation">Release Automation</a> •
  <a href="#adb-debug-automation">ADB Automation</a> •
  <a href="#repository-layout">Repository Layout</a> •
  <a href="#limitations">Limitations</a> •
  <a href="#license">License</a>
</p>

> [!IMPORTANT]
> You can sign in to Steam from Settings using the Steam protocol. The same account is used for Workshop browsing and downloads. Public items can still be downloaded without signing in. Tasks that require authentication are bound to the account selected when they are queued, and use that account's Steam cookies / CM session during download. Collection downloads and QR-code login are not currently supported.

## Highlights

| Area | Description |
| --- | --- |
| Game discovery | Choose from featured Workshop games, search Steam games, or add a game by entering its `AppID`. |
| Workshop browsing | Browse Steam Community Workshop listings, search, paginate, and view item details. |
| Debug automation | Run Gradle tasks backed by `adb` to trigger downloads without navigating the UI and collect diagnostic logs. |

> [!NOTE]
> `:steam-protocol` handles Steam CM connections, account authentication, content-server discovery, and CDN authorization. `:workshop-core` handles manifests, chunks, verification, extraction, and final file assembly.

## Quick Start

### Requirements

- JDK 17 or newer (the project currently builds with JDK 21)
- Android Studio and Android SDK
- Android 12 (API 31) or newer
- Network access to Steam Store, Steam Community, and Steam Web API
- For device automation: working `adb` and a connected Android device

### Build a Debug APK

```powershell
.\gradlew.bat :app:assembleDebug
```

### Install on a device

```powershell
.\gradlew.bat :app:installDebug
```

### Use the app

1. Open the app and go to the game library.
2. Tap the `+` button in the top-right corner to open Add Game.
3. Add a Workshop-enabled game from featured games, search results, or by entering its `AppID`.
4. Open that game's Workshop page, search for items, and inspect their details.
5. Tap **Download** on an item card to add it to the Download Center.
6. Track task status, progress, and logs in the Download Center. Pause, resume, or remove tasks there; manage completed files in the Mod Library.

### Download locations

- Private app cache: `/data/user/0/top.apricityx.workshop/files/workshop/<AppID>/<PublishedFileId>/`
- User-accessible export directory: `/sdcard/Download/workshop/<Game Name>/<Item Name>/<Version>/`

For single-file items, readable titles or filenames from Steam metadata are preferred when naming exported files. Re-downloading the same version replaces that version's directory; different versions are kept in their own directories.

## Building from Source

| Module | Responsibility |
| --- | --- |
| `:app` | Compose UI, navigation, game library, local settings, Download Center, and file export |
| `:workshop-core` | Workshop metadata parsing, `file_url` downloads, UGC manifest/chunk downloads, verification, and file assembly |
| `:steam-protocol` | Steam CM WebSocket connections, account authentication, directory services, content-server requests, manifest request codes, CDN tokens, and depot-key retrieval |

Build targets:

- `minSdk 31`
- `targetSdk 36`
- `compileSdk 36`
- Java / Kotlin bytecode target: `17`

Runtime settings:

- Download worker count: `1..8`
- Maximum concurrent download tasks: `1..3`
- Theme: follow system, light, or dark
- Multiple Steam accounts can be saved in Settings. You can switch the active browsing account; each download task remains bound to the account selected when it was queued.

## Release Automation

The repository includes a tag-based release workflow:

- `.github/workflows/release.yml` builds a signed release APK when a `v*` tag is pushed.
- Build artifacts are uploaded to GitHub Actions.
- When the ref is a tag, the workflow creates or updates a GitHub Release.
- If `docs/release/note/v<version>.md` exists, it is used as the Release description.

Signing credentials are supplied through the GitHub Environment `release-signing`. See [docs/release-automation/README.md](docs/release-automation/README.md) for setup instructions.

To prepare a release note, create a tag, and push it, run:

```powershell
scripts\prepare-release.bat
```

## ADB Debug Automation

The repository includes `adb` automation tasks for device debugging and regression checks. They can test a download using an `AppID + PublishedFileId` without navigating the UI.

`workshopDownloadAndPullLogs` automatically:

- Builds and installs the latest Debug APK.
- Force-stops the app and clears the target item's previous cache.
- Sends `top.apricityx.workshop.action.DOWNLOAD` through `adb`.
- Waits for success, failure, or timeout.
- Retrieves `download.log`, `logcat.txt`, `metadata.json`, and `result.txt`.

Common commands:

```powershell
.\gradlew.bat :app:workshopStartDownload -PworkshopAppId=646570 -PpublishedFileId=3677098410 -PdeviceSerial=<adb-serial>
.\gradlew.bat :app:workshopPullLogs -PworkshopAppId=646570 -PpublishedFileId=3677098410 -PdeviceSerial=<adb-serial>
.\gradlew.bat :app:workshopDownloadAndPullLogs -PworkshopAppId=646570 -PpublishedFileId=3677098410 -PdeviceSerial=<adb-serial> -PdownloadTimeoutSeconds=180 --console=plain
```

Optional parameters:

- `-PdeviceSerial=<adb-serial>`
- `-PdownloadTimeoutSeconds=<seconds>`
- `-PpollIntervalMillis=<milliseconds>`
- `-PlogsDir=<path>`

Default log output directory:

`build/workshop-adb/app-<AppID>-file-<PublishedFileId>-<timestamp>/`

## Repository Layout

| Path | Purpose |
| --- | --- |
| `app/` | Android application, Compose screens, Download Center, and public-directory export |
| `workshop-core/` | Download engine, metadata parsing, chunk processing, verification, and assembly |
| `steam-protocol/` | Steam protocol access, WebSocket sessions, and content-server client |
| `gradle/workshop-adb.gradle.kts` | ADB debugging automation tasks |

## Limitations

- Public items can be downloaded anonymously. When signed in, tasks use the Steam account bound at queue time for authenticated metadata, Steam CM, and CDN requests. Actual access still depends on the account's visibility rights and Steam's upstream authorization.
- Steam Workshop Collections are not currently supported.
- Workshop listings and item details depend on Steam page structures and related APIs; upstream changes may require parser updates.
- If the network cannot reliably reach Steam, game discovery, Workshop browsing, and downloads may fail or time out.

### Built-in acceleration and connection recovery

- Separate routes are used for Steam Community, Store/API, and images. A failure on one route does not disable the others. Failed routes back off briefly (30 seconds) and are retried automatically on later requests.
- If acceleration configuration is temporarily unavailable, built-in fallback routes are used. The Community fallback stays aligned with the current Watt configuration; obsolete route caches are invalidated automatically.
- Steam CM sessions required for downloads do not pass through the site proxy routes above. The app discovers candidate nodes from Steam, interleaves data centers, and connects concurrently with staggered starts to avoid serial delays on unreachable nodes. At least one candidate must be reachable from the current network.
- To troubleshoot, export a log bundle from Settings and inspect CM candidate addresses, connection errors, and timings. Built-in acceleration cannot guarantee access on every network; if failures persist, try another network or an external accelerator.

## License

This project is licensed under the [Apache License 2.0](LICENSE).
