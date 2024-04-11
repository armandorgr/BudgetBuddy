package com.example.budgetbuddy.apps

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase que sirve para hacer que la inyeccion de dependencias funcione correctamente
 * La implementación de la inyección de dependencias se obtuvo de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Dependency Injection with Dagger, Hilt, and Koin
 * */
@HiltAndroidApp
class BudgetApplication : Application()