/*
 * NomaTune (2026)
 * © Shahdullah — github.com/shahdullah
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 *
 * Based on ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.aaravsharma.tideflow.ui.screens

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaravsharma.tideflow.BuildConfig
import com.aaravsharma.tideflow.R
import com.aaravsharma.tideflow.constants.DarkModeKey
import com.aaravsharma.tideflow.constants.UpdateChannel
import com.aaravsharma.tideflow.constants.PureBlackKey
import com.aaravsharma.tideflow.ui.component.BottomSheet
import com.aaravsharma.tideflow.ui.component.BottomSheetMenu
import com.aaravsharma.tideflow.ui.component.LocalMenuState
import com.aaravsharma.tideflow.ui.component.rememberBottomSheetState
import com.aaravsharma.tideflow.ui.screens.BrowseScreen
import com.aaravsharma.tideflow.ui.screens.artist.ArtistAlbumsScreen
import com.aaravsharma.tideflow.ui.screens.artist.ArtistItemsScreen
import com.aaravsharma.tideflow.ui.screens.artist.ArtistScreen
import com.aaravsharma.tideflow.ui.screens.artist.ArtistSongsScreen
import com.aaravsharma.tideflow.ui.screens.library.LocalSongScreen
import com.aaravsharma.tideflow.ui.screens.library.LibraryScreen
import com.aaravsharma.tideflow.ui.screens.playlist.AutoPlaylistScreen
import com.aaravsharma.tideflow.ui.screens.playlist.LocalPlaylistScreen
import com.aaravsharma.tideflow.ui.screens.playlist.OnlinePlaylistScreen
import com.aaravsharma.tideflow.ui.screens.playlist.SpotifyPlaylistScreen
import com.aaravsharma.tideflow.ui.screens.playlist.TopPlaylistScreen
import com.aaravsharma.tideflow.ui.screens.playlist.CachePlaylistScreen
import com.aaravsharma.tideflow.ui.screens.search.OnlineSearchResultArgument
import com.aaravsharma.tideflow.ui.screens.search.OnlineSearchResultRoute
import com.aaravsharma.tideflow.ui.screens.search.OnlineSearchResultRoutePrefix
import com.aaravsharma.tideflow.ui.screens.search.OnlineSearchResult
import com.aaravsharma.tideflow.ui.screens.settings.AboutScreen
import com.aaravsharma.tideflow.ui.screens.settings.AccountSettings
import com.aaravsharma.tideflow.ui.screens.settings.AiIntegrationSettings
import com.aaravsharma.tideflow.ui.screens.settings.AodCustomizedScreen
import com.aaravsharma.tideflow.ui.screens.settings.AppearanceSettings
import com.aaravsharma.tideflow.ui.screens.settings.CustomizeBackground
import com.aaravsharma.tideflow.ui.screens.settings.BackupAndRestore
import com.aaravsharma.tideflow.ui.screens.settings.ChangelogScreen
import com.aaravsharma.tideflow.ui.screens.settings.ContentSettings
import com.aaravsharma.tideflow.ui.screens.settings.DarkMode
import com.aaravsharma.tideflow.ui.screens.settings.DiscordSettings
import com.aaravsharma.tideflow.ui.screens.settings.DebugSettings
import com.aaravsharma.tideflow.ui.screens.settings.IntegrationScreen
import com.aaravsharma.tideflow.ui.screens.settings.LastFMSettings
import com.aaravsharma.tideflow.ui.screens.settings.LyricsAnimationSettings
import com.aaravsharma.tideflow.ui.screens.settings.LyricsSettings
import com.aaravsharma.tideflow.ui.screens.settings.MusicTogetherScreen
import com.aaravsharma.tideflow.ui.screens.settings.PalettePickerScreen
import com.aaravsharma.tideflow.ui.screens.settings.PlayerSettings
import com.aaravsharma.tideflow.ui.screens.settings.PoTokenScreen
import com.aaravsharma.tideflow.ui.screens.settings.PrivacySettings
import com.aaravsharma.tideflow.ui.screens.settings.InternetSettings
import com.aaravsharma.tideflow.ui.screens.settings.SettingsScreen
import com.aaravsharma.tideflow.ui.screens.settings.StorageSettings
import com.aaravsharma.tideflow.ui.screens.settings.ThemeCreatorScreen
import com.aaravsharma.tideflow.ui.screens.settings.UpdateScreen
import com.aaravsharma.tideflow.musicrecognition.MusicRecognitionRoute
import com.aaravsharma.tideflow.ui.screens.musicrecognition.MusicRecognitionScreen
import com.aaravsharma.tideflow.ui.utils.ShowMediaInfo
import com.aaravsharma.tideflow.utils.rememberEnumPreference
import com.aaravsharma.tideflow.ui.screens.onboarding.OnboardingRoute
import com.aaravsharma.tideflow.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    latestVersionName: () -> String,
    disableAnimations: Boolean = false,
    onClearUpdateBadge: () -> Unit = {},
) {
    composable(Screens.Home.route) {
        HomeScreen(navController)
    }
    composable(
        Screens.Library.route,
    ) {
        LibraryScreen(navController)
    }
    composable("local_songs") {
        LocalSongScreen(navController)
    }
    composable("history") {
        HistoryScreen(navController)
    }
    composable("stats") {
        StatsScreen(navController)
    }
    composable("news") {
        NewsScreen(navController)
    }
    composable(
        route = "view_news/{newsId}",
        arguments = listOf(
            navArgument("newsId") { type = NavType.StringType },
        ),
    ) {
        ViewNewsScreen(navController)
    }
    composable(
        route = "year_in_music?year={year}",
        arguments = listOf(
            navArgument("year") {
                type = NavType.IntType
                defaultValue = -1
            }
        ),
    ) { backStackEntry ->
        val selectedYear = backStackEntry.arguments?.getInt("year")?.takeIf { it > 0 }
        YearInMusicScreen(
            navController = navController,
            initialYear = selectedYear,
        )
    }
    composable(MusicRecognitionRoute) {
        MusicRecognitionScreen(navController)
    }
    composable(Screens.MoodAndGenres.route) {
        MoodAndGenresScreen(navController)
    }
    composable("account") {
        AccountScreen(navController, scrollBehavior)
    }
    composable("new_release") {
        NewReleaseScreen(navController, scrollBehavior)
    }
    composable("charts_screen") {
       ChartsScreen(navController)
    }
    composable(
        route = "browse/{browseId}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
            }
        )
    ) {
        BrowseScreen(
            navController,
            scrollBehavior,
            it.arguments?.getString("browseId")
        )
    }
    composable(
        route = OnlineSearchResultRoute,
        arguments =
        listOf(
            navArgument(OnlineSearchResultArgument) {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            if (disableAnimations) {
                fadeIn(tween(0))
            } else {
                fadeIn(tween(250))
            }
        },
        exitTransition = {
            if (disableAnimations) {
                fadeOut(tween(0))
            } else if (targetState.destination.route?.startsWith(OnlineSearchResultRoutePrefix) == true) {
                fadeOut(tween(200))
            } else {
                fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
            }
        },
        popEnterTransition = {
            if (disableAnimations) {
                fadeIn(tween(0))
            } else if (initialState.destination.route?.startsWith(OnlineSearchResultRoutePrefix) == true) {
                fadeIn(tween(250))
            } else {
                fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
            }
        },
        popExitTransition = {
            if (disableAnimations) {
                fadeOut(tween(0))
            } else {
                fadeOut(tween(200))
            }
        },
    ) {
        OnlineSearchResult(navController)
    }
    composable(
        route = "album/{albumId}",
        arguments =
        listOf(
            navArgument("albumId") {
                type = NavType.StringType
            },
        ),
    ) {
        AlbumScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}",
        arguments =
        listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}/songs",
        arguments =
        listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistSongsScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}/albums",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            }
        )
    ) {
        ArtistAlbumsScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}/items?browseId={browseId}&params={params}",
        arguments =
        listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        ArtistItemsScreen(navController, scrollBehavior)
    }
    composable(
        route = "online_playlist/{playlistId}",
        arguments =
        listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "local_playlist/{playlistId}",
        arguments =
        listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        LocalPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "spotify_playlist/{playlistId}",
        arguments =
        listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        SpotifyPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "auto_playlist/{playlist}",
        arguments =
        listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "cache_playlist/{playlist}",
        arguments =
            listOf(
                navArgument("playlist") {
                    type = NavType.StringType
            },
        ),
    ) {
        CachePlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "top_playlist/{top}",
        arguments =
        listOf(
            navArgument("top") {
                type = NavType.StringType
            },
        ),
    ) {
        TopPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "youtube_browse/{browseId}?params={params}",
        arguments =
        listOf(
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        YouTubeBrowseScreen(navController)
    }
    composable("settings") {
        SettingsScreen(navController, scrollBehavior, latestVersionName())
    }
    composable("settings/account") {
        AccountSettings(navController, scrollBehavior, latestVersionName())
    }
    composable("settings/appearance") {
        AppearanceSettings(navController, scrollBehavior)
    }
    composable("settings/appearance/aod_customized") {
        AodCustomizedScreen(navController, scrollBehavior)
    }
    composable("settings/appearance/palette_picker") {
        PalettePickerScreen(navController)
    }
    composable("settings/appearance/lyrics_animations") {
        LyricsAnimationSettings(navController, scrollBehavior)
    }
    composable("settings/appearance/theme_creator") {
        ThemeCreatorScreen(navController)
    }
    composable("settings/content") {
        ContentSettings(navController)
    }
    composable("settings/lyrics") {
        LyricsSettings(navController)
    }
    composable("settings/internet") {
        InternetSettings(navController, scrollBehavior)
    }
    composable("settings/player") {
        PlayerSettings(navController, scrollBehavior)
    }
    composable("settings/storage") {
        StorageSettings(navController, scrollBehavior)
    }
    composable("settings/privacy") {
        PrivacySettings(navController, scrollBehavior)
    }
    composable("settings/backup_restore") {
        BackupAndRestore(navController, scrollBehavior)
    }
    composable("settings/discord") {
        DiscordSettings(navController, scrollBehavior)
    }
    composable("settings/integration") {
        IntegrationScreen(navController, scrollBehavior)
    }
    composable("settings/ai_integration") {
        AiIntegrationSettings(navController)
    }
    composable("settings/music_together") {
        MusicTogetherScreen(navController, scrollBehavior)
    }
    composable("settings/lastfm") {
        LastFMSettings(navController, scrollBehavior)
    }
    composable("settings/discord/experimental") {
        com.aaravsharma.tideflow.ui.screens.settings.DiscordExperimental(navController)
    }
    composable("settings/misc") {
        DebugSettings(navController)
    }
    if (BuildConfig.UPDATER_AVAILABLE) {
        composable("settings/update") {
            UpdateScreen(navController, scrollBehavior, onUpToDate = onClearUpdateBadge)
        }
    }
    composable(
        route = "settings/changelog?channel={channel}",
        arguments = listOf(
            navArgument("channel") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val channelName = backStackEntry.arguments?.getString("channel")
        val channel = channelName?.let {
            runCatching { UpdateChannel.valueOf(it) }.getOrNull()
        } ?: UpdateChannel.STABLE
        ChangelogScreen(navController, scrollBehavior, channel = channel)
    }
    composable("settings/about") {
        AboutScreen(navController, scrollBehavior)
    }
    composable("settings/po_token") {
        PoTokenScreen(navController, scrollBehavior)
    }
    composable("customize_background") {
        CustomizeBackground(navController)
    }
    composable(
        route = "$LOGIN_ROUTE?$LOGIN_URL_ARGUMENT={$LOGIN_URL_ARGUMENT}",
        arguments = listOf(
            navArgument(LOGIN_URL_ARGUMENT) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        LoginScreen(
            navController,
            startUrl = backStackEntry.arguments?.getString(LOGIN_URL_ARGUMENT)?.let(Uri::decode)
        )
    }
    composable("welcome") {
        OnboardingRoute(
            onNavigateToHome = {
                navController.navigate(Screens.Home.route) {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        )
    }
}
