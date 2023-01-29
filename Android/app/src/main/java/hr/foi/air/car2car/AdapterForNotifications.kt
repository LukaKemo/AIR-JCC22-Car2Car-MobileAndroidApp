package hr.foi.air.car2car

import android.service.autofill.CustomDescription
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterForNotifications (private val logList: List<NotificationViewModel>) : RecyclerView.Adapter<AdapterForNotifications.ViewHolder>() {

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
        val NotificationViewModel = logList[position]
        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(NotificationViewModel.image)
        // sets the text to the textview from our itemHolder class
        holder.textView.text = NotificationViewModel.text
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return logList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
