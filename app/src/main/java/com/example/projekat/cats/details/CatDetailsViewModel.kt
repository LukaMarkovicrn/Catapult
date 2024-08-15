package com.example.projekat.cats.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.cats.mappers.asCatUiModel
import com.example.projekat.cats.repository.CatsRepository
import com.example.projekat.navigation.catId
import com.example.projekat.photos.mappers.asPhotoUiModel
import com.example.projekat.photos.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlinx.coroutines.flow.getAndUpdate
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CatsRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val catId: String = savedStateHandle.catId

    private val _state = MutableStateFlow(CatDetailsContract.CatDetailsState())
    val state = _state.asStateFlow()
    private fun setState(reducer: CatDetailsContract.CatDetailsState.() -> CatDetailsContract.CatDetailsState) =
        _state.getAndUpdate(reducer)




    init {
        fetchCat()
        observeCatDetails()

    }

    private fun fetchCat() {
        viewModelScope.launch {
            setState { copy(fetching = true) }
            try {

                withContext(Dispatchers.IO) {
                    repository.getCatDetails(catId = catId)
                }


            } catch (error: Exception) {
                // TODO Handle error
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }


    private fun fetchImage(photoId: String, catId: String){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    photoRepository.fetchPhoto(photoId = photoId, catId = catId)
                }
                getImage()
            } catch (error: IOException) {
                setState { copy(error = true) }
            }
        }
    }


    private fun getImage(){
        viewModelScope.launch {
            try {
                val photo = withContext(Dispatchers.IO) {
                    photoRepository.getPhotosByCatId(catId)[0].asPhotoUiModel()
                }
                setState { copy(image = photo) }
            } catch (error: IOException) {
                setState { copy(error = true) }
            }
        }
    }


    private fun observeCatDetails() {
        viewModelScope.launch {
            repository.observeCatDetails(catId = catId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(cat = it.asCatUiModel()) }
                    fetchImage(it.reference_image_id, it.id)
                }
        }
    }



    /**
     * Observes password details data from our local data
     * and updates the state.
     */

//    private fun observePasswordDetails() {
//        viewModelScope.launch {
//            repository.observePasswordDetails(passwordId = id)
//                .filterNotNull()
//                .collect {
//                    setState { copy(cat = it) }
//                }
//        }
//    }

    /**
     * Observes events sent to this viewModel from UI.
     */


    /**
     * Triggers updating local password details by calling "api"
     * to get latest data and update underlying local data we use.
     *
     * Note that we are not updating the state here. This is done
     * from observePasswordDetails(). Both functions are using
     * the single source of truth (PasswordRepository) so we can
     * do this. If we break this principle, the app will stop working.
     */
//    private fun fetchPasswordDetails() {
//        viewModelScope.launch {
//            setState { copy(fetching = true) }
//            try {
//                withContext(Dispatchers.IO) {
//                    repository.fetchPasswordDetails(passwordId = id)
//                }
//            } catch (error: IOException) {
//                setState {
//                    copy(error = CatDetailsState.DetailsError.DataUpdateFailed(cause = error))
//                }
//            } finally {
//                setState { copy(fetching = false) }
//            }
//        }
//    }



}