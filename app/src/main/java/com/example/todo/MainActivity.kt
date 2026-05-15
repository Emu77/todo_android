package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.ui.theme.TodoTheme

val cardColors = listOf(
    Color(0xFFFF6B6B),
    Color(0xFFFFD93D),
    Color(0xFF6BCB77),
    Color(0xFF4D96FF),
    Color(0xFFFF922B),
    Color(0xFFC77DFF),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme(darkTheme = false) {
                TodoApp()
            }
        }
    }
}

@Composable
fun EditTodoDialog(
    todo: TodoItem,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(todo.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text("✏️ Aufgabe bearbeiten", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B6B),
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Speichern") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(todoViewModel: TodoViewModel = viewModel()) {
    val todos by todoViewModel.todos.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF0F5), Color(0xFFE8F4FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Meine To-Dos",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D2D2D)
            )
            Text(
                text = "${todos.count { !it.isDone }} offen · ${todos.count { it.isDone }} erledigt",
                fontSize = 14.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Neue Aufgabe...") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B6B),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            todoViewModel.addTodo(input)
                            input = ""
                        }
                    },
                    containerColor = Color(0xFFFF6B6B),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(todos, key = { it.id }) { todo ->
                    val cardColor = cardColors[todo.id % cardColors.size]
                    val bgColor by animateColorAsState(
                        targetValue = if (todo.isDone) Color(0xFFEEEEEE) else cardColor.copy(alpha = 0.15f),
                        animationSpec = tween(300)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (todo.isDone) Color.LightGray else cardColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = todo.text,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                fontWeight = if (todo.isDone) FontWeight.Normal else FontWeight.Medium,
                                color = if (todo.isDone) Color.Gray else Color(0xFF2D2D2D),
                                textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
                            )

                            Checkbox(
                                checked = todo.isDone,
                                onCheckedChange = { todoViewModel.toggleDone(todo) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = cardColor,
                                    uncheckedColor = cardColor
                                )
                            )

                            IconButton(onClick = { editingTodo = todo }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Bearbeiten",
                                    tint = Color(0xFFAAAAAA)
                                )
                            }

                            IconButton(onClick = { todoViewModel.deleteTodo(todo) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Löschen",
                                    tint = Color(0xFFCCCCCC)
                                )
                            }
                        }
                    }
                }

                if (todos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = { todoViewModel.clearAll() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Alle löschen",
                                color = Color(0xFFCCCCCC),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    editingTodo?.let { todo ->
        EditTodoDialog(
            todo = todo,
            onDismiss = { editingTodo = null },
            onConfirm = { newText ->
                todoViewModel.updateText(todo, newText)
                editingTodo = null
            }
        )
    }
}