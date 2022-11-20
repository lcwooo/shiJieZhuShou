package video.videoassistant.mainPage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import io.reactivex.Flowable;
import video.videoassistant.net.Api;
import video.videoassistant.playPage.PlayBean;

public class MainModel extends BaseViewModel {

    public MutableLiveData<RuleVersionBean> versionBeanData = new MutableLiveData<>();


    public void getAdRule() {
        Flowable<RuleVersionBean> api = Api.getApi().getVersionBean("http://192.168.10.107:7001/adRule");

        request(api, new ResultListener<RuleVersionBean>() {
            @Override
            public void onSucceed(RuleVersionBean data) {
                versionBeanData.postValue(data);
            }

            @Override
            public void onFail(String t) {

            }
        });
    }
}
