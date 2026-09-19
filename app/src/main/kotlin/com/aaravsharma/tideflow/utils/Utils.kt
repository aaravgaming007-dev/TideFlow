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

package com.aaravsharma.tideflow.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun reportException(throwable: Throwable) {
    throwable.printStackTrace()
}

@Suppress("DEPRECATION")
fun setAppLocale(context: Context, locale: Locale) {
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun openSafeUri(context: Context, uri: String) {
    try {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri)).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        if (uri.startsWith("upi://")) {
            val upiId = "ghanshyamsharma.nlu@okicici"
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("UPI ID", upiId)
            clipboard?.setPrimaryClip(clip)
            android.widget.Toast.makeText(
                context,
                "No UPI app found. UPI ID copied: $upiId",
                android.widget.Toast.LENGTH_LONG
            ).show()
        } else {
            android.widget.Toast.makeText(
                context,
                "Unable to open link",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}