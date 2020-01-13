package com.aap.medicore.Utils;


import com.aap.medicore.Adapters.AdapterTabs;
import com.aap.medicore.Models.TabsModel;
import com.aap.medicore.Models.TaskList;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rabia on 30/5/2019.
 */

public class SettingValues {

    private static ArrayList<TabsModel> tabList = new ArrayList<>();
    private static AdapterTabs adapterTabs;
    private static String taskId="";
    private static String jsonval ="";
    private static String barcodeVal="";
    public  static ArrayList<String> barcodelist = new ArrayList<>();

    private static String formId="";
    private static TaskList taskList;
    private static File path;
    public static TaskList getTaskList() {
        return taskList;
    }

    public static void setTaskList(TaskList taskList) {
        SettingValues.taskList = taskList;
    }

    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        SettingValues.path = path;
    }

    public static String getBarcodeVal() {
        return barcodeVal;
    }

    public static void setBarcodeVal(String barcodeVal) {
        SettingValues.barcodeVal = barcodeVal;
    }

    public static ArrayList<String> getBarcodelist() {
        return barcodelist;
    }

    public static void setBarcodelist(ArrayList<String> barcodelist) {
        SettingValues.barcodelist = barcodelist;
    }

    public static String getJsonval() {
        return jsonval;
    }

    public static void setJsonval(String jsonval) {
        SettingValues.jsonval = jsonval;
    }

    public static String getFormId() {
        return formId;
    }

    public static void setFormId(String formId) {
        SettingValues.formId = formId;
    }

    public static String getTaskId() {
        return taskId;
    }

    public static void setTaskId(String taskId) {
        SettingValues.taskId = taskId;
    }

    public static ArrayList<TabsModel> getTabList() {
        return tabList;
    }

    public static AdapterTabs getAdapterTabs() {
        return adapterTabs;
    }

    public static void setAdapterTabs(AdapterTabs adapterTabs) {
        SettingValues.adapterTabs = adapterTabs;
    }

    public static void setTabList(ArrayList<TabsModel> tabList) {
        SettingValues.tabList = tabList;
    }
}
