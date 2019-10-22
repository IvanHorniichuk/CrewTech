package com.aap.medicore.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aap.medicore.Activities.TaskDetails;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.DBImagesModel;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


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
                if (list.get(i).getState().equalsIgnoreCase(Constants.StateAdded)) {
                    QueueModel queueModel = new QueueModel();
                    queueModel.setId(list.get(i).getId());
                    queueModel.setJson(list.get(i).getJson());
                    queueModel.setState(Constants.StateUploading);
                    queueModel.setTitle(list.get(i).getTitle());
                    queueModel.setMessage("Sending to Admin panel");
                    databaseHandler.updateQueuedIncidenceStateOnRunId(queueModel);
                    sendData(list.get(i).getJson(), list.get(i).getId());
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

    public void sendData(String data, final String incidenceId) {

        ArrayList<DBImagesModel> imagesList = new ArrayList<>();

        imagesList = databaseHandler.getAllImagesOnImageId(incidenceId);

        Log.e("serviceImagesList", imagesList.size() + ", " + incidenceId);


        ArrayList<MultipartBody.Part> images = new ArrayList<>();

        for (int i = 0; i < imagesList.size(); i++) {
            File file1 = new File(String.valueOf(imagesList.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), new File(imagesList.get(i).getTempUri().toString()))));
        }
        Log.e("serviceImgPrtsList", images.size() + "");

        retrofit2.Call<SubmitFormResponse> call;
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), data.toString());

        if (imagesList.size() == 0) {
            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmitWitoutImages(tinyDB.getString(Constants.token),bodyRequest);
        } else {
            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmit(tinyDB.getString(Constants.token),bodyRequest, images);
        }
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {

                    newintent = new Intent(BROADCAST_ACTION);
                    sendBroadcast(newintent);

                    if (response.body().getStatus() == 200) {

                        QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(incidenceId);
                        model.setMessage("Successfully Uploaded.");
                        model.setState(Constants.StateUploadSucceeded);
                        databaseHandler.deleteQueuedIncidenceOnIndidenceId(model.getId());

                    } else {
                        QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(incidenceId);
                        model.setMessage(response.body().getMessage());
                        model.setState(Constants.StateUploadFailed);
                        databaseHandler.updateQueuedIncidenceStateOnRunId(model);
                    }
                } else {
                    QueueModel model = databaseHandler.getQueueIncidenceStateOnIncidenceID(incidenceId);
                    model.setMessage(response.body().getMessage());
                    model.setState(Constants.StateUploadFailed);
                    databaseHandler.updateQueuedIncidenceStateOnRunId(model);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}