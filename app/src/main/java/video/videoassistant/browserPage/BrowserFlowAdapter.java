package video.videoassistant.browserPage;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.cloudPage.TypeAdapter;
import video.videoassistant.cloudPage.TypeBean;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.viewUtil.TagFlowAdapter;

public class BrowserFlowAdapter extends TagFlowAdapter {

    List<HandleEntity> types;
    private Context mContext;

    public BrowserFlowAdapter(List<HandleEntity> types, Context mContext) {
        this.types = types;
        this.mContext = mContext;
    }

    private TypeAdapter.OnItemClickListener mListener;
    public void setOnItemClickListener(TypeAdapter.OnItemClickListener li){
        mListener = li;
    }
    public interface OnItemClickListener{
        void onItemClick(String typeId,String typeName);
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

    @Override
    public View getView(int position) {
        View view = View.inflate(mContext, R.layout.item_textview, null);
        TextView textView = view.findViewById(R.id.name);
        textView.setText(types.get(position).getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onItemClick(types.get(position).getUrl(),
                            types.get(position).getName());
                }
            }
        });
        return view;
    }
}
