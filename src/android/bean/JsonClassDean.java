package com.picker.yhck.bean;


import com.picker.yhck.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by YH_CK on 2017/04/28.
 */

public class JsonClassDean implements IPickerViewData {

    private String labelName;
    private String labelCode;
    private List<TypeBean> optionsList;

  public String getLabelName() {
    return labelName;
  }

  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }

  public String getLabelCode() {
    return labelCode;
  }

  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }

  public List<TypeBean> getOptionsList() {
    return optionsList;
  }

  public void setOptionsList(List<TypeBean> optionsList) {
    this.optionsList = optionsList;
  }

  @Override
    public String getPickerViewText() {
        return this.labelName;
    }


    public static class TypeBean {
        private String labelName;
        private String labelCode;

      public String getLabelName() {
        return labelName;
      }

      public void setLabelName(String labelName) {
        this.labelName = labelName;
      }

      public String getLabelCode() {
        return labelCode;
      }

      public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
      }
    }
}
