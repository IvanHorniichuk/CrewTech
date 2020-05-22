package com.aap.medicore.NetworkCalls;


import com.aap.medicore.Models.AddVitalForm;
import com.aap.medicore.Models.AdminForms;
import com.aap.medicore.Models.CheckListForms;
import com.aap.medicore.Models.EquipmentChecklistResponse;
import com.aap.medicore.Models.LoginData;
import com.aap.medicore.Models.NewAdminChecklist;
import com.aap.medicore.Models.NewAdminForm;
import com.aap.medicore.Models.StaticPages;
import com.aap.medicore.Models.StatusResponse;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.Models.TaskDetailsResponse;
import com.aap.medicore.Models.TasksListResponse;
import com.aap.medicore.Models.VehicleChecklistResponse;
import com.aap.medicore.Models.VehicleResponse;
import com.aap.medicore.Models.Version;
import com.aap.medicore.Models.VersionCheck;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface webRequests {

    @FormUrlEncoded
    @POST("user/user-login")
    Call<LoginData> hitLogin(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("dynamic-form/task-with-json")
    Call<TasksListResponse> hitTasksList(@Header("Authorization") String token,@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("dynamic-form/checklist_forms")
    Call<AdminForms> hitAdminFormsList(@Header("Authorization") String token, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("dynamic-form/admin-form-details")
    Call<NewAdminChecklist> getAdminForms(@Header("Authorization") String token, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user/get_staticfile")
    Call<StaticPages> hitStaticpages(@Header("Authorization") String token, @Field("user_id") String user_id);



//    @FormUrlEncoded
//    @POST("dynamic-form/task-with-json")
//    Call<TasksListResponse> hitTasksList(@Field("user_id") String user_id);



    @FormUrlEncoded
    @POST("user/vehicle-detail")
    Call<VehicleResponse> getVehicleDetail(@Header("Authorization") String token,@Field("user_id") String id);

    @FormUrlEncoded
    @POST("dynamic-form/form-detail")
    Call<TaskDetailsResponse> getTaskDetail(@Field("task_id") String task_id);

    @Multipart
    @POST("dynamic-form/form-data-submit")
    Call<SubmitFormResponse> formSubmit(@Header("Authorization") String token,@Part("json") RequestBody body, @Part ArrayList<MultipartBody.Part> images);

    @Multipart
    @POST("dynamic-form/form-data-submit")
    Call<SubmitFormResponse> formSubmitWitoutImages(@Header("Authorization") String token,@Part("json") RequestBody body);

    @FormUrlEncoded
    @POST("user/user-status")
    Call<StatusResponse> hitUserStatus(@Header("Authorization") String token,@Field("user_id") String user_id, @Field("status") String status, @Field("user_status") String userStatus);

    @Multipart
    @POST("dynamic-form/complete-call")
    Call<SubmitFormResponse> completeCall(@Header("Authorization") String token,@Part("json") RequestBody body);


    @FormUrlEncoded
    @POST("dynamic-form/vehicle-checklist-json")
    Call<VehicleChecklistResponse> getVehicleChecklist(@Header("Authorization") String token,@Field("user_id") String user_id);

//    @FormUrlEncoded
//    @POST("dynamic-form/equipment-checklist-json")
//    Call<EquipmentChecklistResponse> getEquipmentChecklist(@Header("Authorization") String token,@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("dynamic-form/checklist_form_data")
    Call<CheckListForms> getEquipmentChecklist(@Header("Authorization") String token, @Field("form_id") String form_id);

    @FormUrlEncoded
    @POST("dynamic-form/vital_forms")
    Call<AddVitalForm> getvital(@Header("Authorization") String token, @Field("form_id") String user_id, @Field("incident_id") String incident);



    @GET("user/api-version")
    Call<VersionCheck> versioncheck();
    @FormUrlEncoded
    @POST("dynamic-form/version")
    Call<Version> versioncheck(@Field("version") String version_no);
    @Multipart
    @POST("dynamic-form/checklist-data-submit")
    Call<SubmitFormResponse> submitChecklistData(@Header("Authorization") String token,@Part("json") RequestBody body,@Part ArrayList<MultipartBody.Part> images,@Part("vehicle_id") RequestBody vehicle_id, @Part("checklist_type") RequestBody checklist_type,@Part("form_id") int id);


//http://192.168.10.111:8000/api/user/incident-reported-by-crew

    @Multipart
    @POST("user/incident-reported-by-crew")
    Call<SubmitFormResponse> reportIncident(@Header("Authorization") String token,@Part("incident_date") RequestBody incident_date, @Part("incident_time") RequestBody incident_time, @Part("reported_by") RequestBody reported_by, @Part("location") RequestBody location, @Part("affected_person_details") RequestBody affected_person_details, @Part("did_anybody_suffer_harm") RequestBody did_anybody_suffer_harm, @Part("assistance_given") RequestBody assistance_given, @Part("reported_to_other_agency") RequestBody incident_reported_to_other_agency, @Part("incident_witness") RequestBody incident_witness, @Part("details_of_the_incident") RequestBody details_of_the_incident, @Part("call_id") RequestBody job_id, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("user/updatefirebasedata")
    Call<ResponseBody> sendFCMTokenToServer(@Header("Authorization") String token, @Field("device_id")String deviceId, @Field("device_type") String deviceType);

    @FormUrlEncoded
    @POST("user/crew-logout")
    Call<ResponseBody> crewLogout (@Header("Authorization") String token,@Field("user_id") String userId);
}