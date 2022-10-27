package video.videoassistant.cloudPage;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.viewUtil.TagFlowAdapter;


public class TypeAdapter extends TagFlowAdapter {



    List<TypeBean> types;
    private Context mContext;

    public TypeAdapter(List<TypeBean> types, Context mContext) {
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
    public void setOnItemClickListener(OnItemClickListener li){
        mListener = li;
    }
    public interface OnItemClickListener{
        void onItemClick(String typeId,String typeName);
    }


    @Override
    public View getView(int position) {
        View view = View.inflate(mContext, R.layout.item_textview, null);
        TextView textView = view.findViewById(R.id.name);
        textView.setText(types.get(position).getTypeName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onItemClick(types.get(position).getTypeId()+"",
                            types.get(position).getTypeName());
                }
            }
        });
        return view;
    }
}
