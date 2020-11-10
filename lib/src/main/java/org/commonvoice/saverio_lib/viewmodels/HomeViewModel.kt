package org.commonvoice.saverio_lib.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.commonvoice.saverio_lib.preferences.SettingsPrefManager
import org.commonvoice.saverio_lib.repositories.GithubRepository
import org.commonvoice.saverio_lib.repositories.StatsRepository

class HomeViewModel(
    private val statsRepository: StatsRepository,
    private val githubRepository: GithubRepository,
    private val settingsPrefManager: SettingsPrefManager
): ViewModel() {

    fun postStats(
        appVersion: String,
        versionCode: Int,
        appSource: String
    ) = viewModelScope.launch {
        statsRepository.postStatsUpdate(appVersion, versionCode.toString(), appSource)
    }

    fun checkForNewVersion(localVersion: String): LiveData<String> = liveData {
        val serverVersion = try {
            githubRepository.getLatestVersion().body()?.latestVersion
        } catch (e: Exception) {
            null
        } ?: localVersion

        if (serverVersion != settingsPrefManager.latestVersion && serverVersion != localVersion) {
            settingsPrefManager.latestVersion = serverVersion

            emit(serverVersion)
        }
    }

}