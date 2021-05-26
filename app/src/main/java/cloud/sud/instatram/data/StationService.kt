package cloud.sud.instatram.data

import retrofit2.Response
import retrofit2.http.GET

interface StationService {
    @GET("/v3/9e519596-909a-4302-82c0-056de21de9c2")
    suspend fun getStationData(): Response<StationData>
}