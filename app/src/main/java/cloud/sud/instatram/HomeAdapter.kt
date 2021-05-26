package cloud.sud.instatram

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cloud.sud.instatram.activities.ImagesActivity
import cloud.sud.instatram.data.Tram
import kotlinx.android.synthetic.main.station_row_item.view.*

class HomeAdapter(val context: Context,
                          val stations: List<Tram>):
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    override fun getItemCount() = stations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.station_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]
        holder.itemView.nameText.text =station.name
        holder.station= station
    }
    //  ViewHolder
    inner class ViewHolder(itemView: View, var station: Tram? =null): RecyclerView.ViewHolder(itemView) {

        init{
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ImagesActivity::class.java)
                intent.putExtra(mtitle,station?.name)
                intent.putExtra(stationID ,station?.id)
                itemView.context.startActivity(intent)
            }

        }
    }
    companion object{
        const val  mtitle ="title"
        const val stationID ="stationId"
    }


}