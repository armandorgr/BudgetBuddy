package com.example.budgetbuddy.model

import com.example.budgetbuddy.R

/**
 * Enumeración que agrupa las categorías de grupos disponibles dentro de la aplicación
 * El uso de los enum en Kotlin fur consultado en la documentación de Kotlin: https://kotlinlang.org/docs/enum-classes.html
 * @author Armando Guzmán
 * */
enum class GROUP_CATEGORY(val colorID: Int, val stringID: Int, val iconID: Int) {
    RESTAURANT(
        R.color.restaurant_category_color,
        R.string.restaurant_category,
        R.drawable.restaurant_icon
    ),
    UNDEFINED(
        R.color.undefined_category_color,
        R.string.undefined_category,
        R.drawable.undefined_icon
    ),
    BIRTHDAY(R.color.birthday_category_color, R.string.birthday_category, R.drawable.cake_icon),
    TRIP(R.color.travel_category_color, R.string.trip_category, R.drawable.travel_icon)
}