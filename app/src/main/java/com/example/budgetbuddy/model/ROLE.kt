package com.example.budgetbuddy.model

import com.example.budgetbuddy.R
/**
 * Enumeración que agrupa los roles disponibles dentro de la aplicación
 * El uso de los enum en Kotlin fur consultado en la documentación de Kotlin: https://kotlinlang.org/docs/enum-classes.html
 * @author Armando Guzmán
 * */
enum class ROLE(val resourceID: Int) {
    ADMIN(R.string.admin_role),
    MEMBER(R.string.member_role)
}