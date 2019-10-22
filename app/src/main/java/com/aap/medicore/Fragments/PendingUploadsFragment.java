package com.aap.medicore.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aap.medicore.Activities.Login;
import com.aap.medicore.Adapters.AdapterPendingUploads;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;

import java.util.ArrayList;

import static com.aap.medicore.Utils.UploaderService.BROADCAST_ACTION;

public class PendingUploadsFragment extends BaseFragment {

    private View view;
    private LinearLayout llLogout;
    TinyDB tinyDB;
    RecyclerView rvPendingUploads;
    AdapterPendingUploads adapter;
    LinearLayoutManager manager;
    DatabaseHandler databaseHandler;
    ArrayList<QueueModel> list = new ArrayList<>();
    private SwipeRefreshLayout pullToRefresh;
    CustomTextView tvNoPendingUploads;

    public PendingUploadsFragment() {
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
        view = inflater.inflate(R.layout.fragment_pending_uploads, container, false);

        init(view);
        clickListeners();

        return view;
    }

    private void init(View v) {
        llLogout = v.findViewById(R.id.llLogout);
        tinyDB = new TinyDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        rvPendingUploads = v.findViewById(R.id.rvPendingUploads);
        manager = new LinearLayoutManager(getActivity());
        tvNoPendingUploads = view.findViewById(R.id.tvNoPendingUploads);
        pullToRefresh = v.findViewById(R.id.pullToRefresh);

        list = databaseHandler.getAllQueuedIncidences();
        Log.e("Up", databaseHandler.getAllQueuedIncidences().size() + "");

        if (list.size() == 0) {
            tvNoPendingUploads.setVisibility(View.VISIBLE);
            rvPendingUploads.setVisibility(View.GONE);
        } else {
            tvNoPendingUploads.setVisibility(View.GONE);
            rvPendingUploads.setVisibility(View.VISIBLE);
            adapter = new AdapterPendingUploads(list, getActivity());
            rvPendingUploads.setLayoutManager(manager);
            rvPendingUploads.setAdapter(adapter);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("CALLED", "Intent passed");

            pullToRefresh.setRefreshing(true);
            if (adapter != null) {
                list = databaseHandler.getAllQueuedIncidences();
                if (list.size() == 0) {
                    tvNoPendingUploads.setVisibility(View.VISIBLE);
                    rvPendingUploads.setVisibility(View.GONE);
                } else {
                    tvNoPendingUploads.setVisibility(View.GONE);
                    rvPendingUploads.setVisibility(View.VISIBLE);
                    adapter.setItems(list);
                    adapter.notifyDataSetChanged();
                }
            } else {

                list = databaseHandler.getAllQueuedIncidences();
                Log.e("Up", databaseHandler.getAllQueuedIncidences().size() + "");

                if (list.size() == 0) {
                    tvNoPendingUploads.setVisibility(View.VISIBLE);
                    rvPendingUploads.setVisibility(View.GONE);
                } else {
                    tvNoPendingUploads.setVisibility(View.GONE);
                    rvPendingUploads.setVisibility(View.VISIBLE);
                    adapter = new AdapterPendingUploads(list, getActivity());
                    rvPendingUploads.setLayoutManager(manager);
                    rvPendingUploads.setAdapter(adapter);
                }
            }
            pullToRefresh.setRefreshing(false);

        }
    };

    private void clickListeners() {
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                logoutConfirmDialogBox();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    list = databaseHandler.getAllQueuedIncidences();
                    if (list.size() == 0) {
                        tvNoPendingUploads.setVisibility(View.VISIBLE);
                        rvPendingUploads.setVisibility(View.GONE);
                    } else {
                        tvNoPendingUploads.setVisibility(View.GONE);
                        rvPendingUploads.setVisibility(View.VISIBLE);
                        adapter.setItems(list);
                        adapter.notifyDataSetChanged();
                    }
                } else {

                    list = databaseHandler.getAllQueuedIncidences();
                    Log.e("Up", databaseHandler.getAllQueuedIncidences().size() + "");

                    if (list.size() == 0) {
                        tvNoPendingUploads.setVisibility(View.VISIBLE);
                        rvPendingUploads.setVisibility(View.GONE);
                    } else {
                        tvNoPendingUploads.setVisibility(View.GONE);
                        rvPendingUploads.setVisibility(View.VISIBLE);
                        adapter = new AdapterPendingUploads(list, getActivity());
                        rvPendingUploads.setLayoutManager(manager);
                        rvPendingUploads.setAdapter(adapter);
                    }
                }
                pullToRefresh.setRefreshing(false);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(getActivity());
        final CustomButton btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        btnYes = (CustomButton) dialog.findViewById(R.id.btnYes);
        btnNo = (CustomButton) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                tinyDB.putBoolean(Constants.LoggedIn, false);
                tinyDB.remove(Constants.user_id);
                tinyDB.remove(Constants.email);
                tinyDB.remove(Constants.first_name);
                tinyDB.remove(Constants.last_name);
                tinyDB.remove(Constants.username);
                tinyDB.remove(Constants.profile_image);

                databaseHandler.deleteQueuedIncidencesTable();
                databaseHandler.deleteAssignedIncidencesTable();
                databaseHandler.deleteQueuedImagestable();


                Intent i = new Intent(getActivity(), Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                dialog.hide();
                getActivity().finishAffinity();
            }
        });
        dialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
                BROADCAST_ACTION));
        pullToRefresh.setRefreshing(true);
        if (adapter != null) {
            list = databaseHandler.getAllQueuedIncidences();
            if (list.size() == 0) {
                tvNoPendingUploads.setVisibility(View.VISIBLE);
                rvPendingUploads.setVisibility(View.GONE);
            } else {
                tvNoPendingUploads.setVisibility(View.GONE);
                rvPendingUploads.setVisibility(View.VISIBLE);
                adapter.setItems(list);
                adapter.notifyDataSetChanged();
            }
        } else {

            list = databaseHandler.getAllQueuedIncidences();
            Log.e("Up", databaseHandler.getAllQueuedIncidences().size() + "");

            if (list.size() == 0) {
                tvNoPendingUploads.setVisibility(View.VISIBLE);
                rvPendingUploads.setVisibility(View.GONE);
            } else {
                tvNoPendingUploads.setVisibility(View.GONE);
                rvPendingUploads.setVisibility(View.VISIBLE);
                adapter = new AdapterPendingUploads(list, getActivity());
                rvPendingUploads.setLayoutManager(manager);
                rvPendingUploads.setAdapter(adapter);
            }
        }
        pullToRefresh.setRefreshing(false);

        super.onResume();
    }
}
