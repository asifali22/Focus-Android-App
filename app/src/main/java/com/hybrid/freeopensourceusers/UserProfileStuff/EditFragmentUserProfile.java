package com.hybrid.freeopensourceusers.UserProfileStuff;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.freeopensourceusers.Callback.UpdateInterest;
import com.hybrid.freeopensourceusers.Callback.UpdateOrg;
import com.hybrid.freeopensourceusers.Callback.UpdateStatus;
import com.hybrid.freeopensourceusers.Callback.UpdateUI;
import com.hybrid.freeopensourceusers.R;
import com.hybrid.freeopensourceusers.SharedPrefManager.SharedPrefManager;
import com.hybrid.freeopensourceusers.Utility.Utility;
import com.hybrid.freeopensourceusers.Volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragmentUserProfile extends Fragment {

    private TextInputLayout editTextGeneralised;
    private TextView toolbarTitle;
    private EditText insideEditText;
    private SharedPrefManager sharedPrefManager;
    private AppCompatButton okButton, cancelButton;
    private static int FLAG;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String desc, interest, organisation, status, input;
    private UpdateUI updateUI;
    private UpdateInterest updateInterest;
    private UpdateOrg updateOrg;
    private UpdateStatus updateStatus;


    public EditFragmentUserProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FLAG = getArguments().getInt("FLAG_FINAL");

        desc = getArguments().getString("DESCRIPTION");
        interest = getArguments().getString("INTEREST");
        organisation = getArguments().getString("ORGANISATION");
        status = getArguments().getString("STATUS");
        int maxValue = getArguments().getInt("MAX");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_fragment_user_profile, container, false);

        toolbarTitle = (TextView) root.findViewById(R.id.toolbarTitle);
        insideEditText = (EditText) root.findViewById(R.id.insideEditText);
        editTextGeneralised = (TextInputLayout) root.findViewById(R.id.editTextFragmentUserProfile);
        okButton = (AppCompatButton) root.findViewById(R.id.okButtonUserPro);
        cancelButton = (AppCompatButton) root.findViewById(R.id.cancelButtonUserPro);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        editTextGeneralised.setCounterEnabled(true);
        sharedPrefManager = new SharedPrefManager(getContext());

        editTextGeneralised.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openKeyboard();
            }
        }, 500);


        if (FLAG == 1) {
            if (desc != null) {
                editTextGeneralised.getEditText().setText(desc);
                insideEditText.setSelection(desc.length());
            }
            toolbarTitle.setText("Update description");
            editTextGeneralised.setCounterMaxLength(maxValue);

        } else if (FLAG == 2) {
            if (interest != null) {
                editTextGeneralised.getEditText().setText(interest);
                insideEditText.setSelection(interest.length());
            }
            toolbarTitle.setText("Update interests");
            editTextGeneralised.setCounterMaxLength(maxValue);
            setEditTextMaxLength(insideEditText, maxValue);
        } else if (FLAG == 3) {
            if (organisation != null) {
                editTextGeneralised.getEditText().setText(organisation);
                insideEditText.setSelection(organisation.length());
            }
            toolbarTitle.setText("Update organisation");
            editTextGeneralised.setCounterMaxLength(maxValue);
            setEditTextMaxLength(insideEditText, maxValue);
        } else if (FLAG == 4) {
            if (status != null) {
                editTextGeneralised.getEditText().setText(status);
                insideEditText.setSelection(status.length());
            }
            toolbarTitle.setText("Update status");
            editTextGeneralised.setCounterMaxLength(maxValue);
            setEditTextMaxLength(insideEditText, maxValue);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input = editTextGeneralised.getEditText().getText().toString().trim();
                if (FLAG == 1) {

                    updateDescription(input);
                } else if (FLAG == 2) {

                    updateInterest(input);
                } else if (FLAG == 3) {
                    updateOrganisation(input);
                } else if (FLAG == 4) {
                    updateStatus(input);
                }


            }


        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return root;
    }

    private void updateStatus(final String input) {

        String URL = Utility.getIPADDRESS() + "updateUserStatus";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        sharedPrefManager.updateUserStatus(input);
                        updateStatus.statusUpdate(input);
                        getActivity().onBackPressed();
                    } else if (jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", input);
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }

    private void updateOrganisation(final String input) {
        String URL = Utility.getIPADDRESS() + "updateUserOrganisation";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        sharedPrefManager.updateUserOrganisation(input);
                        updateOrg.updateOrg(input);
                        getActivity().onBackPressed();
                    } else if (jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("organisation", input);
                return params;
            }

        };
        requestQueue.add(stringRequest);


    }

    private void updateInterest(final String input) {
        String URL = Utility.getIPADDRESS() + "updateUserInterest";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        sharedPrefManager.updateUserInterest(input);
                        updateInterest.updateInterest(input);
                        getActivity().onBackPressed();
                    } else if (jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("interest", input);
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }

    private void updateDescription(final String input) {
        String URL = Utility.getIPADDRESS() + "updateUserAbout";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        sharedPrefManager.updateUserDesc(input);
                        updateUI.updateDESC(input);
                        getActivity().onBackPressed();
                    } else if (jsonObject.getBoolean("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", sharedPrefManager.getApiKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("about", input);
                return params;
            }

        };
        requestQueue.add(stringRequest);


    }


    public void setEditTextMaxLength(EditText editText, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(FilterArray);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            updateUI = (UpdateUI) context;
            updateInterest = (UpdateInterest) context;
            updateOrg = (UpdateOrg) context;
            updateStatus = (UpdateStatus) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onViewSelected");
        }
    }


    public void openKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                insideEditText.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }
}
