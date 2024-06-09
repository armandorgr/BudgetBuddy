package com.example.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 * Clase de datos para representar un gasto.
 *
 * @property title Título del gasto.
 * @property amount Monto del gasto.
 * @property date Fecha del gasto.
 * @property payer Quién pagó el gasto.
 * @property debt Deuda asociada al gasto.
 * @property payerUserName Nombre de usuario de quien pagó el gasto.
 */
@Parcelize
data class Expense(
    val title: String? = null,
    val amount: Double? = null,
    val date: String? = null,
    val payer: String? = null,
    val debt:  Double? = null,
    val payerUserName: String? = null,
    var expenseUID : String? = null
) : Parcelable
