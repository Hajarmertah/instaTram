package cloud.sud.instatram

import androidx.lifecycle.*
import cloud.sud.instatram.model.ImageDAO
import cloud.sud.instatram.model.ImageEntity
import cloud.sud.instatram.model.ImageRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class ImageViewModel (private val repository: ImageRepository): ViewModel() {
    fun insert(image:ImageEntity)=viewModelScope.launch {
        repository.insertImageData(image)

    }
    fun allPicList(Id:String) : LiveData<List<ImageEntity>> {
        return repository.allImagesList(Id).asLiveData()
    }

    fun deletePic(image: ImageEntity) =viewModelScope.launch {
        repository.deleteImageData(image)
    }
    fun updatePicTitle(image: ImageDAO.ImUpdate)=viewModelScope.launch{
        repository.updateImageData(image)
    }
}

class ImageViewModelFactory(private val repository: ImageRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImageViewModel::class.java)){
            //   @Suppress("UNCHECKED_CAST")
            return  ImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}