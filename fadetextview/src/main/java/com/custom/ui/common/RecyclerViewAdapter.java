package com.custom.ui.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.custom.ui.textview.R;

import java.util.ArrayList;
import java.util.List;


public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private static final int BASE_ITEM_TYPE_HEADER = Integer.MIN_VALUE;
    private static final int BASE_ITEM_TYPE_FOOTER = Integer.MAX_VALUE;

    /**
     * Map : int(HeaderView的类型) <----> view(所对应的HeaderView)
     */
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();

    /**
     * Map : int(FooterView的类型) <----> view(所对应的FooterView)
     */
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    /**
     * 所有的数据
     */
    private List<T> mData;
    private Context mContext;
    private LayoutInflater mInflater;


    /**
     * Item的点击事件
     */
    private OnItemClickListener<T> mOnItemClickListener;

    private OnItemClick mOnItemClick;

    /**
     * Item点击事件
     */
    public interface OnItemClickListener<T> {
        /**
         * 当Item点击的时候回调的方法
         *
         * @param view     点击的视图
         * @param position 点击的位置
         * @param id       点击视图的ID
         */
        void onItemClick(View view, T t, int position, long id);
    }

    public RecyclerViewAdapter(Context context) {
        this(context, null);
    }

    public RecyclerViewAdapter(Context context, List<T> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFooterViews.keyAt(position - getHeaderCount() - getDataSize());
        }
        return 0;
    }

    /**
     * 是否是普通的ItemView，也就是除去HeaderView和FooterView之外的普通的ViewItem
     *
     * @param position 当前Item的位置
     * @return true 是普通的ItemView / false 不是普通的ItemView
     */
    public boolean isGeneralItemView(int position) {
        return !isHeaderViewPos(position) && !isFooterViewPos(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup oldLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (mHeaderViews.get(viewType) != null || mFooterViews.get(viewType) != null) {
                        return gridLayoutManager.getSpanCount();
                    }

                    if (oldLookup != null) {
                        return oldLookup.getSpanSize(position);
                    }
                    return 1;
                }
            });
            gridLayoutManager.setSpanSizeLookup(gridLayoutManager.getSpanSizeLookup());
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /**
     * 添加多个HeaderView
     *
     * @param views 需要添加的多个View
     */
    public void addHeaderViews(View... views) {
        if (views == null) {
            return;
        }

        for (View view : views) {
            addHeaderView(view);
        }
    }

    /**
     * 将一个View添加的HeaderView
     *
     * @param view 需要添加的View
     */
    public void addHeaderView(View view) {
        if (view == null) {
            return;
        }

        mHeaderViews.put(BASE_ITEM_TYPE_HEADER + getHeaderCount(), view);
    }

    /**
     * 添加多个FooterView
     *
     * @param views 需要添加的多个View
     */
    public void addFooterViews(View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            addFooterView(view);
        }
    }

    /**
     * 将一个View添加的FooterView
     *
     * @param view 需要添加的View
     */
    public void addFooterView(View view) {
        if (view == null) {
            return;
        }

        mFooterViews.put(BASE_ITEM_TYPE_FOOTER - getFooterCount(), view);
    }

    /**
     * 设置Item点击事件
     *
     * @param li Item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener<T> li) {
        mOnItemClickListener = li;
        if (mOnItemClickListener != null) {
            mOnItemClick = new OnItemClick();
        }
    }

    /**
     * 获取所有的HeaderView的数量
     *
     * @return HeaderView的个数
     */
    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取所有的FooterView的数量
     *
     * @return FooterView的个数
     */
    public int getFooterCount() {
        return mFooterViews.size();
    }

    /**
     * 在当前位置的视图是否是一个HeaderView
     *
     * @param position 当前视图的位置
     * @return true 是一个HeaderView / false 不是一个HeaderView
     */
    private boolean isHeaderViewPos(int position) {
        return position < getHeaderCount();
    }

    /**
     * 在当前位置的视图是否是一个FooterView
     *
     * @param position 当前视图的位置
     * @return true 是一个FooterView / false 不是一个FooterView
     */
    private boolean isFooterViewPos(int position) {
        return position >= getHeaderCount() + getDataSize();
    }

    /**
     * 获取当前位置的一个数据Item
     *
     * @param position 当前位置
     * @return 返回对应的数据
     */
    protected T getItem(int position) {
        return mData == null ? null : mData.get(position - getHeaderCount());
    }

    public Context getContext() {
        return mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return ViewHolder.createViewHolder(mContext, mHeaderViews.get(viewType));
        } else if (mFooterViews.get(viewType) != null) {
            return ViewHolder.createViewHolder(mContext, mFooterViews.get(viewType));
        }

        int layoutId = getLayoutId(viewType);
        return ViewHolder.createViewHolder(mInflater, parent, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            if (mHeaderViews.get(itemType) != null) {
                onHeaderViewBind(holder, position);
            }
            if (mFooterViews.get(itemType) != null) {
                onFooterViewBind(holder, position);
            }
            return;
        }

        // 设置点击操作
        View itemView = holder.itemView;
        if (mOnItemClickListener != null) {
            itemView.setOnClickListener(mOnItemClick);
            itemView.setTag(R.id.tag_click, holder);
        }

        T t = getItem(position);
        onBind(holder, t);
    }

    protected void onHeaderViewBind(ViewHolder holder, int position) {

    }

    protected void onFooterViewBind(ViewHolder holder, int position) {

    }

    private class OnItemClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == null || mOnItemClickListener == null) {
                return;
            }

            ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_click);
            if (holder == null) {
                return;
            }
            int position = holder.getLayoutPosition();
            mOnItemClickListener.onItemClick(v, getItem(position), position, v.getId());
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getDataSize();
    }

    public List<T> getData() {
        return mData;
    }

    public int getDataSize() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * @param data
     */
    public void addData(List<T> data) {
        mData = checkData(mData);
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * @param data
     */
    public void addData(T data) {
        mData = checkData(mData);
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * @param data
     */
    public void setData(List<T> data) {
        mData = checkData(mData);
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 设置的数据是否是空的
     *
     * @return true 没有设置数据或者设置的数据是空 / false 数据不为空
     */
    public boolean isEmpty() {
        return mData != null && mData.isEmpty();
    }

    private List<T> checkData(List<T> data) {
        return data == null ? new ArrayList<T>() : data;
    }

    /**
     * 将视图与数据进行绑定
     *
     * @param holder ViewHolder视图掌控器
     * @param data   当前需要绑定的数据
     */
    public abstract void onBind(ViewHolder holder, T data);

    /**
     * 获取当前类型item的布局ID
     *
     * @param viewType 对应布局的类型
     * @return 当前类型布局的ID，这个布局会被绘制为Item布局
     */
    public abstract int getLayoutId(int viewType);
}
