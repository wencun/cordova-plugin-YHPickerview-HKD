package com.picker.yhck;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.picker.yhck.bean.GetJsonDataUtil;
import com.picker.yhck.bean.JsonBean;
import com.picker.yhck.bean.JsonClassDean;
import com.picker.yhck.pickerview.OptionsPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.yonghui.huikaidian.R;

public class ShowActivity extends Activity {

  //一级数据列表
  private ArrayList<String> optionItems = new ArrayList<String>();
  private ArrayList<String> optionItemsCode = new ArrayList<String>();

  //三级城市列表
  private ArrayList<JsonBean> options1Items = new ArrayList<JsonBean>();
  private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
  private ArrayList<ArrayList<String>> options2CodeItems = new ArrayList<ArrayList<String>>();
  private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<ArrayList<ArrayList<String>>>();
  private ArrayList<ArrayList<ArrayList<String>>> options3CodeItems = new ArrayList<ArrayList<ArrayList<String>>>();
  //二级列表
  private ArrayList<JsonClassDean> classItems = new ArrayList<JsonClassDean>();
  private ArrayList<ArrayList<String>> typeListItems = new ArrayList<ArrayList<String>>();

  private static final int MSG_LOAD_SUCCESS = 0x0002;
  private static final int MSG_LOAD_FAILED = 0x0003;
  private static final int MSG_LOAD_SUCCESS_SECOND_LEVEL = 0x00047;
  private String mParamJson;
  private String mInfoString;
  private String mOptionsIndex;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pickerview_activity_main);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = this.getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.BLACK);
    }
    getParamJsonData();
  }

  private void getParamJsonData() {
    mParamJson = getIntent().getStringExtra("PARAM_JSON");
    try {
      JSONObject jsonObject = new JSONObject(mParamJson);
      mInfoString = jsonObject.getString("pickerId");
      mOptionsIndex=jsonObject.getString("depth");
      JSONArray jsonArray = jsonObject.getJSONArray("options");
      openPickerView(mInfoString, jsonArray);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void openPickerView(String pickerid, JSONArray jsonArray) {
    if ("picker3".equals(pickerid)) {
      initCityJsonData();
    } else if ("picker8".equals(pickerid)) {
      initSecondPickerData(jsonArray);
    } else {
      initOnePickerJsonData(jsonArray);
    }
  }

  //解析一级PickerView的数据
  private void initOnePickerJsonData(JSONArray jsonArray) {
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject arrayItem = null;
      try {
        arrayItem = (JSONObject) jsonArray.get(i);
        String name = arrayItem.getString("labelName");
        String code = arrayItem.getString("labelCode");
        optionItems.add(name);
        optionItemsCode.add(code);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    showOnePicker();
  }

  //显示一级PickerView
  private void showOnePicker() {
    OptionsPickerView pvOptions = new OptionsPickerView.Builder(ShowActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
      @Override
      public void onOptionsSelect(int options1, int options2, int options3, View v) {
        //返回选中位置
        String txName = optionItems.get(options1);
        String txCode = optionItemsCode.get(options1);
        Intent intent = new Intent();
        intent.putExtra("selectName", txName);
        intent.putExtra("selectCode", txCode);
        setResult(1001, intent);
        ShowActivity.this.finish();
      }
    })

      .setTitleText("")
      .setDividerColor(Color.BLACK)
      .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
      .setContentTextSize(20)
      .build();

    pvOptions.setPicker(optionItems);//一级选择器
    pvOptions.show();
  }

  //解析二级PickerView的数据
  private void initSecondPickerData(JSONArray dataList) {
    try {
      ArrayList<JsonClassDean> dataArrayList = new ArrayList<JsonClassDean>();
      for (int i = 0; i < dataList.length(); i++) {
        JSONObject jsonObject = (JSONObject) dataList.get(i);
        JsonClassDean jsonBean = new JsonClassDean();
        jsonBean.setLabelName(jsonObject.getString("labelName"));
        jsonBean.setLabelCode(jsonObject.getString("labelCode"));
        JSONArray typeArray = jsonObject.getJSONArray("options");

        ArrayList<JsonClassDean.TypeBean> cityList = new ArrayList<JsonClassDean.TypeBean>();
        ArrayList<String> typeNameList = new ArrayList<String>();//列表（第二级）
        for (int j = 0; j < typeArray.length(); j++) {
          JSONObject cityJson = (JSONObject) typeArray.get(j);
          JsonClassDean.TypeBean jsonCityBean = new JsonClassDean.TypeBean();
          jsonCityBean.setLabelName(cityJson.getString("labelName"));
          typeNameList.add(jsonCityBean.getLabelName());
          jsonCityBean.setLabelCode(cityJson.getString("labelCode"));
          cityList.add(jsonCityBean);
        }
        typeListItems.add(typeNameList);
        jsonBean.setOptionsList(cityList);
        dataArrayList.add(jsonBean);
      }
      classItems = dataArrayList;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS_SECOND_LEVEL);
  }

  private void ShowTowPickerView() {
    OptionsPickerView pvOptions = new OptionsPickerView.Builder(ShowActivity.this, new OptionsPickerView.OnOptionsSelectListener() {

      @Override
      public void onOptionsSelect(int options1, int options2, int options3, View view) {
        //返回的分别是三个级别的选中位置
        Intent intent = new Intent();
        intent.putExtra("resultP", classItems.get(options1).getLabelName());
        intent.putExtra("resultPC", classItems.get(options1).getLabelCode());
        intent.putExtra("resultC", classItems.get(options1).getOptionsList().get(options2).getLabelName());
        intent.putExtra("resultCC", classItems.get(options1).getOptionsList().get(options2).getLabelCode());
        setResult(1002, intent);
        ShowActivity.this.finish();
      }

      ;
    })
      .setTitleText("")
      .setDividerColor(Color.BLACK)
      .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
      .setContentTextSize(20)
      .setOutSideCancelable(true)// default is true
      .build();

    pvOptions.setPicker(classItems, typeListItems);//二级选择器
    pvOptions.show();
  }

  //解析城市数据
  private void initCityJsonData() {
    String JsonData = new GetJsonDataUtil().getJson(ShowActivity.this, "province.json");
    ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
    options1Items = jsonBean;
    for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
      ArrayList<String> CityList = new ArrayList<String>();//该省的城市列表（第二级）
      ArrayList<String> CityCodeList = new ArrayList<String>();//该省的城市Code列表（第二级）
      ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<ArrayList<String>>();//该省的所有地区列表（第三极）
      ArrayList<ArrayList<String>> Province_AreaCodeList = new ArrayList<ArrayList<String>>();//该省的所有地区Code列表（第三极）

      for (int c = 0; c < jsonBean.get(i).getOptions().size(); c++) {//遍历该省份的所有城市
        String CityName = jsonBean.get(i).getOptions().get(c).getLabelName();
        String CityCode = jsonBean.get(i).getOptions().get(c).getLabelCode();
        //添加城市数据
        CityList.add(CityName);
        CityCodeList.add(CityCode);
        ArrayList<String> City_AreaList = new ArrayList<String>();//该城市的所有地区列表
        ArrayList<String> City_AreaCodeList = new ArrayList<String>();//该城市的所有地区列表
        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
        if (jsonBean.get(i).getOptions().get(c).getOptions() == null || jsonBean.get(i).getOptions().get(c).getOptions().size() == 0) {
          City_AreaList.add("");
          City_AreaCodeList.add("");
        } else {
          for (int d = 0; d < jsonBean.get(i).getOptions().get(c).getOptions().size(); d++) {//该城市对应地区所有数据
            String AreaName = jsonBean.get(i).getOptions().get(c).getOptions().get(d).getLabelName();
            String AreaCode = jsonBean.get(i).getOptions().get(c).getOptions().get(d).getLabelCode();
            //添加该城市所有地区数据
            City_AreaList.add(AreaName);
            City_AreaCodeList.add(AreaCode);
          }
        }
        //添加该省所有地区数据
        Province_AreaList.add(City_AreaList);
        Province_AreaCodeList.add(City_AreaCodeList);
      }

      /**
       * 添加城市数据
       */
      options2Items.add(CityList);
      options2CodeItems.add(CityCodeList);

      /**
       * 添加地区数据
       */
      options3Items.add(Province_AreaList);
      options3CodeItems.add(Province_AreaCodeList);
    }
    mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
  }

  //Gson 解析
  public ArrayList<JsonBean> parseData(String result) {
    ArrayList<JsonBean> detail = new ArrayList<JsonBean>();
    try {
      JSONArray data = new JSONArray(result);
      for (int i = 0; i < data.length(); i++) {
        Gson gson = new Gson();
        JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
        detail.add(entity);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return detail;
  }

  //显示城市选择
  private void ShowPickerView() {
    OptionsPickerView pvOptions = new OptionsPickerView.Builder(ShowActivity.this, new OptionsPickerView.OnOptionsSelectListener() {

      @Override
      public void onOptionsSelect(int options1, int options2, int options3, View view) {
        //返回的分别是三个级别的选中位置
        Intent intent = new Intent();
        intent.putExtra("resultP", options1Items.get(options1).getLabelName());
        intent.putExtra("resultPC", options1Items.get(options1).getLabelCode());
        intent.putExtra("resultC", options2Items.get(options1).get(options2));
        intent.putExtra("resultCC", options2CodeItems.get(options1).get(options2));
        intent.putExtra("resultA",options3Items.get(options1).get(options2).get(options3));
        intent.putExtra("resultAC",options3CodeItems.get(options1).get(options2).get(options3));
        setResult(1002, intent);
        ShowActivity.this.finish();
      }

      ;
    })
      .setTitleText("")
      .setDividerColor(Color.BLACK)
      .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
      .setContentTextSize(20)
      .setOutSideCancelable(true)// default is true
      .build();

        //pvOptions.setPicker(options1Items);一级选择器
    if(mOptionsIndex.equals("3")){
      pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
    }else {
      pvOptions.setPicker(options1Items, options2Items);//二级选择器
    }
    pvOptions.show();
  }

  //Gson 解析
  public ArrayList<JsonClassDean> parseJobData(String result) {
    ArrayList<JsonClassDean> detail = new ArrayList<JsonClassDean>();
    try {
      JSONArray data = new JSONArray(result);
      Gson gson = new Gson();
      for (int i = 0; i < data.length(); i++) {
        JsonClassDean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonClassDean.class);
        detail.add(entity);
      }
    } catch (Exception e) {
      e.printStackTrace();
      mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
    }
    return detail;
  }

  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_LOAD_SUCCESS:
          ShowPickerView();
          break;
        case MSG_LOAD_SUCCESS_SECOND_LEVEL:
          ShowTowPickerView();
          break;
        case MSG_LOAD_FAILED:
          Toast.makeText(ShowActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
          break;
      }
    }
  };
}
