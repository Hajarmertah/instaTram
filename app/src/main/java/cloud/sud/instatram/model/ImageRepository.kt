package cloud.sud.instatram.model

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ImageRepository(private val im: ImageDAO) {

    @WorkerThread
    suspend fun insertImageData(instaTram: ImageEntity) {
        im.insertImageDetails(instaTram)
    }


    suspend fun deleteImageData(image:ImageEntity){
        im.deletePic(image)
    }

    @WorkerThread
    suspend fun updateImageData(image: ImageDAO.ImUpdate){
        im.updateTitle(image)
    }


    fun allImagesList(id:String): Flow<List<ImageEntity>> {

        return im.getImageList(id)
    }
}