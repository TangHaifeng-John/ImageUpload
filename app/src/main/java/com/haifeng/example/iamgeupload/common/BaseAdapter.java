package com.haifeng.example.iamgeupload.common;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haifeng.example.iamgeupload.ui.ItemClickListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author haifeng
 */
public abstract class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    private static final java.lang.String TAG = BaseAdapter.class.getSimpleName();
    /**
     * 标识是否显示EmptyView
     */
    protected boolean isShowEmptyView = false;
    protected List<T> datas;
    protected Context context;
    private ItemClickListener<T> itemClickListener;

    public void setItemClickListener(ItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public BaseAdapter(Context context) {
        this(context, null);
    }

    public BaseAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
        if (datas == null) {
            this.datas = new ArrayList<>();
        }
    }


    protected Context getContext() {
        return context;
    }

    public void removeItem(T t) {
        removeItem(datas.indexOf(t));
    }

    public void removeItem(int position) {
        if (datas != null && datas.size() > position) {
            datas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateData(List<T> list) {
        datas.clear();
        datas.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        datas.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(T t) {
        datas.add(t);
        notifyDataSetChanged();
    }

    public void addItemToFirst(T t) {
        datas.add(0, t);
        notifyDataSetChanged();
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setData(List<T> datas) {
        this.datas = datas;
        if (this.datas == null) {
            this.datas = new ArrayList<>();
            isShowEmptyView = true;
        } else {
            isShowEmptyView = false;
        }
        notifyDataSetChanged();
    }

    protected View createItemView(ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(getItemViewLayout(), viewGroup, false);
    }


    protected Class getHolderClass() {
        return null;
    }

    public T getItem(int position) {
        if (datas != null && datas.size() > position) {
            return datas.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public V onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (getHolderClass() != null) {
            try {
                Constructor c = getHolderClass().getConstructor(View.class);

                V v = (V) c.newInstance(createItemView(parent));
                return v;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected abstract int getItemViewLayout();

    public abstract void convert(int position, V holder, T t);

    @Override
    public void onBindViewHolder(V holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClick(position, getItem(position));
                }
            }
        });
        convert(position, holder, getItem(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
