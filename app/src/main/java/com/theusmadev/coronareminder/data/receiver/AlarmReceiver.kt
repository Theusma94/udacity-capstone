/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.theusmadev.coronareminder.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theusmadev.coronareminder.utils.sendNotification

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminder: String = intent.getStringExtra("data") ?: "Going somewhere?"
        val timestamp: Long = intent.getLongExtra("timestamp", 0L)
        val title = "Going to $reminder?"
            sendNotification(context,title,timestamp)
    }

}