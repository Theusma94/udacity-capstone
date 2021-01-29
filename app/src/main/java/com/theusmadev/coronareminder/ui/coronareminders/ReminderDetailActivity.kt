package com.theusmadev.coronareminder.ui.coronareminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivityReminderDetailBinding
import com.theusmadev.coronareminder.ui.signin.SignInActivity

class ReminderDetailActivity: AppCompatActivity() {

    companion object {
        private const val EXTRA_Title = "EXTRA_Title"
        private const val EXTRA_Date = "EXTRA_Date"

        fun newIntent(context: Context, title: String, date: Long): Intent {
            val intent = Intent(context, ReminderDetailActivity::class.java)
            intent.putExtra(EXTRA_Title, title)
            intent.putExtra(EXTRA_Date, date)
            return intent
        }
    }

        private lateinit var binding: ActivityReminderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_reminder_detail
        )
        val titleReminder = intent.getStringExtra(EXTRA_Title)
        val timestamp = intent.getLongExtra(EXTRA_Date, 0L)

        binding.title = "You going to $titleReminder?"
        binding.timestamp = timestamp

        binding.buttonFinalize.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}