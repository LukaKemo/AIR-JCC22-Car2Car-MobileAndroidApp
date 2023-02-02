package hr.foi.air.car2car.Notifications

import AdapterForNotifications
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.air.car2car.MainMapActivity
import hr.foi.air.car2car.MqttViewModel
import hr.foi.air.car2car.R


class NotificationActivity : AppCompatActivity() {
    private lateinit var viewModel: MqttViewModel
    private val adapter = AdapterForNotifications(ArrayList())
    private var currentFilter: String = "NOTIFICATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val buttonNotifications: Button = findViewById(R.id.round_button_notifications)
        val mImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getDrawable(R.drawable.map_icon)
        } else {
            TODO()
        }
        buttonNotifications.setCompoundDrawablesWithIntrinsicBounds(mImage, null, null, null)
        buttonNotifications.setOnClickListener {
            val intent = Intent(this, MainMapActivity::class.java)
            startActivity(intent)
        }

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        viewModel = MqttViewModel.getInstance()
        Log.d("TAG", "Hash code Notification: " + viewModel.hashCode());

        recyclerview.adapter = adapter

        viewModel.data.observe(this, Observer {
            Log.d("DATA", "Data changed in notifications: $it")
            adapter.updateData(it,currentFilter)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.location -> {
                currentFilter = "LOCATION"
            }
            R.id.danger -> {
                currentFilter = "DANGER"
            }
            R.id.notification -> {
                currentFilter = "NOTIFICATION"
            }
        }
        adapter.updateData(viewModel.data.value, currentFilter)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.notifications_navigation, menu);
    return true
    }
}


