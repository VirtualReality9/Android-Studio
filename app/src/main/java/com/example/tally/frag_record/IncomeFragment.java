package com.example.tally.frag_record;


import com.example.tally.R;
import com.example.tally.db.DBManager;
import com.example.tally.db.TypeBean;

import java.util.List;

public class IncomeFragment extends BaseRecordFragment {

    @Override
    public void loadDataToGV() {
        super.loadDataToGV();
        //获取数据库当中的数据源
        List<TypeBean> inList = DBManager.getTypeList(1);
        typeList.addAll(inList);
        adapter.notifyDataSetChanged();
        typeTv.setText("其他");
        typeIv.setImageResource(R.mipmap.in_qt_fs);
    }

    @Override
    public void saveAccountToDB() {
        accountBean.setKind(1);
        DBManager.insertItemToAccounttb(accountBean);
    }
}