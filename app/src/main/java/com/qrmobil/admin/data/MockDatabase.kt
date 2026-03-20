package com.qrmobil.admin.data

data class Category(val id: String, val name: String)
data class MenuItem(val id: String, val categoryId: String, val name: String, val price: Double)
data class Feedback(val id: String, val senderName: String, val message: String, val date: String)

object MockDatabase {
    val categories = mutableListOf<Category>()
    val menuItems = mutableListOf<MenuItem>()
    val feedbacks = mutableListOf<Feedback>()
}
