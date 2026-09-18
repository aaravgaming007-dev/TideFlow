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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.aaravsharma.tideflow.network.NetworkBannerUiState
import com.aaravsharma.tideflow.network.ObserveNetworkBannerStateUseCase

@HiltViewModel
class NetworkBannerViewModel
@Inject
constructor(
    observeNetworkBannerStateUseCase: ObserveNetworkBannerStateUseCase,
) : ViewModel() {
    val bannerState: StateFlow<NetworkBannerUiState> =
        observeNetworkBannerStateUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NetworkBannerUiState.Hidden,
            )
}
