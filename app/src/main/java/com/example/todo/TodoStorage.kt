package com.example.todo

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "todos")

class TodoStorage(private val context: Context) {
    private val TODOS_KEY = stringPreferencesKey("todos")

    val todosFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[TODOS_KEY]?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun saveTodos(todos: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[TODOS_KEY] = todos.joinToString("||")
        }
    }
}