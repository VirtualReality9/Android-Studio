package com.example.tally.frag_record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tally.R;
import com.example.tally.db.TypeBean;

import java.util.List;

public class TypeBaseAdapter extends BaseAdapter {
    Context context;
    List<TypeBean> mData;
    int selectPos = 0;//选中位置
    public TypeBaseAdapter(Context context, List<TypeBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
//此适配器不考虑复用问题，因为所有的item都显示在界面上，不会因为滑动就消失
//所以没有剩余的view，所以不用复写
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_recordfrag_gv,viewGroup,false);
        //查找布局中的控件
        ImageView iv = view.findViewById(R.id.item_recordfrag_iv);
        TextView tv = view.findViewById(R.id.item_recordfrag_tv);
        //获取指定位置的数据源
        TypeBean typeBean = mData.get(i);
        tv.setText(typeBean.getTypename());
        //判断当前位置是否为选中位置，如果是设置带颜色图片，否则灰色图片
        if (selectPos==i) {
            iv.setImageResource(typeBean.getSimageId());
        }else{
            iv.setImageResource(typeBean.getImageId());
        }

        return view;
    }
}
