package video.videoassistant.playPage;

import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.AdapterJsonBinding;
import video.videoassistant.me.jsonManage.JsonEntity;

public class JsonPlayAdapter extends BaseDBRVAdapter<JsonEntity, AdapterJsonBinding> {
    SortIndex sortIndex;

    public void getSortIndex(SortIndex sortIndex) {
        this.sortIndex = sortIndex;
    }

    public JsonPlayAdapter() {
        super(R.layout.adapter_json, BR.bean);
    }

    @Override
    protected void initData(AdapterJsonBinding binding, JsonEntity jsonEntity, int position) {
        binding.tvArea.setText(jsonEntity.getName());

        binding.tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortIndex.toIndex(jsonEntity,position);
            }
        });
    }


}
