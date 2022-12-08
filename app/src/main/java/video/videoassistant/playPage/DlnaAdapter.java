package video.videoassistant.playPage;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import org.fourthline.cling.model.meta.Device;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.ItemDlnaBinding;

public class DlnaAdapter extends BaseDBRVAdapter<Device, ItemDlnaBinding> {

    public DlnaAdapter() {
        super(R.layout.item_dlna, BR.view);
    }

    @Override
    protected void initData(ItemDlnaBinding binding, Device device, int position) {
        binding.name.setText(device.getDetails().getFriendlyName());
    }
}
