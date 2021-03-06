package com.example.professt.onlineexamination;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class OTPSActivity extends AppCompatActivity {

    EditText otpsEditText;
    Button submitsButton;

    String otp,cell,email;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otps);

        otpsEditText = (EditText)findViewById(R.id.otpsEditText);
        submitsButton = (Button)findViewById(R.id.submitsButton);

        Reminder reminder = Reminder.getInstance();
        cell = reminder.getNumber();
        email = reminder.getEmail();

        submitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = otpsEditText.getText().toString();

                if(otp.isEmpty())
                {
                    otpsEditText.setError("Please Enter OTP Code");
                    otpsEditText.requestFocus();
                }
                else
                {
                    loading = new ProgressDialog(OTPSActivity.this);
                    loading.setIcon(R.drawable.load);
                    loading.setTitle("Forget Password");
                    loading.setMessage("Please wait...");
                    loading.show();


                    HttpsTrustManager.allowAllSSL();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Key.OTPS_CHECK, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //for track response in Logcat
                            Log.d("RESPONSE", "" + response);

                            //if we are getting success from server
                            if (response.equals("success")) {
                                //creating a shared preference
                                loading.dismiss();
                                //starting profile activity
                                Intent intent = new Intent(OTPSActivity.this, NewPasswordsActivity.class);
//                            intent.putExtra("email",emails);
                                startActivity(intent);

                                Toast.makeText(OTPSActivity.this, "Information matched", Toast.LENGTH_SHORT).show();



                            } else if (response.equals("exists")) {
                                Toast.makeText(OTPSActivity.this, "Wrong matched", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                            /*else if (response.equals("existss")) {
                                Toast.makeText(ForgetPasswordActivity.this, "Email is not matched", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }*/
                            else if (response.equals("failure")) {
                                Toast.makeText(OTPSActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(OTPSActivity.this, "There is an error", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(OTPSActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                                    loading.dismiss();

//                                if (error instanceof NetworkError) {
//                                } else if (error instanceof ServerError) {
//                                } else if (error instanceof AuthFailureError) {
//                                } else if (error instanceof ParseError) {
//                                } else if (error instanceof NoConnectionError) {
//                                } else if (error instanceof TimeoutError) {
//                                    Toast.makeText(SignUp.this,
//                                            "Oops. Timeout error!",
//                                            Toast.LENGTH_LONG).show();
//                                }
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            //return super.getParams();

                            Map<String,String> params = new HashMap<>();

                            //Ading parameters to request

                            params.put(Key.KEY_OTP,otp);
                            params.put(Key.KEY_CELL,cell);
                            params.put(Key.KEY_EMAIL,email);

                            //returning parameter
                            return params;

                        }
                    };

//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        MY_SOCKET_TIMEOUT_MS,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    //Adding the string request to the queue
                    RequestQueue requestQueue = Volley.newRequestQueue(OTPSActivity.this);
                    requestQueue.add(stringRequest);
                }
            }
        });
    }
}
