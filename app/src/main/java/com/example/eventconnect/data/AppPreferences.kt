package com.example.eventconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.eventconnect.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferences @Inject constructor(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val FAVORITE_CATEGORIES_KEY = stringSetPreferencesKey("favorite_categories")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    val favoriteCategories: Flow<Set<Category>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_CATEGORIES_KEY]?.map { Category.valueOf(it) }?.toSet() ?: emptySet()
        }

    suspend fun setDarkTheme(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isEnabled
        }
    }

    suspend fun setFavoriteCategories(categories: Set<Category>) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_CATEGORIES_KEY] = categories.map { it.name }.toSet()
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
