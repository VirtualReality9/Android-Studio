package com.example.tally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tally.adapter.AccountAdapter;
import com.example.tally.db.AccountBean;
import com.example.tally.db.DBManager;
import com.example.tally.utils.BudgetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView todayLv; // 展示今日收支情况的ListView
    ImageView searchIv;
    Button editBtn;
    ImageButton moreBtn;

    // 声明数据源
    List<AccountBean> mDatas;
    AccountAdapter adapter;
    int year,month,day;
    // 头布局相关控件
    View headerView;
    TextView topOutTv,topInTv,topbudgetTv,topConTv;
    ImageView topShowIv;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime();
        initView();
        preferences = getSharedPreferences("budget", Context.MODE_PRIVATE);
        // 添加ListView的头布局
        addLVHeaderView();
        mDatas = new ArrayList<>();
        // 设置适配器：加载每一行数据到列表当中
        adapter = new AccountAdapter(this, mDatas);
        todayLv.setAdapter(adapter);
    }

    /*初始化自带的View的方法*/
    private void initView() {
        todayLv = findViewById(R.id.main_lv);
        editBtn = findViewById(R.id.main_btn_edit);
        moreBtn = findViewById(R.id.main_btn_more);
        searchIv = findViewById(R.id.main_iv_search);
        editBtn.setOnClickListener(this);
        moreBtn.setOnClickListener(this);
        searchIv.setOnClickListener(this);
    }

    /*
    * 给ListView添加头布局的方法
    * */
    private void addLVHeaderView() {
        // 将布局转换成View对象
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        // 查找头布局可用控件
        topOutTv = headerView.findViewById(R.id.item_mainlv_top_tv_out);
        topInTv = headerView.findViewById(R.id.item_main_lv_top_tv_in);
        topbudgetTv = headerView.findViewById(R.id.item_mainlv_top_tv_budget);
        topConTv = headerView.findViewById(R.id.item_mainlv_top_tv_day);
        topShowIv = headerView.findViewById(R.id.item_mainlv_top_iv_hide);

        topbudgetTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
        topShowIv.setOnClickListener(this);
    }
    /*
    * 获取今日的具体时间
    * */
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    // 当activity获取焦点时，会调用的方法
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();
    }

    /*
    * 设置头布局当中文本内容的显示
    * */
    private void setTopTvShow() {
        // 获取今日支出和收入总金额，显示在view当中
        float incomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 1);
        float outcomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 0);
        String infoOneDay = "今日支出 ￥"+outcomeOneDay+"  收入 ￥"+incomeOneDay;
        topConTv.setText(infoOneDay);

        // 获取本月收入和支出总金额
        float incomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
        topInTv.setText("￥"+incomeOneMonth);
        topOutTv.setText("￥"+outcomeOneMonth);

        // 设置显示预算剩余
        float bmoney = preferences.getFloat("bmoney", 0); // 预算
        if (bmoney == 0) {
            topbudgetTv.setText("￥ 0");
        }else {
            float syMoney = bmoney - outcomeOneMonth;
            topbudgetTv.setText("￥"+syMoney);
        }
    }

    private void loadDBData() {
        List<AccountBean> list = DBManager.getAccountListOneDayFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_iv_search:

                break;
            case R.id.main_btn_edit:
                Intent it1 = new Intent(this, RecordActivity.class);//跳转界面
                startActivity(it1);
                break;
            case R.id.main_btn_more:

                break;
            case R.id.item_mainlv_top_tv_budget:
                showBudgetDialog();
                break;
            case R.id.item_mainlv_top_iv_hide:
                // 切换TextView明文和密文
                toggleShow();
                break;
        }
        if (view == headerView) {
            // 头布局被点击了
        }
    }

    /*
    * 显示预算设置对话框
    * */
    private void showBudgetDialog() {
        BudgetDialog dialog = new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                // 将预算金额写入到共享参数当中，进行存储
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("bmoney",money);
                editor.commit();

                //计算剩余金额
                float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);

                float syMoney = money - outcomeOneMonth; // 预算剩余 = 预算 - 支出
                topbudgetTv.setText("￥"+syMoney);
            }
        });

    }

    boolean isShow = true;
    /*
    * 点击头布局眼睛时，如果原来时明文，就加密，如果是密文，就显示出来
    * */
    private void toggleShow() {
        if (isShow) { // 明文 ---> 密文
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            topInTv.setTransformationMethod(passwordMethod); // 密码转换方法，可以设置隐藏
            topOutTv.setTransformationMethod(passwordMethod); // 密码转换方法，可以设置隐藏
            topbudgetTv.setTransformationMethod(passwordMethod); // 密码转换方法，可以设置隐藏
            topShowIv.setImageResource(R.mipmap.ih_hide); // 密码转换方法，可以设置隐藏
            isShow = false; // 设置标志位为隐藏状态
        }else { // 密文 ---> 明文
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            topInTv.setTransformationMethod(hideMethod); // 密码转换方法，可以设置隐藏
            topOutTv.setTransformationMethod(hideMethod); // 密码转换方法，可以设置隐藏
            topbudgetTv.setTransformationMethod(hideMethod); // 密码转换方法，可以设置隐藏
            topShowIv.setImageResource(R.mipmap.ih_show); // 密码转换方法，可以设置隐藏
            isShow = true; // 设置标志位为显示状态
        }
    }
}