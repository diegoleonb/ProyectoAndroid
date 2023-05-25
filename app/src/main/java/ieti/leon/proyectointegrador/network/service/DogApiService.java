package ieti.leon.proyectointegrador.network.service;

import ieti.leon.proyectointegrador.network.dto.BreedsImgDto;
import ieti.leon.proyectointegrador.network.dto.BreedsListDto;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DogApiService {
    @GET("api/breeds/list/all")
    Call<BreedsListDto> getAllBreeds();

    @GET("api/breed/pitbull/images/random")
    Call<BreedsImgDto> getImage();
}
