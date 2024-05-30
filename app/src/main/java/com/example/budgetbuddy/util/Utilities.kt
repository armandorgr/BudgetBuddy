package com.example.budgetbuddy.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.example.budgetbuddy.model.GROUP_CATEGORY
import com.example.budgetbuddy.model.ROLE

/**
 * Clase que contiene constantes usadas en la aplicación y métodos de utilidad
 *
 * @author Armando Guzmán
 * */
class Utilities {
    companion object {
        const val PROFILE_PIC_GG = "GG"
        const val PROFILE_PIC_ST = "ST"
        val ROLES_LIST = listOf(ROLE.MEMBER, ROLE.ADMIN)
        val CATEGORIES_LIST = listOf(
            GROUP_CATEGORY.UNDEFINED,
            GROUP_CATEGORY.TRIP,
            GROUP_CATEGORY.BIRTHDAY,
            GROUP_CATEGORY.RESTAURANT
        )

        /**
         * Método que sirve para esconder el teclado
         * Funcionamiento consultado aquí: https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically
         * @param activity Actividad desde donde se llama el método
         * @param context Contexto desde donde se llama al método
         * */
        fun hideKeyboard(activity: Activity, context: Context) {
            activity.currentFocus?.let { view ->
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}