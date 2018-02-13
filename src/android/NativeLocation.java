package com.kit.cordova.nativeLocation;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NativeLocation extends CordovaPlugin {

  CallbackContext callbackContext = null;

  //声明AMapLocationClient类对象
  AMapLocationClient mLocationClient = null;

  //声明定位回调监听器
  AMapLocationListener mLocationListener = new AMapLocationListener() {
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
      if (aMapLocation != null) {
        if (aMapLocation.getErrorCode() == 0) {
          int locationType = aMapLocation.getLocationType();//获取当前定位结果来源 定位类型对照表: http://lbs.amap.com/api/android-location-sdk/guide/utilities/location-type/
          Double latitude = aMapLocation.getLatitude();//获取纬度
          Double longitude = aMapLocation.getLongitude();//获取经度
          JSONObject jo = new JSONObject();
          try {
            jo.put("locationType", locationType);
            jo.put("latitude", latitude);
            jo.put("longitude", longitude);
          } catch (JSONException e) {
            jo = null;
            e.printStackTrace();
          }
          Log.w("定位成功:", jo.toString());
          callbackContext.success(jo);
        } else {
          // 错误码对照表 http://lbs.amap.com/api/android-location-sdk/guide/utilities/errorcode/
          Log.e("定位失败错误码:", aMapLocation.getAdCode());
          Log.e("定位失败信息:", aMapLocation.getErrorInfo());
          callbackContext.error(aMapLocation.getErrorCode());
        }
      }
    }
  };

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Context context = this.cordova.getActivity().getApplicationContext();
    mLocationClient = new AMapLocationClient(context);
    mLocationClient.setLocationListener(mLocationListener);
    AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
    mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);// 使用签到定位场景
    mLocationClient.setLocationOption(mLocationOption); // 设置定位参数
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    if (action.equals("getLocation")) {
      // 设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
      mLocationClient.stopLocation();
      mLocationClient.startLocation(); // 启动定位
      PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
      r.setKeepCallback(true);
      callbackContext.sendPluginResult(r);
      return true;
    }
    return false;
  }

}
