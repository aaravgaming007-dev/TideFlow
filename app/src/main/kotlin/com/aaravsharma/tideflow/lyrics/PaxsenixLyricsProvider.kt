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

package com.aaravsharma.tideflow.lyrics

import android.content.Context
import com.aaravsharma.tideflow.constants.EnablePaxsenixLyricsKey
import com.aaravsharma.tideflow.paxsenix.PaxsenixLyrics
import com.aaravsharma.tideflow.utils.dataStore
import com.aaravsharma.tideflow.utils.get

object PaxsenixLyricsProvider : LyricsProvider {
    override val name = "Paxsenix (Auto)"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnablePaxsenixLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> = PaxsenixLyrics.getLyrics(title, artist, duration)

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        PaxsenixLyrics.getAllLyrics(title, artist, duration, callback)
    }
}
