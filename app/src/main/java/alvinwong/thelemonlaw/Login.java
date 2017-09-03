package alvinwong.thelemonlaw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private final String DB_URL = "https://thelemonlaw-feb88.firebaseio.com/users.json";
    private ProgressDialog PD;
    private UserInfo user;
    private String username;
    private String gender;
    private int age;
    private boolean loggedIn = false;



    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username_input = (EditText)findViewById(R.id.username_input);
        final EditText age_input = (EditText)findViewById(R.id.age_input);
        final EditText gender_input = (EditText)findViewById(R.id.gender_input);
        PD = new ProgressDialog(Login.this);

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean login = true;
                username = username_input.getText().toString();
                if (username.equals("") || username == null){
                    Toast.makeText(Login.this, "Username cannot be blank", Toast.LENGTH_LONG).show();
                    login = false;
                }

                if (age_input.getText().toString().equals("")){
                    Toast.makeText(Login.this, "Age cannot be blank", Toast.LENGTH_LONG).show();
                    login = false;
                }else{
                    age = Integer.parseInt(age_input.getText().toString());
                }

                if (gender_input.getText().toString().equals("")){
                    Toast.makeText(Login.this, "Gender cannot be blank", Toast.LENGTH_LONG).show();
                    login = false;
                }else{
                    gender = gender_input.getText().toString();
                }



                PD.setMessage("Loading...");
                PD.show();


                if (login){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        public static final String TAG = "LoginActivity";

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            //String value = dataSnapshot.getValue(String.class);
                            Object obj = dataSnapshot.getValue();
                            try {
                                JSONObject response_json;
                                if (obj != null) {
                                    response_json = new JSONObject(obj.toString());
                                }
                                else{
                                    response_json = new JSONObject("{}");
                                }

                                if (response_json.has(username)){
                                    // set username

                                    UserInfo.username = username;
                                    if (!checkLogin(response_json)) {
                                        UserInfo.age = age;
                                        UserInfo.gender = gender;
                                        Toast.makeText(Login.this, "Logging in as " + UserInfo.username, Toast.LENGTH_LONG).show();
                                        // Login
                                        reference.child(UserInfo.username).child("online").setValue(true);
                                        Intent i = new Intent(Login.this, BeforeChat.class);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(Login.this, "User already logged in", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else{
                                    // set username
                                    // set username
                                    UserInfo.username = username;
                                    UserInfo.age = age;
                                    UserInfo.gender = gender;
                                    // register to DB
                                    Register.Run(Login.this);
                                    Toast.makeText(Login.this, "Registered user", Toast.LENGTH_LONG).show();
                                    // Login
                                    Intent i = new Intent(Login.this, BeforeChat.class);
                                    startActivity(i);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG, "Value is: " + obj);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                }
                PD.dismiss();


            }
        });
    }
    private boolean checkLogin(JSONObject response_json){

        if (response_json.has(UserInfo.username)){
            try {
                if (response_json.getJSONObject(username).get("online").toString().equals("true")){
                    return true;
                }
                else{
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;

        }
        else{
            return false;
        }

    }
}
