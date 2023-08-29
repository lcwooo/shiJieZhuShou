package video.videoassistant.me;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

public class MeModel extends BaseViewModel {

    public String editTextValue;

    public MutableLiveData<Boolean> imageViewClicked = new MutableLiveData<>();
    public MutableLiveData<Boolean> hostSave = new MutableLiveData<>();


    public void setImageViewClicked(boolean imageViewClicked) {
        this.imageViewClicked.setValue(imageViewClicked);
    }

    public void setHostSave() {
        this.hostSave.setValue(true);
    }
}
