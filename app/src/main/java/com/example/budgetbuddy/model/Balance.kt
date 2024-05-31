package com.example.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Clase de datos para representar el balance entre dos usuarios.
 *
 * @property user1 UID del primer usuario.
 * @property user2 UID del segundo usuario.
 * @property amountUser1 Monto del balance del primer usuario.
 * @property amountUser2 Monto del balance del segundo usuario.
 */
@Parcelize
class Balance (
    val user1 : String ? = null,
    val user2 : String? = null,
    val amountUser1: Double? = null,
    val amountUser2: Double? = null,
): Parcelable