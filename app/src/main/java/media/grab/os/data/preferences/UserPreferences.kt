package media.grab.os.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import media.grab.os.data.model.FileNameMode
import media.grab.os.data.model.YtDlpUpdateChannel

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class AccessMode { NORMAL, ACCESSIBILITY, SHIZUKU, ROOT }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mediagrab_prefs")

data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accessMode: AccessMode = AccessMode.NORMAL,
    val fileNameMode: FileNameMode = FileNameMode.ORIGINAL,
    val ytDlpUpdateChannel: YtDlpUpdateChannel = YtDlpUpdateChannel.STABLE,
    val onboardingDone: Boolean = false
)

class UserPreferences(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val ACCESS = stringPreferencesKey("access_mode")
        val FILE_NAME_MODE = stringPreferencesKey("file_name_mode")
        val YTDLP_UPDATE_CHANNEL = stringPreferencesKey("ytdlp_update_channel")
        val ONBOARDING = booleanPreferencesKey("onboarding_done")
    }

    val settings: Flow<Settings> = context.dataStore.data.map { p ->
        Settings(
            themeMode = runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM),
            accessMode = runCatching { AccessMode.valueOf(p[Keys.ACCESS] ?: "NORMAL") }.getOrDefault(AccessMode.NORMAL),
            fileNameMode = runCatching { FileNameMode.valueOf(p[Keys.FILE_NAME_MODE] ?: "ORIGINAL") }.getOrDefault(FileNameMode.ORIGINAL),
            ytDlpUpdateChannel = runCatching {
                YtDlpUpdateChannel.valueOf(p[Keys.YTDLP_UPDATE_CHANNEL] ?: "STABLE")
            }.getOrDefault(YtDlpUpdateChannel.STABLE),
            onboardingDone = p[Keys.ONBOARDING] ?: false
        )
    }

    suspend fun setTheme(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = mode.name }
    }

    suspend fun setAccessMode(mode: AccessMode) {
        context.dataStore.edit { it[Keys.ACCESS] = mode.name }
    }

    suspend fun setFileNameMode(mode: FileNameMode) {
        context.dataStore.edit { it[Keys.FILE_NAME_MODE] = mode.name }
    }

    suspend fun setYtDlpUpdateChannel(channel: YtDlpUpdateChannel) {
        context.dataStore.edit { it[Keys.YTDLP_UPDATE_CHANNEL] = channel.name }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING] = done }
    }
}
