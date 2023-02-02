import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import hr.foi.air.car2car.Notifications.NotificationViewModel
import hr.foi.air.car2car.R
import kotlin.collections.ArrayList

class AdapterForNotifications (private var logList: List<NotificationViewModel>) : RecyclerView.Adapter<AdapterForNotifications.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_single, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = logList[position]
        // sets the image to the ImageView from our itemHolder class
        // sets the text to the TextView from our itemHolder class
        holder.textView.text = notification.text
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return logList.size
    }


    fun updateData(it: ArrayList<NotificationViewModel>?, filter: String) {
        if (it != null) {
            when (filter){
                "NOTIFICATION"-> {
                    val filteredList = it.filter { notification ->
                        notification.text.startsWith("NOTIFICATION:")
                    }
                    val sortedList = filteredList.sortedBy { notification ->
                        notification.text
                    }
                    logList = sortedList
                }

                "DANGER"-> {
                    val filteredList = it.filter { notification ->
                            notification.text.startsWith("DANGER:") }
                    val sortedList = filteredList.sortedBy { notification ->
                        notification.text }
                    logList = sortedList
                }

                "LOCATION"->{
                    val filteredList = it.filter { notification ->
                        notification.text.startsWith("LOCATION:")
                    }
                    val sortedList = filteredList.sortedBy { notification ->
                        notification.text
                    }
                    logList = sortedList
                }

                ""->{
                    logList = it.asReversed()
                }
            }

            notifyDataSetChanged()
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
