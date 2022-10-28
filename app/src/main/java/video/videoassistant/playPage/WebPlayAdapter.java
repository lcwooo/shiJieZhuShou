package video.videoassistant.playPage;

import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.AdapterJsonBinding;
import video.videoassistant.me.handleManage.HandleEntity;


public class WebPlayAdapter extends BaseDBRVAdapter<HandleEntity, AdapterJsonBinding> {
    SortIndex sortIndex;

    public void getSortIndex(SortIndex sortIndex) {
        this.sortIndex = sortIndex;
    }

    public WebPlayAdapter() {
        super(R.layout.adapter_json, BR.bean);
    }

    @Override
    protected void initData(AdapterJsonBinding binding, HandleEntity HandleEntity, int position) {
        binding.tvArea.setText(HandleEntity.getName());

        binding.tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortIndex.toIndex(HandleEntity,position);
            }
        });
    }


}
