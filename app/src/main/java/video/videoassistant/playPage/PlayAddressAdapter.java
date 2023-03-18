package video.videoassistant.playPage;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.util.UiUtil;
import video.videoassistant.viewUtil.TagFlowAdapter;


public class PlayAddressAdapter extends TagFlowAdapter {


    private static final String TAG = "PlayAddressAdapter";
    List<PlayBean> types;
    private Context mContext;

    public PlayAddressAdapter(List<PlayBean> types, Context mContext) {
        this.types = types;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public interface OnItemClickListener {
        void onItemClick(String typeId, String typeName,String name);
    }


    @Override
    public View getView(int position) {
        View view = View.inflate(mContext, R.layout.item_textview, null);
        TextView textView = view.findViewById(R.id.name);
        textView.setText(types.get(position).getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position + "",
                            types.get(position).getUrl(),types.get(position).getName());
                }
            }
        });
        return view;
    }


}
