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
 
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaravsharma.tideflow.db.MusicDatabase
import com.aaravsharma.tideflow.utils.reportException
import com.aaravsharma.tideflow.innertube.YouTube
import com.aaravsharma.tideflow.innertube.models.AlbumItem
import com.aaravsharma.tideflow.innertube.models.PlaylistItem
import com.aaravsharma.tideflow.innertube.models.YTItem
import com.aaravsharma.tideflow.innertube.utils.completed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
 
@HiltViewModel
class BrowseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val browseId: String? = savedStateHandle.get<String>("browseId")
 
    val items = MutableStateFlow<List<YTItem>?>(emptyList())
    val title = MutableStateFlow<String?>("")
 
    init {
        viewModelScope.launch {
            browseId?.let {
                YouTube.browse(browseId, null).onSuccess { result ->
                    // Store the title
                    title.value = result.title
 
                    // Flatten the nested structure to get all YTItems
                    val allItems = result.items.flatMap { it.items }
                    items.value = allItems
                }.onFailure {
                    reportException(it)
                }
            }
        }
    }
}
