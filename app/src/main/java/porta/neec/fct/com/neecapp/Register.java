package porta.neec.fct.com.neecapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import porta.neec.fct.com.neecapp.request.RegisterRequest;

public class Register extends AppCompatActivity {

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String android_i = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String IME = telephonyManager.getDeviceId();

        Log.d("Dados", IME);
        Log.d("Dados", android_i);

        final EditText etPass = (EditText) findViewById(R.id.pass1);
        final EditText etmail = (EditText) findViewById(R.id.email);
        final EditText pass2 = (EditText) findViewById(R.id.pass2);


        final Button bRegister = (Button) findViewById(R.id.register);


        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = etmail.getText().toString();
                final String password = etPass.getText().toString();
                final String password2 = pass2.getText().toString();


                if (password.length() <= 5) {
                    AlertDialog.Builder burlder = new AlertDialog.Builder(Register.this);

                    burlder.setMessage("A Password tem de ter no minimo 6 letras")
                            .setNegativeButton("Tenta novamente", null)
                            .create()
                            .show();


                } else {


                    if (!password.matches(password2)) {
                        AlertDialog.Builder burrlder = new AlertDialog.Builder(Register.this);

                        burrlder.setMessage("Password não são iguais")
                                .setNegativeButton("Tenta novamente", null)
                                .create()
                                .show();

                    }


                    progress = ProgressDialog.show(Register.this, "Loading..",
                            "Verificando seja paciente", true);


                    Response.Listener<String> responListerner = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progress.dismiss();
                                JSONObject jsonOResponse = new JSONObject(response);
                                boolean sucess = jsonOResponse.getBoolean("success");

                                if (sucess) {
                                    Toast.makeText(getApplicationContext(), "Registado com Sucesso", Toast.LENGTH_SHORT).show();
                                    SystemClock.sleep(730);
                                    Intent intent = new Intent(Register.this, LoginActivity.class);
                                    Register.this.startActivity(intent);
                                } else {
                                    AlertDialog.Builder bulder = new AlertDialog.Builder(Register.this);

                                    bulder.setMessage("Falha no Registo,Email em uso")
                                            .setNegativeButton("Tenta novamente", null)
                                            .create()
                                            .show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };


                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    @SuppressLint("MissingPermission") String IMEI = telephonyManager.getDeviceId();

                    String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
                    RegisterRequest registerrequest = new RegisterRequest(username, password, android_id, IMEI, responListerner);
                    registerrequest.setRetryPolicy(new DefaultRetryPolicy(
                            9000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue queue = Volley.newRequestQueue(Register.this);
                    queue.add(registerrequest);

                }
            }

        });


    }
}
