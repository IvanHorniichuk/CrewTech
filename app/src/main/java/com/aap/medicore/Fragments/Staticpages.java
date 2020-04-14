package com.aap.medicore.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aap.medicore.Adapters.StaticPagesAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.Models.StaticPages;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.TinyDB;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

public class Staticpages extends BaseFragment {
    private SwipeRefreshLayout pullToRefresh;
    private OnFragmentInteractionListener mListener;
    View v;
    RecyclerView rv_admin;
    StaticPagesAdapter adapter;
    private TinyDB tinyDB;
    ArrayList<String> animalNames = new ArrayList<>();
//    Handler h = new Handler();
//    int delay = 5 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
//    Runnable runnable;


    public Staticpages() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.static_pages, container, false);
        init(v);
        clickListeners();
        hitTasksList();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitTasksList();
//                pullToRefresh.setRefreshing(false);
            }
        });

        return v;
    }


    private void init(View view) {

        rv_admin = view.findViewById(R.id.rv_admin);
        tinyDB = new TinyDB(getActivity());
        pullToRefresh = v.findViewById(R.id.pullToRefresh);
        animalNames.add("Amin Form 1");
        animalNames.add("Amin Form 2");
        // set up the RecyclerView


    }

    private void clickListeners() {
    }


    public void hitTasksList() {
        retrofit2.Call<StaticPages> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitStaticpages(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<StaticPages>() {
            @Override
            public void onResponse(retrofit2.Call<StaticPages> call, final Response<StaticPages> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        animalNames.clear();
                        for (int i = 0; i < response.body().getData().size(); i++) {

                            animalNames.add(response.body().getData().get(i).getTitle());
                            if (getActivity() != null) {

                                rv_admin.setLayoutManager(new LinearLayoutManager(getActivity()));
                                adapter = new StaticPagesAdapter(getActivity(), animalNames, response);
                                rv_admin.setAdapter(adapter);
                                rv_admin.getViewTreeObserver()
                                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                            @Override
                                            public void onGlobalLayout() {
                                                //At this point the layout is complete and the
                                                //dimensions of recyclerView and any child views are known.
                                                //Remove listener after changed RecyclerView's height to prevent infinite
                                                pullToRefresh.setRefreshing(false);
                                                rv_admin.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                            }
                                        });
                            }
                        }

                        Log.e("size", "sizeeee" + animalNames.size());


                    }
                } else if (response.code() == 401) {
                    if (getActivity() != null)
                        new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
                    pullToRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<StaticPages> call, Throwable t) {
                pullToRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onResume() {


//        h.postDelayed(runnable = new Runnable() {
//            public void run() {
//
//                if (isConnected(getActivity())) {
//                    hitTasksList();
//                } else {
////                    Toast.makeText(getActivity(), "Please check your internet conection", Toast.LENGTH_SHORT).getDialog();
//                }
//                h.postDelayed(runnable, delay);
//            }
//        }, delay);

        super.onResume();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        h.removeCallbacks(runnable); //stop handler when activity not visible
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
