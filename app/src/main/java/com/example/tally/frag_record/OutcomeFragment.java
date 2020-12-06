package com.example.tally.frag_record;

import com.example.tally.R;
import com.example.tally.db.DBManager;
import com.example.tally.db.TypeBean;

import java.util.List;

public class OutcomeFragment extends BaseRecordFragment{

    //重写
    @Override
    public void loadDataToGV() {
        super.loadDataToGV();
        //获取数据库当中的数据源
        List<TypeBean> outList = DBManager.getTypeList(0);
        typeList.addAll(outList);
        adapter.notifyDataSetChanged();
        typeTv.setText("其他");
        typeIv.setImageResource(R.mipmap.ic_qita_fs);
    }

    @Override
    public void saveAccountToDB() {
        accountBean.setKind(0);
        DBManager.insertItemToAccounttb(accountBean);
    }
}
