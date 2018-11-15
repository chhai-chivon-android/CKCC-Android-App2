package com.leapkh.ckcc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private SimpleDraweeView imgProfile;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtGender;
    private TextView txtBirthday;
    private TextView txtAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_profile, container, false);
        return home;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgProfile = view.findViewById(R.id.img_profile);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtGender = view.findViewById(R.id.txtGender);
        txtBirthday = view.findViewById(R.id.txtBirthday);
        txtAddress = view.findViewById(R.id.txtAddress);

        loadProfileInfo();

    }

    private void loadProfileInfo() {
        Log.d("ckcc", "loadProfileInfo");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        fetchResult(object);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,birthday,gender,location");
        parameters.putString("field", "id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void fetchResult(JSONObject object) {
        Log.d("ckcc", "fetchResult: " + object);

        Gson gson = new Gson();
        UserProfile profile = gson.fromJson(object.toString(), UserProfile.class);
        txtName.setText("Name: " + profile.name);
        txtEmail.setText("Email: " + profile.email);
        txtGender.setText("Gender: " + profile.gender);
        txtBirthday.setText("Birthday: " + profile.birthday);
        txtAddress.setText("Address: " + profile.location.name);

        String profileImageUrl = "http://graph.facebook.com/" + profile.id + "/picture?type=large";
        imgProfile.setImageURI(profileImageUrl);

        /*
        try {
            String name = object.getString("name");
            String email = object.getString("email");
            String gender = object.getString("gender");
            String birthday = object.getString("birthday");
            JSONObject locationObject = object.getJSONObject("location");
            String locationName = locationObject.getString("name");

            txtName.setText("Name: " + name);
            txtEmail.setText("Email: " + email);
            txtGender.setText("Gender: " + gender);
            txtBirthday.setText("Birthday: " + birthday);
            txtAddress.setText("Address: " + locationName);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("ckcc", "fetchResult error: " + e.getMessage());
        }
        */
    }

}
