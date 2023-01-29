package hr.foi.air.car2car

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections.min
import kotlin.math.min
import kotlin.random.Random

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<NotificationViewModel>()
        val handler = Handler()

        fun refreshData() {
            // Generate a random number of items to add to the list, with a max of 3 items
            val randomNumOfItems = min(data.size + 3, Random.nextInt(1, 4))
            for (i in data.size until data.size + randomNumOfItems) {
                val randomInt = Random.nextInt(0, 10)
                data.add(
                    NotificationViewModel(
                        R.drawable.ic_launcher_foreground,
                        "Notification " + randomInt
                    )
                )
            }
            recyclerview.adapter = AdapterForNotifications(data)
            handler.postDelayed({ refreshData() }, 1000)
        }

        handler.postDelayed({ refreshData() }, 1000)
    }
}
