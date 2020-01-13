package com.aap.medicore.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.DBImagesModel;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Service to form Queue of videos after processing of video.
public class UploaderService extends Service {


    QueueModel model;
    TinyDB tinyDB;
    ArrayList<QueueModel> list;
    public static final String
            BROADCAST_ACTION = "com.medicore.service";
    Intent newintent;
    UploaderService context;
    DatabaseHandler databaseHandler;
    private JSONObject completeCallObject;
    private List<String> formIds;
    private int numSubmitted = 0;
    boolean flag;

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        formIds = new ArrayList<>();
        tinyDB = new TinyDB(getApplicationContext());
        databaseHandler = new DatabaseHandler(UploaderService.this);

        new UploadFile(UploaderService.this).execute();
        // TBD
        return Service.START_FLAG_REDELIVERY;       //        return Service.START_FLAG_REDELIVERY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UploadFile extends AsyncTask<String, Integer, String> {

        public UploadFile(UploaderService activity) {
        }

        @Override
        protected String doInBackground(String... url) {
            list = databaseHandler.getAllQueuedIncidences();
            Log.e("serviceList", list.size() + "");

            for (int i = 0; i < list.size(); i++) {
                Log.e("serviceItemState", list.get(i).getState() + "");
                if (list.get(i).getState().equalsIgnoreCase(Constants.StateAdded) || list.get(i).getState().equalsIgnoreCase(Constants.StateUploadFailed)) {
                    QueueModel queueModel = new QueueModel();
                    queueModel.setId(list.get(i).getId());
                    queueModel.setJson(list.get(i).getJson());
                    queueModel.setState(Constants.StateUploading);
                    queueModel.setTitle(list.get(i).getTitle());
                    queueModel.setMessage("Sending to Admin panel");
                    databaseHandler.updateQueuedIncidenceStateOnRunId(queueModel);
                    sendData(queueModel);
                }

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
//            dialog.dismiss();
            Log.e("progress", "Canceled");
        }
    }

    public void sendData(QueueModel model) {
        String task_id = model.getId();

        ArrayList<DBImagesModel> imagesList = new ArrayList<>();

        imagesList = databaseHandler.getAllImagesOnImageId(task_id);

        Log.e("serviceImagesList", imagesList.size() + ", " + task_id);


        ArrayList<MultipartBody.Part> images = new ArrayList<>();

        for (int i = 0; i < imagesList.size(); i++) {
            File file1 = new File(String.valueOf(imagesList.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), new File(imagesList.get(i).getTempUri().toString()))));
        }
        JSONArray forms = new JSONArray();


        try {
            completeCallObject = new JSONObject(model.getJson());

            forms = completeCallObject.getJSONArray("forms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        completeCallObject.remove("forms");
        for (int i = 0; i < forms.length(); i++) {
            try {

                org.json.JSONObject object1 = (JSONObject) forms.get(i);
                JSONObject object = new JSONObject();
                object.put("job_id", task_id);
                String formId = (String) object1.get("form_id");
                object.put("form_id", object1.get("form_id"));
                object1.remove("form_id");
                JSONArray array = new JSONArray();
                array.put(object1);
                object.put("forms", array);
                RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), object.toString());
                retrofit2.Call<SubmitFormResponse> call;
                for (String formId1 : formIds) {
                    if (formId.equalsIgnoreCase(formId1)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    if (imagesList.size() == 0) {
                        call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmitWitoutImages(tinyDB.getString(Constants.token), bodyRequest);
                    } else {
                        call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmit(tinyDB.getString(Constants.token), bodyRequest, images);
                    }

                    call.enqueue(new Callback<SubmitFormResponse>() {
                        @Override
                        public void onResponse(Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                            if (response.isSuccessful()) {
                                formIds.add(formId);
                                QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(task_id);
                                model.setNumSubmitted(++numSubmitted + "");
                                databaseHandler.updateQueuedIncidenceStateOnRunId(model);
//                            updateUI.updateUI(databaseHandler.getAllQueuedIncidences());
//                            sendBroadcast(newintent);
                                EventBus.getDefault().post(new MessageEvent("Form Submitted"));
                            }
                        }

                        @Override
                        public void onFailure(Call<SubmitFormResponse> call, Throwable t) {
                            QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(task_id);
                            model.setState(Constants.StateUploadFailed);
                            model.setNumSubmitted(numSubmitted + "");
                            databaseHandler.updateQueuedIncidenceStateOnRunId(model);
                            EventBus.getDefault().post(new MessageEvent("Form Submitted"));
                            t.printStackTrace();
                        }
                    });
                } else
                    break;


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.e("serviceImgPrtsList", images.size() + "");

        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), completeCallObject.toString());
        retrofit2.Call<SubmitFormResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().completeCall(tinyDB.getString(Constants.token), bodyRequest);
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {

                    newintent = new Intent(BROADCAST_ACTION);

                    if (response.body().getStatus() == 200) {
                        newintent.putExtra(Constants.pendingStatus, false);
                        tinyDB.putBoolean(Constants.pendingStatus, false);
                        Toast.makeText(getApplicationContext(), "The pending call is completed successfully.", Toast.LENGTH_SHORT).show();
                        QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(task_id);
                        model.setMessage("Successfully Uploaded.");
                        model.setState(Constants.StateUploadSucceeded);
                        model.setNumSubmitted(0 + "");
                        numSubmitted = 0;
                        databaseHandler.deleteDraftIncidencesTable();
                        databaseHandler.deleteTabsData();
                        databaseHandler.deleteTabsImagesData();
                        databaseHandler.deleteQueuedIncidenceOnIndidenceId(task_id);
                        databaseHandler.deleteAssignedIncidenceOnIndidenceId(task_id);
                        for (String formId : formIds)
                            databaseHandler.deleteDraftIncidenceOnIndidenceId(formId, task_id);
                        formIds.clear();
                        sendBroadcast(newintent);
                        EventBus.getDefault().post(new MessageEvent("Call Completed"));


                    } else {

                        QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(task_id);
                        model.setMessage(response.body().getMessage());
                        model.setState(Constants.StateUploadFailed);
                        databaseHandler.updateQueuedIncidenceStateOnRunId(model);
                        sendBroadcast(newintent);

                    }
                } else {

                    QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(task_id);
//                    model.setMessage(response.body().getMessage());
                    model.setState(Constants.StateUploadFailed);
                    databaseHandler.updateQueuedIncidenceStateOnRunId(model);
                    sendBroadcast(newintent);

                }
            }

            @Override
            public void onFailure(Call<SubmitFormResponse> call, Throwable t) {
                t.printStackTrace();
                QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(task_id);
                model.setState(Constants.StateUploadFailed);
                databaseHandler.updateQueuedIncidenceStateOnRunId(model);
            }
        });
    }
}

