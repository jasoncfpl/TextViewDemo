package com.custom.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private Resources mResources;
    private View mConvertView;

    public ViewHolder(Context context, View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
        mResources = context.getResources();
    }

    public static ViewHolder createViewHolder(Context context, View view) {
        if (context == null) {
            return null;
        }
        return new ViewHolder(context, view);
    }

    public static ViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
        if (context == null) {
            return null;
        }
        return createViewHolder(LayoutInflater.from(context), parent, layoutId);
    }

    public static ViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        if (inflater == null) {
            return null;
        }
        View itemView = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(inflater.getContext(), itemView);
    }

    public View getConvertView() {
        return mConvertView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public ViewHolder setText(int viewId, int textRes) {
        TextView textView = getView(viewId);
        String text = mResources.getString(textRes);
        textView.setText(text);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView textView = getView(viewId);
        textView.setTextColor(textColor);
        return this;
    }

    public ViewHolder setTextColorResource(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        view.setTextColor(mResources.getColor(textColorRes));
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView imageView = getView(viewId);
        imageView.setImageDrawable(drawable);
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public ViewHolder setBackgroundResource(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public ViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    public ViewHolder setRating(int viewId, float rating) {
        RatingBar ratingBar = getView(viewId);
        ratingBar.setRating(rating);
        return this;
    }

    public ViewHolder setOnClickListener(int viewId, View.OnClickListener li) {
        View view = getView(viewId);
        view.setOnClickListener(li);
        return this;
    }

    public ViewHolder setOnTouchListener(int viewId, View.OnTouchListener li) {
        View view = getView(viewId);
        view.setOnTouchListener(li);
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener li) {
        View view = getView(viewId);
        view.setOnLongClickListener(li);
        return this;
    }

    public ViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }
}
