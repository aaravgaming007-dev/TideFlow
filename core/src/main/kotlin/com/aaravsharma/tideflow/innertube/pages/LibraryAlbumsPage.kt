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

package com.aaravsharma.tideflow.innertube.pages

import com.aaravsharma.tideflow.innertube.models.Album
import com.aaravsharma.tideflow.innertube.models.AlbumItem
import com.aaravsharma.tideflow.innertube.models.Artist
import com.aaravsharma.tideflow.innertube.models.ArtistItem
import com.aaravsharma.tideflow.innertube.models.MusicResponsiveListItemRenderer
import com.aaravsharma.tideflow.innertube.models.MusicTwoRowItemRenderer
import com.aaravsharma.tideflow.innertube.models.PlaylistItem
import com.aaravsharma.tideflow.innertube.models.SongItem
import com.aaravsharma.tideflow.innertube.models.YTItem
import com.aaravsharma.tideflow.innertube.models.oddElements
import com.aaravsharma.tideflow.innertube.utils.parseTime

data class LibraryAlbumsPage(
    val albums: List<AlbumItem>,
    val continuation: String?,
) {
    companion object {
        fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): AlbumItem? {
            val browseId = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null
            val playlistId = renderer.thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content
                ?.musicPlayButtonRenderer?.playNavigationEndpoint
                ?.watchPlaylistEndpoint?.playlistId
                ?: renderer.menu?.menuRenderer?.items?.firstOrNull()
                    ?.menuNavigationItemRenderer?.navigationEndpoint
                    ?.watchPlaylistEndpoint?.playlistId
                ?: browseId.removePrefix("MPREb_").let { "OLAK5uy_$it" }

            return AlbumItem(
                browseId = browseId,
                playlistId = playlistId,
                title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                artists = null,
                year = renderer.subtitle?.runs?.lastOrNull()?.text?.toIntOrNull(),
                thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                explicit = renderer.subtitleBadges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null
            )
        }
    }
}
