package com.example.data

data class CategoryInfo(
    val name: String,
    val bengaliName: String,
    val type: String // "EXPENSE", "INCOME", or "BOTH"
)

object Categories {
    val list = listOf(
        CategoryInfo("Food", "খাদ্য", "EXPENSE"),
        CategoryInfo("Transport", "যাতায়াত", "EXPENSE"),
        CategoryInfo("Shopping", "কেনাকাটা", "EXPENSE"),
        CategoryInfo("House Rent", "বাসা ভাড়া", "EXPENSE"),
        CategoryInfo("Education", "শিক্ষা", "EXPENSE"),
        CategoryInfo("Medical", "চিকিৎসা", "EXPENSE"),
        CategoryInfo("Mobile", "মোবাইল", "EXPENSE"),
        CategoryInfo("Salary", "বেতন", "INCOME"),
        CategoryInfo("Business", "ব্যবসা", "INCOME"),
        CategoryInfo("Other", "অন্যান্য", "BOTH")
    )
}
