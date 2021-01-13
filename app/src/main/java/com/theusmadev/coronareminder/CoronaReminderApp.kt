package com.theusmadev.coronareminder

import android.app.Application
import com.theusmadev.coronareminder.ui.signin.SignInViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CoronaReminderApp: Application() {

    override fun onCreate() {
        super.onCreate()

        val myModule = module {
            viewModel {
                SignInViewModel()
            }
        }

        startKoin {
            androidContext(this@CoronaReminderApp)
            modules(listOf(myModule))
        }
    }
}