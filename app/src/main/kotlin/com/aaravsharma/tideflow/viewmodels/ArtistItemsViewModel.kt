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

package com.aaravsharma.tideflow.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaravsharma.tideflow.innertube.YouTube
import com.aaravsharma.tideflow.innertube.models.BrowseEndpoint
import com.aaravsharma.tideflow.innertube.models.filterExplicit
import com.aaravsharma.tideflow.innertube.pages.ArtistItemsPageLayout
import com.aaravsharma.tideflow.constants.HideExplicitKey
import com.aaravsharma.tideflow.models.ItemsPage
import com.aaravsharma.tideflow.utils.dataStore
import com.aaravsharma.tideflow.utils.get
import com.aaravsharma.tideflow.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistItemsViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val browseId = savedStateHandle.get<String>("browseId")!!
    private val params = savedStateHandle.get<String>("params")
        ?.takeUnless { it.isBlank() || it == "null" }

    val title = MutableStateFlow("")
    val itemsPage = MutableStateFlow<ItemsPage?>(null)
    val itemsLayout = MutableStateFlow(ArtistItemsPageLayout.LIST)

    init {
        viewModelScope.launch {
            YouTube
                .artistItems(
                    BrowseEndpoint(
                        browseId = browseId,
                        params = params,
                    ),
                ).onSuccess { artistItemsPage ->
                    title.value = artistItemsPage.title
                    itemsLayout.value = artistItemsPage.layout
                    itemsPage.value =
                        ItemsPage(
                            items = artistItemsPage.items
                                .distinctBy { it.id }
                                .filterExplicit(context.dataStore.get(HideExplicitKey, false)),
                            continuation = artistItemsPage.continuation,
                        )
                }.onFailure {
                    reportException(it)
                }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            val oldItemsPage = itemsPage.value ?: return@launch
            val continuation = oldItemsPage.continuation ?: return@launch
            YouTube
                .artistItemsContinuation(continuation)
                .onSuccess { artistItemsContinuationPage ->
                    itemsPage.update {
                        ItemsPage(
                            items =
                            (oldItemsPage.items + artistItemsContinuationPage.items)
                                .distinctBy { it.id }
                                .filterExplicit(context.dataStore.get(HideExplicitKey, false)),
                            continuation = artistItemsContinuationPage.continuation,
                        )
                    }
                }.onFailure {
                    reportException(it)
                }
        }
    }
}
