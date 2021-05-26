package cloud.sud.instatram.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDAO {
    @Insert
    suspend fun insertImageDetails(image:ImageEntity)

    @Query("SELECT * FROM image_table WHERE station_id= :myId ORDER BY date")
    fun getImageList(myId:String): Flow<List<ImageEntity>>
    @Delete
    suspend fun deletePic(image: ImageEntity)

    @Update(entity=ImageEntity::class)
    suspend fun updateTitle(up: ImUpdate)

    @Entity
    data class ImUpdate(
        @ColumnInfo val id: Int,
        @ColumnInfo val title:String
    )
}