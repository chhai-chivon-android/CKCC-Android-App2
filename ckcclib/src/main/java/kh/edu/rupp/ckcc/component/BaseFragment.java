package kh.edu.rupp.ckcc.component;

import android.support.v4.app.Fragment;

import kh.edu.rupp.ckcc.utility.Utility;

public class BaseFragment extends Fragment {

    protected boolean isInternetAvailable(){
        return Utility.isInternetAvailable(getActivity());
    }

    protected void showNoInternetConnectionDialog(){
        Utility.showAlertDialog("No Internet", "Internet connection is required. Please check your connection.", getActivity());
    }

}
