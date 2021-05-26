package cloud.sud.instatram

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cloud.sud.instatram.data.StationRepository

class HomeViewModel (app: Application) : AndroidViewModel(app) {
    private val dataRepo = StationRepository(app)
    val stationData = dataRepo.stationData
}