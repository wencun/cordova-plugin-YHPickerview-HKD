package com.picker.yhck;

import android.content.Intent;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YHPickerViewPlugin extends CordovaPlugin {

  private CallbackContext mCallbackContext;//回调数据
  private static final int MSG_REQUEST_CODE = 1000;
  private static final int MSG_RESULT_CODE_ONE = 1001;//一级PickerVeiw
  private static final int MSG_RESULT_CODE_TWO = 1002;//二级PickerView
  private String getParam;//获取数据

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.getParam = "";
    this.mCallbackContext = callbackContext;

    if (!"".equals(action) || action != null) {
      getParam = args.getJSONObject(0).toString();
      openActivity();
      return true;
    }
    mCallbackContext.error("error");
    return false;
  }

  private void openActivity() {
    Intent intent = new Intent(cordova.getActivity(), ShowActivity.class);
    intent.putExtra("PARAM_JSON", getParam);
    cordova.startActivityForResult(this, intent, MSG_REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == MSG_REQUEST_CODE && resultCode == MSG_RESULT_CODE_TWO) {
      String selectProvinceName = intent.getStringExtra("resultP");
      String selectProvinceCode = intent.getStringExtra("resultPC");
      String selectCityName = intent.getStringExtra("resultC");
      String selectCityCode = intent.getStringExtra("resultCC");
      String selectAreaName = intent.getStringExtra("resultA");
      String selectAreaCode = intent.getStringExtra("resultAC");
      JSONObject resultJson = new JSONObject();
      try {
        JSONArray nameArray = new JSONArray();
        nameArray.put(selectProvinceName);
        nameArray.put(selectCityName);
        nameArray.put(selectAreaName);
        JSONArray codeArray = new JSONArray();
        codeArray.put(selectProvinceCode);
        codeArray.put(selectCityCode);
        codeArray.put(selectAreaCode);
        resultJson.put("selectedTextArray", nameArray);
        resultJson.put("selectedIDArray", codeArray);

      } catch (JSONException e) {
        e.printStackTrace();
      }
      mCallbackContext.success(resultJson);
    } else if (requestCode == MSG_REQUEST_CODE && resultCode == MSG_RESULT_CODE_ONE) {
      String selectName = intent.getStringExtra("selectName");
      String selectCode = intent.getStringExtra("selectCode");
      JSONObject resultJson = new JSONObject();
      try {
        JSONArray nameArray = new JSONArray();
        nameArray.put(selectName);
        JSONArray codeArray = new JSONArray();
        codeArray.put(selectCode);
        resultJson.put("selectedTextArray", nameArray);
        resultJson.put("selectedIDArray", codeArray);

      } catch (JSONException e) {
        e.printStackTrace();
      }
      mCallbackContext.success(resultJson);
    }
  }

  private String setJobData(String resultA, String resultB, String resultC) {
    String JonType = "";
    List<String> arr = new ArrayList<String>();
    if (StringUtils.isNotBlank(resultA)) {
      arr.add(resultA.trim());
    }
    if (StringUtils.isNotBlank(resultB)) {
      arr.add(resultB.trim());
    }
    if (StringUtils.isNotBlank(resultC)) {
      arr.add(resultC.trim());
    }
    JonType = StringUtils.join(arr, ",");
    return JonType;
  }
}
