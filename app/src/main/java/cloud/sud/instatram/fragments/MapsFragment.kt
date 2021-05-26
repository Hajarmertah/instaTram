package cloud.sud.instatram.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cloud.sud.instatram.HomeViewModel
import cloud.sud.instatram.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel

    private lateinit var mMap:GoogleMap
    var lat:String="1.0"
    var lon:String ="1.0"
    private var stationId:String=""


    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap=googleMap
        /*   val sydney = LatLng(lat.toDouble(), lon.toDouble())
           googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
           googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        viewModel.stationData.observe(viewLifecycleOwner, Observer  {
            for (station in it) {
                lat = station.lat!!
                lon = station.lon!!
                val position = LatLng(lat.toDouble(), lon.toDouble())
                mMap.addMarker(MarkerOptions().position(position).title(station.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,10f))
            }
        })
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){

        }
    }

}