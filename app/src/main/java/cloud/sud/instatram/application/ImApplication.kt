package cloud.sud.instatram.application

import android.app.Application
import cloud.sud.instatram.model.ImageRepository
import cloud.sud.instatram.model.ImageRoomDatabase

class ImApplication: Application() {
    private val database by lazy { ImageRoomDatabase.getDatabase(this@ImApplication) }
    val repository by lazy { ImageRepository(database.imageDAO()) }
}