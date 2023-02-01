package hr.foi.air.car2car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import hr.foi.air.car2car.Notifications.NotificationViewModel

class MqttViewModel private constructor(): ViewModel() {
    companion object {
        @Volatile private var instance: MqttViewModel? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MqttViewModel().also { instance = it }
            }
    }
    val data = MutableLiveData<ArrayList<NotificationViewModel>>()
}


class MainViewModelStoreOwner : ViewModelStoreOwner {
    private val viewModelStore = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }
}