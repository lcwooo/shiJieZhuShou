package video.videoassistant.viewUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import video.videoassistant.R;

public class FlowViewGroup extends ViewGroup {
    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingBottom;
    private Context mContext;

    public FlowViewGroup(Context context) {
        super(context);
        mContext = context;
    }


    public FlowViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FlowViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = 0;
        int left;
        int lineHeight = 0;
        for (int i = 0; i < lines.size(); i++) {
            left = 0;
            top += lineHeight;
            lineHeight = 0;
            ArrayList<View> views = lines.get(i);
            for (View view : views) {
                if (view.getVisibility() == GONE) {
                    continue;
                }
                MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
                view.layout(left + layoutParams.leftMargin + mPaddingLeft, top + layoutParams.topMargin + mPaddingTop, left + view.getMeasuredWidth() + layoutParams.leftMargin + mPaddingLeft, top + view.getMeasuredHeight() + layoutParams.topMargin + mPaddingTop);
                lineHeight = Math.max(lineHeight, view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                left = left + view.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            }
        }

    }

    //计算后要显示行的数据的集合
    private ArrayList<ArrayList<View>> lines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        lines.clear();
        //自身的padding
        mPaddingTop = getPaddingTop();
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);

        //通过计算获取的总行高(包括ViewGroup的padding和子View的margin)
        int linesHeight = 0;
        //最宽行的宽度
        int widthMax = 0;
        //当前行已占用的宽度
        int lineEmployWidth = 0;
        //计算时当前行的最大高度
        int currentLineHeightMax = 0;
        //每一行中View的数据集
        ArrayList<View> lineInfo = new ArrayList<>();
        //获取子View的个数
        int childCount = getChildCount();
        //遍历子View对其进行测算
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //判断子View的显示状态 gone就不进行测算
            if (childView.getVisibility() == GONE) {
                continue;
            }
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = 0;
            int childHeight = 0;
            //获取view的测量宽度
            childWidth += childView.getMeasuredWidth();
            //每行的第一个添加父布局的paddingLeft
            if (0 == i) {
                childWidth += mPaddingLeft;
            }
            //获取子View自身的margin属性
            childWidth += (layoutParams.leftMargin + layoutParams.rightMargin);
            //当前的行高
            childHeight = childHeight + (childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
            //当前行放不下时，重起一行显示
            if (lineEmployWidth + childWidth > widthMeasureSize - mPaddingRight) {
                //初始当前行的宽度
                lineEmployWidth = childWidth + mPaddingLeft;
                //添加一次行高
                linesHeight += currentLineHeightMax;
                //初始化行高
                currentLineHeightMax = childHeight;
                lines.add(lineInfo);
                lineInfo = new ArrayList<>();
                lineInfo.add(childView);
            } else {//当前行可以显示时
                lineInfo.add(childView);
                //增加当前行已显示的宽度
                lineEmployWidth += childWidth;
                //为了显示最大的行高
                currentLineHeightMax = Math.max(currentLineHeightMax, childHeight);
                //显示中最大的行宽
                widthMax = Math.max(widthMax, lineEmployWidth);
            }
        }
        lines.add(lineInfo);
        linesHeight += (mPaddingTop + mPaddingBottom + currentLineHeightMax);
        setMeasuredDimension((widthMeasureMode == MeasureSpec.EXACTLY) ? widthMeasureSize : widthMax + mPaddingRight, (heightMeasureMode == MeasureSpec.EXACTLY) ? heightMeasureSize : linesHeight);
    }


    /**
     * 重写该方法是为了使用MarginLayoutParams获取子View的margin值
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
    /**
     *当使用adapter添加数据时使用。
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    private TagFlowAdapter mAdapter;

    public void setAdapter(TagFlowAdapter adapter) {
        if (null == adapter) {
            throw new NullPointerException("TagFlowAdapter is null, please check setAdapter(TagFlowAdapter adapter)...");
        }
        mAdapter = adapter;
        adapter.setOnNotifyDataSetChangedListener(new TagFlowAdapter.OnNotifyDataSetChangedListener() {
            @Override
            public void OnNotifyDataSetChanged() {
                notifyDataSetChanged();
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        MarginLayoutParams layoutParams = new MarginLayoutParams(generateDefaultLayoutParams());
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i);
            if (view == null) {
                throw new NullPointerException("item layout is null, please check getView()...");
            }
            addView(view, i, layoutParams);
        }
    }

    public void selectLocation(int location){
        Log.i("PlayActivity", "selectLocation: "+location);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            TextView name = view.findViewById(R.id.name);
            if(i==location){
                name.setTextColor(mContext.getResources().getColor(R.color.white));
                name.setBackground(mContext.getResources().getDrawable(R.drawable.shap_select_movies));
            }else {
                name.setTextColor(mContext.getResources().getColor(R.color.textColor));
                name.setBackground(mContext.getResources().getDrawable(R.drawable.shap_all_movies));
            }
        }
    }
}

