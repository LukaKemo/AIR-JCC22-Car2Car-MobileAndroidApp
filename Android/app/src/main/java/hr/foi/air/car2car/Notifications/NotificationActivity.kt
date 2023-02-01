package hr.foi.air.car2car.Notifications

import AdapterForNotifications
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.air.car2car.MainMapActivity
import hr.foi.air.car2car.MqttViewModel
import hr.foi.air.car2car.R


class NotificationActivity : AppCompatActivity() {
    private lateinit var viewModel: MqttViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        viewModel = MqttViewModel.getInstance()
        Log.d("TAG", "Hash code Notification: " + viewModel.hashCode());
        val adapter = AdapterForNotifications(ArrayList())
        recyclerview.adapter = adapter

        viewModel.data.observe(this, Observer {
            Log.d("DATA", "Data changed in notifications: $it")
            adapter.updateData(it)
        })
    }
}


