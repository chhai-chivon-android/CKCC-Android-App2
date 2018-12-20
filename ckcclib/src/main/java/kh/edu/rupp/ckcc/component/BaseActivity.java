package kh.edu.rupp.ckcc.component;

import android.support.v7.app.AppCompatActivity;

import kh.edu.rupp.ckcc.utility.Utility;

public class BaseActivity extends AppCompatActivity {

    protected boolean isInternetAvailable(){
        return Utility.isInternetAvailable(this);
    }

    protected void showNoInternetConnectionDialog(){
        Utility.showAlertDialog("No Internet", "Internet connection is required. Please check your connection.", this);
    }

}
