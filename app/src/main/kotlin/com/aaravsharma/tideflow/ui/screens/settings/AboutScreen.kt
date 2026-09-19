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

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.aaravsharma.tideflow.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.aaravsharma.tideflow.BuildConfig
import com.aaravsharma.tideflow.R
import com.aaravsharma.tideflow.ui.component.IconButton
import com.aaravsharma.tideflow.utils.Updater
import com.aaravsharma.tideflow.ui.utils.backToMain
import com.aaravsharma.tideflow.LocalPlayerAwareWindowInsets
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsetsSides
import com.aaravsharma.tideflow.currentBuildHash
import com.aaravsharma.tideflow.utils.openSafeUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.ui.layout.ContentScale

data class TeamMember(
    val avatarModel: Any,
    val name: String,
    val position: String,
    val profileUrl: String? = null,
    val github: String? = null,
    val website: String? = null,
    val discord: String? = null,
    val instagram: String? = null,
)

@Composable
private fun OutlinedIconChip(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp),
        )
    }
}

@Composable
fun OutlinedIconChipMembers(
    iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.size(32.dp),
        shapes = ButtonDefaults.shapes(),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AboutBadge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
            )
            .padding(
                horizontal = 6.dp,
                vertical = 2.dp,
            ),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val nightlyBuildHash = currentBuildHash
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var totalDownloads by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/aaravgaming007-dev/TideFlow/releases")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    setRequestProperty("Accept", "application/vnd.github+json")
                    setRequestProperty("User-Agent", "TideFlow")
                    connectTimeout = 8000
                    readTimeout = 8000
                }
                if (connection.responseCode in 200..299) {
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(body)
                    var count = 0
                    for (i in 0 until jsonArray.length()) {
                        val release = jsonArray.optJSONObject(i)
                        val assets = release?.optJSONArray("assets")
                        if (assets != null) {
                            for (j in 0 until assets.length()) {
                                count += assets.optJSONObject(j)?.optInt("download_count", 0) ?: 0
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        totalDownloads = count
                    }
                }
            } catch (_: Exception) {}
        }
    }

    val leadDeveloper = TeamMember(
        avatarModel = R.drawable.aarav_avatar,
        name = "AARAV SHARMA",
        position = stringResource(R.string.about_position_lead_dev),
        profileUrl = "https://github.com/aaravgaming007-dev",
        github = "https://github.com/aaravgaming007-dev",
        website = "https://aaravsharma.pages.dev",
        instagram = "https://instagram.com/aarav_sharma_sui"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(innerPadding)
                .windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    .heightIn(max = 16.dp)
            )

            Image(
                painter = painterResource(R.drawable.about_splash_img),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable { },
            )

            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "TideFlow",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                AboutBadge(text = "v${BuildConfig.VERSION_NAME.removePrefix("v")}")

                nightlyBuildHash?.let {
                    Spacer(Modifier.width(4.dp))
                    AboutBadge(text = it)
                }

                Spacer(Modifier.width(4.dp))

                if (BuildConfig.DEBUG) {
                    AboutBadge(text = "DEBUG")
                } else {
                    AboutBadge(text = BuildConfig.ARCHITECTURE.uppercase())
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.download),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Total Downloads",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = totalDownloads?.let { "$it+" } ?: "144+",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row {
                IconButton(
                    onClick = { openSafeUri(context, "https://github.com/aaravgaming007-dev/TideFlow/") },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = stringResource(R.string.about_content_desc_github)
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { openSafeUri(context, "https://aaravsharma.pages.dev") },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.website),
                        contentDescription = stringResource(R.string.about_content_desc_website)
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { openSafeUri(context, "https://instagram.com/aarav_sharma_sui") },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.instagram),
                        contentDescription = "Instagram"
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { openSafeUri(context, "upi://pay?pa=ghanshyamsharma.nlu@okicici&pn=AARAV%20SHARMA&cu=INR") },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.coffee),
                        contentDescription = stringResource(R.string.about_content_desc_donate)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = {
                        openSafeUri(context, "upi://pay?pa=ghanshyamsharma.nlu@okicici&pn=AARAV%20SHARMA&cu=INR")
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.coffee),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Donate (UPI)")
                }

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            isCheckingUpdate = true
                            try {
                                withContext(Dispatchers.IO) {
                                    val url = URL("https://api.github.com/repos/aaravgaming007-dev/TideFlow/releases/latest")
                                    val connection = (url.openConnection() as HttpURLConnection).apply {
                                        setRequestProperty("Accept", "application/vnd.github+json")
                                        setRequestProperty("User-Agent", "TideFlow")
                                        connectTimeout = 8000
                                        readTimeout = 8000
                                    }
                                    if (connection.responseCode in 200..299) {
                                        val body = connection.inputStream.bufferedReader().use { it.readText() }
                                        val json = JSONObject(body)
                                        val tagName = json.optString("tag_name", "")
                                        val htmlUrl = json.optString("html_url", "")
                                        val latestVersion = tagName.removePrefix("v").removePrefix("V")
                                        withContext(Dispatchers.Main) {
                                            if (latestVersion.isNotBlank() && Updater.isUpdateAvailable(latestVersion, BuildConfig.VERSION_NAME) && htmlUrl.isNotBlank()) {
                                                openSafeUri(context, htmlUrl)
                                            } else {
                                                Toast.makeText(context, "You're up to date!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Failed to check for updates", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } catch (_: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Failed to check for updates", Toast.LENGTH_SHORT).show()
                                }
                            } finally {
                                isCheckingUpdate = false
                            }
                        }
                    },
                    enabled = !isCheckingUpdate,
                ) {
                    if (isCheckingUpdate) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text("Check for Updates")
                }
            }

            Spacer(Modifier.height(16.dp))

            SectionHeader(
                title = stringResource(R.string.about_lead_developer),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            LeadDeveloperCard(
                member = leadDeveloper,
                onOpenUri = { url -> openSafeUri(context, url) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun LeadDeveloperCard(
    member: TeamMember,
    onOpenUri: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (member.avatarModel is Int) {
                Image(
                    painter = painterResource(member.avatarModel),
                    contentDescription = member.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            } else {
                AsyncImage(
                    model = member.avatarModel,
                    contentDescription = member.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = member.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = member.position,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                member.github?.let { url ->
                    OutlinedIconChip(
                        iconRes = R.drawable.github,
                        contentDescription = stringResource(R.string.about_content_desc_github),
                        onClick = { onOpenUri(url) },
                    )
                }

                member.website?.takeIf { it.isNotBlank() }?.let { url ->
                    OutlinedIconChip(
                        iconRes = R.drawable.website,
                        contentDescription = stringResource(R.string.about_content_desc_website),
                        onClick = { onOpenUri(url) },
                    )
                }

                member.instagram?.let { url ->
                    OutlinedIconChip(
                        iconRes = R.drawable.instagram,
                        contentDescription = "Instagram",
                        onClick = { onOpenUri(url) },
                    )
                }

                member.discord?.let { url ->
                    OutlinedIconChip(
                        iconRes = R.drawable.alternate_email,
                        contentDescription = stringResource(R.string.about_content_desc_discord),
                        onClick = { onOpenUri(url) },
                    )
                }
            }
        }
    }
}
