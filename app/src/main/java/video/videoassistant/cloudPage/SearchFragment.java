package video.videoassistant.cloudPage;


import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;


import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentSearchBinding;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class SearchFragment extends BaseFragment<SearchModel, FragmentSearchBinding> {


    @Override
    protected SearchModel initViewModel() {
        return new ViewModelProvider(this).get(SearchModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView() {
        viewModel.showDialog.setValue(true);
    }

    @Override
    protected void initData() {

        LiveEventBus.get(Constant.searchData, List.class).observe(this, new Observer<List>() {
            @Override
            public void onChanged(List list) {
                viewModel.showDialog.setValue(false);
                List<SearchBean> searchBeans = list;
                if(UiUtil.listIsEmpty(searchBeans)){
                    return;
                }
                initList(searchBeans);
            }
        });


    }

    private void initList(List<SearchBean> searchBeans) {
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SearchAdapter searchAdapter = new SearchAdapter();
        dataBinding.recyclerView.setAdapter(searchAdapter);
        searchAdapter.setNewData(searchBeans);


    }
}
