package cloud.sud.instatram.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class ImageEntity (
    @ColumnInfo val image: String,
    @ColumnInfo(name = "image_source") val imageSource:String,
    @ColumnInfo val date :String,
    @ColumnInfo val title:String,
    @ColumnInfo(name = "station_name") val stationName:String,
    @ColumnInfo(name = "station_id") val stationId:String,
    @PrimaryKey(autoGenerate = true) val id: Int=0
)