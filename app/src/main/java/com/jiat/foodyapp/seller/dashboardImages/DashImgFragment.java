package com.jiat.foodyapp.seller.dashboardImages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.jiat.foodyapp.R;

import java.util.ArrayList;

public class DashImgFragment extends Fragment {

    public DashImgFragment() {
        // Required empty public constructor
    }


    private ImageSlider imageSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dash_img, container, false);
        imageSlider = view.findViewById(R.id.image_slider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel("https://icms-image.slatic.net/images/ims-web/5264c754-632f-47c8-8fdd-59eec28d61ad.jpg_1200x1200.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://icms-image.slatic.net/images/ims-web/5264c754-632f-47c8-8fdd-59eec28d61ad.jpg_1200x1200.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/butterfly-wild_53876-90200.jpg?w=1380&t=st=1660152951~exp=1660153551~hmac=cf3600731da410ea4e9332b6717bf7dd1cb0f002a0c36eca02a91e5a15c160f9", ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


        return view;
    }
}