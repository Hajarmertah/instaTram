package cloud.sud.instatram.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImageEntity::class], version = 1)
abstract class ImageRoomDatabase: RoomDatabase() {

    abstract fun imageDAO(): ImageDAO

    companion object{
        @Volatile
        private var INSTANCE:ImageRoomDatabase? =null

        fun getDatabase(context: Context): ImageRoomDatabase{
            //if the instance is not null, then return it
            // if it is, the create the database
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageRoomDatabase::class.java,
                    "images_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE =instance
                //return instance
                instance
            }
        }
    }
}