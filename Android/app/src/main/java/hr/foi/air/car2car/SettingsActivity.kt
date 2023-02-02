package hr.foi.air.car2car

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import hr.foi.air.car2car.Notifications.NotificationActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnDark = findViewById<Switch>(R.id.switch1)
        btnDark.setOnCheckedChangeListener { _, isChecked ->
            if (btnDark.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                btnDark.text = "Disable dark mode"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                btnDark.text = "Enable dark mode"
            }
        }

        val btnReset = findViewById<Button>(R.id.resetButton)
        btnReset.setOnClickListener() {
            MainMapActivity().clearMarkers()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                startActivity(Intent(this, MainMapActivity::class.java))
                return true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about_navigation, menu);
        return true
    }
}