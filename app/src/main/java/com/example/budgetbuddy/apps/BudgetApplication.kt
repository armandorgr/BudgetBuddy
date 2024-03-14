package com.example.budgetbuddy.apps

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase que sirve para hacer que la inyeccion de dependencias funcione correctamente
 * */
@HiltAndroidApp
class BudgetApplication : Application()