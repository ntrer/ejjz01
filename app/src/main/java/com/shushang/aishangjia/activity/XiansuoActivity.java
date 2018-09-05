package com.shushang.aishangjia.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.shushang.aishangjia.Bean.NewPeople;
import com.shushang.aishangjia.Bean.Progress;
import com.shushang.aishangjia.Bean.Response;
import com.shushang.aishangjia.R;
import com.shushang.aishangjia.base.BaseActivity;
import com.shushang.aishangjia.base.BaseUrl;
import com.shushang.aishangjia.ui.ExtAlertDialog;
import com.shushang.aishangjia.ui.GenderDialog;
import com.shushang.aishangjia.utils.Json.JSONUtil;
import com.shushang.aishangjia.utils.OkhttpUtils.CallBackUtil;
import com.shushang.aishangjia.utils.OkhttpUtils.OkhttpUtil;
import com.xys.libzxing.zxing.utils.PreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

public class XiansuoActivity extends BaseActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private LinearLayout mLlCustomerGender,mllDecorateProgress;
    private EditText mEtCustomerName;//客户姓名
    private EditText mEtCustomerMobile;//客户电话
    private TextView mTvCustomerGender;//客户性别
    private EditText mEtXiaoQu;//小区名称
    private TextView mEtDecorateStyle;//销售线索信息
    private Button mButton,mButton2;
    private Toolbar mToolbar;
    private RelativeLayout rl_activity;
    private EditText mTvIntentionToPurchaseProduct;//本次沟通时间
    private TextView mNextTime;//下次沟通时间
    private EditText mXiansuoThink;//本次次线索意向
    private TextView mBiezhu;//本次备注信息
    private TextView mTvActivity;
    private GenderDialog mGenderDialog;
    private String token_id = null;
    private String activityId, activityName, activityCode;
    private String username, yzmCode,phone, sex, address,xiaoqu, decorationProgress, decorationStyle, thinkBuyGood,activity ,info;
    private RadioGroup mRadioButton;
    private long lastClick;
    private static final int REQUEST_CODE_CITY = 2010;
    private static final int REQUEST_CODE_ACTIVITY = 2011;
    private static final int REQUEST_PROGRESS_ACTIVITY = 2012;
    private static final int REQUEST_INFO_ACTIVITY = 2013;
    private static final int REQUEST_BEIZHU_ACTIVITY = 2014;
    private static final int REQUEST_NEXT_BEIZHU_ACTIVITY = 2015;
    private Dialog mRequestDialog;
    private boolean isVisiable=false;
    private  List<Progress.DataListBean> mDataListBeen;
    private String progress=null;
    private String select_year,select_mounth,select_day;
    private int now_year,now_mounth,now_day;
    @Override
    public int setLayout() {
        return R.layout.activity_xiansuo;
    }


    @Override
    public void init() {
        mEtCustomerName = (EditText) findViewById(R.id.et_customer_name);
        mEtCustomerMobile = (EditText) findViewById(R.id.et_customer_mobile);
        mLlCustomerGender = (LinearLayout) findViewById(R.id.ll_customer_gender);
        mTvCustomerGender = (TextView) findViewById(R.id.tv_customer_gender);
        mEtDecorateStyle = (TextView) findViewById(R.id.et_decorate_style);
        mNextTime= (TextView) findViewById(R.id.next_time);
        mBiezhu= (TextView) findViewById(R.id.now_beizhu);
        mXiansuoThink= (EditText) findViewById(R.id.et_xiansuo_think);
        mEtXiaoQu= (EditText) findViewById(R.id.et_customer_xiaoqu);
        mllDecorateProgress= (LinearLayout) findViewById(R.id.ll_DecorateProgress);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRequestDialog = ExtAlertDialog.creatRequestDialog(this, getString(R.string.submit));
        Intent data=getIntent();
        NewPeople.DataListBean dataListBean= (NewPeople.DataListBean) data.getSerializableExtra("data");

        //设置支持toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButton = (Button) findViewById(R.id.btn_submit);
        token_id = PreferencesUtils.getString(this, "token_id");
        mTvIntentionToPurchaseProduct = (EditText) findViewById(R.id.et_intention_to_purchase_product);
        mGenderDialog = new GenderDialog(this);
        mLlCustomerGender.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mGenderDialog.setListener(new GenderDialog.OnGenderDialogListener() {
            @Override
            public void onGenderDialogClick(String itemName) {
                mTvCustomerGender.setText(itemName);
            }
        });

        mNextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                       XiansuoActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpd.setAutoHighlight(true);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mEtDecorateStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(XiansuoActivity.this,InfoActivity.class),REQUEST_INFO_ACTIVITY);
            }
        });


        mBiezhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(XiansuoActivity.this,InfoActivity2.class),REQUEST_BEIZHU_ACTIVITY);
            }
        });
        Calendar c = Calendar.getInstance();//
        now_year = c.get(Calendar.YEAR); // 获取当前年份
        now_mounth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        now_day = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Log.d("mTvIntention",String.valueOf(System.currentTimeMillis()));
        mTvIntentionToPurchaseProduct.setText(simpleDateFormat.format(date));

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_customer_gender://客户性别
                if (!mGenderDialog.isShowing())
                    mGenderDialog.show();
                break;
            case R.id.tv_customer_address:
                startActivityForResult(new Intent(this, CityActivity.class), REQUEST_CODE_CITY);
                break;
            case R.id.btn_submit:
                username = mEtCustomerName.getText().toString().replace(" ", "");
                phone = mEtCustomerMobile.getText().toString().replace(" ", "");
               if(mTvCustomerGender.getText().toString().replace(" ", "").equals("男")){
                   sex="1";
               }else {
                   sex="2";
               }
                xiaoqu=mEtXiaoQu.getText().toString().replace(" ", "");
                decorationStyle=mEtDecorateStyle.getText().toString().replace(" ", "");
                thinkBuyGood=mTvIntentionToPurchaseProduct.getText().toString().replace(" ", "");
               if(isVisiable){
                   if(username.equals("")||phone.equals("")||sex.equals("")||xiaoqu.equals("")||decorationProgress.equals("")||thinkBuyGood.equals("")){
                       Toast.makeText(this, "必填项不能为空", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       mRequestDialog.show();
                       submit(username, phone, sex, xiaoqu,decorationStyle, decorationProgress,thinkBuyGood);
                   }
               }
               else {
                   if(username.equals("")||phone.equals("")||sex.equals("")||xiaoqu.equals("")||decorationProgress.equals("")||thinkBuyGood.equals("")){
                       Toast.makeText(this, "必填项不能为空", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       mRequestDialog.show();
                       submit(username, phone, sex, xiaoqu,decorationStyle, decorationProgress,thinkBuyGood);
                   }
               }

                break;
        }
    }


    public void submit(String username, String phone, String sex,String xiaoqu, String decorationStyle, String decorationProgress, String thinkBuyGood) {
        String url =  BaseUrl.BASE_URL + "phoneApi/customerManager.do?method=saveCustomer";
        Log.d("创建活动", yzmCode);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("token_id", token_id);
        paramsMap.put("username", username);
        paramsMap.put("address", xiaoqu);
        paramsMap.put("decorationStyle", decorationStyle);
        paramsMap.put("thinkBuyGood", thinkBuyGood);
        paramsMap.put("phone", phone);
        paramsMap.put("sex", sex);
        paramsMap.put("decorationProgress", decorationProgress);
        OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                if(mRequestDialog!=null&&mRequestDialog.isShowing()){
                    mRequestDialog.dismiss();
                }
            }

            @Override
            public void onResponse(String response) {
                Log.d("创建活动",response);
                if(response!=null){
                    Response response1 = JSONUtil.fromJson(response, Response.class);
                    if(response1.getRet().equals("200")){
                        if(mRequestDialog!=null&&mRequestDialog.isShowing()){
                            mRequestDialog.dismiss();
                        }
                        Toast.makeText(XiansuoActivity.this, "成功", Toast.LENGTH_SHORT).show();
                        mEtCustomerName.setText("");
                        mEtCustomerMobile.setText("");
                        mEtDecorateStyle.setText("");
                        mEtXiaoQu.setText("");
                        mTvActivity.setText("");
                        mTvCustomerGender.setText("");
                        mTvIntentionToPurchaseProduct.setText("");

                    }
                    else if(response1.getRet().equals("201")){
                        if(mRequestDialog!=null&&mRequestDialog.isShowing()){
                            mRequestDialog.dismiss();
                        }
                        Toast.makeText(XiansuoActivity.this, ""+response1.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INFO_ACTIVITY) {
            if(PreferencesUtils.getString(XiansuoActivity.this, "info")==null){
                mEtDecorateStyle.setText("");
            }else {
                info= PreferencesUtils.getString(getApplicationContext(),"info");
                mEtDecorateStyle.setText(info);
            }
        } else if (requestCode == REQUEST_BEIZHU_ACTIVITY) {
            if (PreferencesUtils.getString(XiansuoActivity.this, "beizhu")==null) {
                mBiezhu.setText("");
            }
            else {
                info= PreferencesUtils.getString(getApplicationContext(),"beizhu");
                mBiezhu.setText(info);
            }
        }
    }


    /**
     * 退出activity的动画效果不起作用，要在java代码里写
     * 复写activity的finish方法，在overridePendingTransition中写入自己的动画效果
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        select_year=year+"";
        select_mounth=monthOfYear+1+"";
        select_day=dayOfMonth+"";
        if(Integer.parseInt(select_year)<now_year){
            ToastUtils.showLong("请选择当前时间以后的日期");
        }
        else if(Integer.parseInt(select_mounth)<now_mounth){
            ToastUtils.showLong("请选择当前时间以后的日期");
        }
        else if(Integer.parseInt(select_mounth)==now_mounth&&Integer.parseInt(select_day)<now_day){
            ToastUtils.showLong("请选择当前时间以后的日期");
        }
        else {
            Calendar now = Calendar.getInstance();
            TimePickerDialog tpd = TimePickerDialog.newInstance(
                    XiansuoActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
            tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
            tpd.show(getFragmentManager(), "Timepickerdialog");
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time=select_year+"-"+select_mounth+"-"+select_day+" "+hourString+":"+minuteString+":00";
        mNextTime.setText(time);
    }
}
