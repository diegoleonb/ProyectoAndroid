package ieti.leon.proyectointegrador;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import ieti.leon.proyectointegrador.databinding.FragmentSecondBinding;
import ieti.leon.proyectointegrador.network.RetrofitInstance;
import ieti.leon.proyectointegrador.network.dto.BreedsImgDto;
import ieti.leon.proyectointegrador.network.dto.BreedsListDto;
import ieti.leon.proyectointegrador.network.service.DogApiService;
import ieti.leon.proyectointegrador.storage.EncryptedStorage;
import ieti.leon.proyectointegrador.storage.JWTInterceptor;
import ieti.leon.proyectointegrador.storage.SharedPreferencesStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private static final String TAG = "MainActivity-SecondFragment";
    final String SHARED_PREFERENCES_FILE_NAME = "my_prefs";

    private TextView textView;
    private ImageView imageView;

    private SharedPreferencesStorage sharedPreferencesStorage;

    private EncryptedStorage encryptedStorage;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        initRestApi();
        return binding.getRoot();

    }

    public void initRestApi(){
        try {
            init(binding.getRoot());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        DogApiService dogApiService = RetrofitInstance.getRetrofitInstance(new JWTInterceptor(encryptedStorage)).create(DogApiService.class);

        Call<BreedsImgDto> img = dogApiService.getImage();
        img.enqueue(new Callback<BreedsImgDto>() {
            @Override
            public void onResponse(Call<BreedsImgDto> call, Response<BreedsImgDto> response) {
                if (response.isSuccessful()) {
                    loadDogInfo(response.body().getMessage());
                } else {
                    Log.e(TAG, "Error en la respuesta de la API");
                }
            }

            @Override
            public void onFailure(Call<BreedsImgDto> call, Throwable t) {
                Log.e(TAG, "Error al llamar a la API", t);
            }
        });

        Call<BreedsListDto> call = dogApiService.getAllBreeds();
        call.enqueue(new Callback<BreedsListDto>() {
            @Override
            public void onResponse(Call<BreedsListDto> call, Response<BreedsListDto> response) {
                if (response.isSuccessful()) {
                    Map<String, String[]> breeds = response.body().getMessage();
                    for (Map.Entry<String, String[]> entry : breeds.entrySet()) {
                        Log.d(TAG, "Raza: " + entry.getKey());
                        for (String subRaza : entry.getValue()) {
                            Log.d(TAG, "Subraza: " + subRaza);
                        }
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta de la API");
                }
            }

            @Override
            public void onFailure(Call<BreedsListDto> call, Throwable t) {
                Log.e(TAG, "Error al llamar a la API", t);
            }
        });
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(view1 -> NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment));
    }

    private void init(View root) throws NoSuchAlgorithmException {
        textView = root.findViewById(R.id.textview_second);
        imageView = root.findViewById(R.id.imageview_second);
        sharedPreferencesStorage = new SharedPreferencesStorage(requireActivity().getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,MODE_PRIVATE));
        encryptedStorage = new EncryptedStorage(requireActivity().getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,MODE_PRIVATE));
    }
    private void loadDogInfo(String image) {
        String dogName = "PITBULL";
        textView.setText(dogName);
        Picasso.get()
                .load(image)
                .into(imageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}