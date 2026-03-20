package com.qrmobil.admin.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.qrmobil.admin.data.Category
import com.qrmobil.admin.data.MenuItem
import com.qrmobil.admin.data.MockDatabase
import java.util.UUID

class MenuViewModel : ViewModel() {
    private val _categories = mutableStateListOf<Category>()
    val categories: List<Category> get() = _categories

    private val _menuItems = mutableStateListOf<MenuItem>()
    val menuItems: List<MenuItem> get() = _menuItems

    init {
        loadData()
    }

    fun loadData() {
        _categories.clear()
        _categories.addAll(MockDatabase.categories)
        _menuItems.clear()
        _menuItems.addAll(MockDatabase.menuItems)
    }

    fun addMenuItem(categoryId: String, name: String, price: Double) {
        val newItem = MenuItem(id = UUID.randomUUID().toString(), categoryId = categoryId, name = name, price = price)
        MockDatabase.menuItems.add(newItem)
        _menuItems.add(newItem)
    }

    fun updateMenuItem(id: String, newName: String, newPrice: Double) {
        val index = _menuItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = _menuItems[index].copy(name = newName, price = newPrice)
            _menuItems[index] = updated
            val dbIndex = MockDatabase.menuItems.indexOfFirst { it.id == id }
            if (dbIndex != -1) MockDatabase.menuItems[dbIndex] = updated
        }
    }

    fun deleteMenuItem(id: String) {
        _menuItems.removeAll { it.id == id }
        MockDatabase.menuItems.removeAll { it.id == id }
    }

    fun getItemsForCategory(categoryId: String): List<MenuItem> {
        return _menuItems.filter { it.categoryId == categoryId }
    }
}

