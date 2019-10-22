package com.aap.medicore.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Activities.TaskDetails;
import com.aap.medicore.Models.Field;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.ItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterTaskDetails extends RecyclerView.Adapter<AdapterTaskDetails.ViewHolder> {
    ArrayList<Field> list = new ArrayList<>();
    Context mContext;
    private long mLastClickTime = 0;
    CustomButton btnSubmit;
    ProgressBar progressBar;
//    GridLayoutManager cbgridLayoutManager;
//    GridLayoutManager rbgridLayoutManager;

    public AdapterTaskDetails(ArrayList<Field> list, Context mContext, CustomButton btnSubmit, ProgressBar progressBar) {
        this.list = list;
        this.mContext = mContext;
        this.btnSubmit = btnSubmit;
        this.progressBar = progressBar;
//        cbgridLayoutManager = new GridLayoutManager(mContext, 3);
//        cbgridLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        rbgridLayoutManager = new GridLayoutManager(mContext, 3);
//        rbgridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Log.e("ImagesListSize", list.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_task_details, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    public void setItems(ArrayList<Field> list) {
        this.list = list;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        if (list.get(position).getType().equalsIgnoreCase("text")) {
            holder.textbox.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.radio.setVisibility(View.GONE);
            holder.textarea.setVisibility(View.GONE);
            holder.dropdown.setVisibility(View.GONE);

            holder.etText.setHint(list.get(position).getPlaceholder());

        } else if (list.get(position).getType().equals("checkbox-group")) {
            holder.textbox.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.radio.setVisibility(View.GONE);
            holder.textarea.setVisibility(View.GONE);
            holder.dropdown.setVisibility(View.GONE);
            holder.cbTitle.setText(list.get(position).getPlaceholder());
            for (int i = 0; i < list.get(position).getOptions().size(); i++) {
                AppCompatCheckBox checkBox = new AppCompatCheckBox(mContext);

                checkBox.setText(list.get(position).getOptions().get(i).getLabel());
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 0, 0);
                checkBox.setLayoutParams(perams);
                Typeface font = Typeface.createFromAsset(mContext.getAssets(), "ProductSans-Regular.ttf");
                checkBox.setTypeface(font);
                checkBox.setTextSize(12);
                holder.checkbox.addView(checkBox);
            }

        } else if (list.get(position).getType().equals("radio-group")) {
            holder.textbox.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.GONE);
            holder.radio.setVisibility(View.VISIBLE);
            holder.textarea.setVisibility(View.GONE);
            holder.dropdown.setVisibility(View.GONE);
            holder.rbTitle.setText(list.get(position).getPlaceholder());
            RadioGroup rg = new RadioGroup(mContext);

            for (int i = 0; i < list.get(position).getOptions().size(); i++) {
                RadioButton rb = new RadioButton(mContext);
                rb.setText(list.get(position).getOptions().get(i).getLabel());
                Typeface font = Typeface.createFromAsset(mContext.getAssets(), "ProductSans-Regular.ttf");
                rb.setTypeface(font);
                rb.setTextSize(12);
                rg.addView(rb);
            }
            LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
            rg.setLayoutParams(perams);
            perams.setMargins(4, 4, 0, 0);
            holder.radio.addView(rg);

        } else if (list.get(position).getType().equals("textarea")) {
            holder.textbox.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.GONE);
            holder.radio.setVisibility(View.GONE);
            holder.textarea.setVisibility(View.VISIBLE);
            holder.dropdown.setVisibility(View.GONE);

            holder.etTextArea.setHint(list.get(position).getPlaceholder());

            } else if (list.get(position).getType().equals("select")) {
            holder.textbox.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.GONE);
            holder.radio.setVisibility(View.GONE);
            holder.textarea.setVisibility(View.GONE);
            holder.dropdown.setVisibility(View.VISIBLE);

            SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(mContext,
                    R.layout.custom_spinner_item, list.get(position).getOptions());

            holder.spinner.setAdapter(adapter);

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONArray jsonArray = new JSONArray();
//
//                for (int i = 0; i < list.size(); i++) {
//                    JSONObject obj = new JSONObject();
//
//                    if (list.get(i).getType().equals("text")) {
//
//                        String text = holder.etText.getText().toString();
//                        Log.e("text", text);
//
//                        if (text.isEmpty()) {
//                            Toast.makeText(mContext, "Form is incomplete, Please fill the form carefully.", Toast.LENGTH_SHORT).show();
//                            break;
//                        } else {
//                            try {
//                                obj.put("field_id", list.get(i).getFieldId());
//                                obj.put("value", "\"" + text + "\"");
//                                obj.put("option", "[]");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else if (list.get(i).getType().equals("checkbox-group")) {
//
//                    } else if (list.get(i).getType().equals("radio-group")) {
//
//                    } else if (list.get(i).getType().equals("textarea")) {
//                        String text = holder.etTextArea.getText().toString();
//                        Log.e("textarea", text);
//
//                        if (text.isEmpty()) {
//                            Toast.makeText(mContext, "Form is incomplete, Please fill the form carefully.", Toast.LENGTH_SHORT).show();
//                            break;
//                        } else {
//                            try {
//                                obj.put("field_id", list.get(i).getFieldId());
//                                obj.put("value", "\"" + text + "\"");
//                                obj.put("option", "[]");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else if (list.get(position).getType().equals("select")) {
//
//                    }
//                    jsonArray.put(obj);
//                }
//                JSONObject finalObj = new JSONObject();
//
//                try {
//                    finalObj.put("", jsonArray);
//                    String jsonStr = finalObj.toString();
//                    Log.e("JSON", jsonStr);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

//    public Boolean isConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            //we are connected to a network
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public int getItemCount() {

        return (null != list ? list.size() : 0);
    }

    public void setTypeFace(CustomTextView title, String name, Activity activity) {
        Typeface font = Typeface.createFromAsset(
                mContext.getAssets(),
                name);
        //   "fonts/androidnation.ttf"

        title.setTypeface(font);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener clickListener;
        CustomTextView tvHeading, tvDetails, rbTitle, cbTitle;
        LinearLayout ll, dropdown, textarea, radio, checkbox, textbox;
        AppCompatSpinner spinner;
        CustomEditText etTextArea, etText;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            this.tvHeading = view.findViewById(R.id.tvHeading);
            this.tvDetails = view.findViewById(R.id.tvDetails);
//            this.ll = view.findViewById(R.id.ll);
            dropdown = view.findViewById(R.id.dropdown);
            textarea = view.findViewById(R.id.textarea);
            radio = view.findViewById(R.id.radio);
            checkbox = view.findViewById(R.id.checkbox);
            textbox = view.findViewById(R.id.textbox);
            etTextArea = view.findViewById(R.id.etTextArea);
            rbTitle = view.findViewById(R.id.rbTitle);

            cbTitle = view.findViewById(R.id.cbTitle);
            etText = view.findViewById(R.id.etText);
            spinner = view.findViewById(R.id.spinner);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}