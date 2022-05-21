package com.radumarinescu.components

data class Product(
    val productId: String,
    val category: ProductCategory,
    val brand: Brand,
    val name: String,
    val imageURL: String,
    val stock: Int,
    val price: Double,
    val reviews: List<ProductReview>
)
