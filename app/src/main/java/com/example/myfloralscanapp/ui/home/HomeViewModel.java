package com.example.myfloralscanapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
<<<<<<< HEAD
        mText.setValue("This is home fragment");
=======
        mText.setValue("");
>>>>>>> 181e3eb (Application Push)
    }

    public LiveData<String> getText() {
        return mText;
    }
}