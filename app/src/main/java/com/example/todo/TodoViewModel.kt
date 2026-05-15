package com.example.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TodoDatabase.getDatabase(application).todoDao()

    val todos = dao.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTodo(text: String) = viewModelScope.launch {
        dao.insert(TodoItem(text = text))
    }

    fun toggleDone(todo: TodoItem) = viewModelScope.launch {
        dao.update(todo.copy(isDone = !todo.isDone))
    }

    fun deleteTodo(todo: TodoItem) = viewModelScope.launch {
        dao.delete(todo)
    }

    fun clearAll() = viewModelScope.launch {
        dao.deleteAll()
    }

    fun updateText(todo: TodoItem, newText: String) = viewModelScope.launch {
        dao.update(todo.copy(text = newText))
    }
}