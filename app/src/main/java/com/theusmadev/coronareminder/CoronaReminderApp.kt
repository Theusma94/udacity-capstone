package com.theusmadev.coronareminder

import android.app.Application
import androidx.room.Room
import com.theusmadev.coronareminder.data.local.CoronaDatabase
import com.theusmadev.coronareminder.data.local.prefs.PreferencesHelper
import com.theusmadev.coronareminder.data.network.CoronaApiService
import com.theusmadev.coronareminder.data.repository.CoronaRepository
import com.theusmadev.coronareminder.ui.dashboard.DashboardViewModel
import com.theusmadev.coronareminder.ui.signin.SignInViewModel
import com.theusmadev.coronareminder.ui.signup.SignUpViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class CoronaReminderApp: Application() {

    override fun onCreate() {
        super.onCreate()

        val myModule = module {
            single {
                HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }
            }
            single {
                OkHttpClient.Builder()
                    .addInterceptor(get<HttpLoggingInterceptor>())
                    .build()
            }

            single {
                Retrofit.Builder()
                    .client(get())
                    .baseUrl("https://covid-api.mmediagroup.fr/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            }

            single {
                get<Retrofit>().create(CoronaApiService::class.java)
            }

            single {
                Room.databaseBuilder(
                    this@CoronaReminderApp,
                    CoronaDatabase::class.java,
                    "corona"
                ).build()
            }

            single {
                PreferencesHelper(this@CoronaReminderApp)
            }
            single {
                CoronaRepository(get(),get())
            }

            viewModel {
                SignInViewModel()
            }
            viewModel {
                SignUpViewModel()
            }
            viewModel {
                DashboardViewModel(get(),get())
            }
        }

        startKoin {
            androidContext(this@CoronaReminderApp)
            modules(listOf(myModule))
        }
    }
}