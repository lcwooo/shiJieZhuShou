package video.videoassistant.me.urlManage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.util.UiUtil;


public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> implements IItemTouchHelperAdapter {

    private List<CollectionUrlEntity> mList;
    private final OnStartDragListener mDragListener;
    private Context mContext;

    public RecyclerViewAdapter(List<CollectionUrlEntity> list, Context context, OnStartDragListener mDragListener) {
        mList = list;
        this.mDragListener = mDragListener;
        this.mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_url, parent,
                false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        CollectionUrlEntity entity = mList.get(position);
        holder.name.setText(entity.name);
        holder.url.setText(entity.url);
        if(TextUtils.isEmpty(entity.remark)){
            holder.remark.setVisibility(View.GONE);
        }else {
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(entity.remark);
        }

        holder.re.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDragListener.onStartDrag(holder);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if(fromPosition!=toPosition){
            if(mDragListener!=null){
                mDragListener.dragEnd();
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        if(mDragListener!=null){
            mDragListener.dragRemove(position);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements IItemTouchHelperViewHolder {
        private TextView name;
        private TextView url;
        private TextView remark;
        private RelativeLayout re;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);
            remark = itemView.findViewById(R.id.remark);
            re = itemView.findViewById(R.id.rl);
        }


        @Override
        public void onItemSelected() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.setTranslationZ(10);
            }

        }

        @Override
        public void onItemClear() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.setTranslationZ(0);
            }
        }
    }




    public List<CollectionUrlEntity> getSortList() {
        return mList;
    }
}
