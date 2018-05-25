package com.picker.yhck.bean;



import com.picker.yhck.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * TODO<json数据源>
 * 读取Json文件的工具类
 * Created by YH_CK on 2017/04/26.
 */

public class JsonBean implements IPickerViewData {


    /**
     * name : 省份
     * code :省code
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */

    private String name;
    private String code;
    private List<CityBean> city;

    public String getName() {
        return name;
    }
    public String getCode() {
    return code;
  }

    public void setName(String name) {
        this.name = name;
    }
    public void setCode(String code) {
    this.code = code;
  }

    public List<CityBean> getCityList() {
        return city;
    }

    public void setCityList(List<CityBean> city) {
        this.city = city;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }



    public static class CityBean {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */

        private String name;
        private String code;
        private List<String> area;

        public String getName() {
            return name;
        }
        public String getCode() {
        return code;
      }
        public void setName(String name) {
            this.name = name;
        }

        public List<String> getArea() {
            return area;
        }
        public void setCode(String code) {
        this.code = code;
      }
        public void setArea(List<String> area) {
            this.area = area;
        }
    }
}
