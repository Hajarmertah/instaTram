package cloud.sud.instatram.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cloud.sud.instatram.WEB_SERVICE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class StationRepository (val app: Application) {
    val stationData = MutableLiveData<List<Tram>>()

    init {
        if (networkAvailable()) {
            parseJSON()
        }
    }

    @SuppressLint("LongLogTag")
    fun parseJSON() {
        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WEB_SERVICE_URL)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()

        // Create Service
        val service = retrofit.create(StationService::class.java)

        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            val response = service.getStationData()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val items = response.body()?.data?.tram

                    val serviceData = items ?: emptyList()
                    stationData.postValue(serviceData)
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    //  Vérifier si la connexion Internet est en marche ou pas
    @Suppress("DEPRECATION")
    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }

}