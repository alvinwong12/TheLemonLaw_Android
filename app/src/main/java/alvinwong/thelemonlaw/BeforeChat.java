package alvinwong.thelemonlaw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class BeforeChat extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int total_users = 0;
    ProgressDialog PD;
    TextView user_count;
    TextView username;
    private boolean nextActivityChat = false;

    final String DB_URL = "https://thelemonlaw-feb88.firebaseio.com/users.json";

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference("users");

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_chat);

        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);
        user_count = (TextView)findViewById(R.id.user_count);
        username = (TextView) findViewById((R.id.username));

        username.setText("Logged in as: " + UserInfo.username);

        queue = Volley.newRequestQueue(BeforeChat.this);

        PD = new ProgressDialog(BeforeChat.this);
        PD.setMessage("Loading...");
        PD.show();
        /*
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                StringRequest request = new StringRequest(Request.Method.GET, DB_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        listUsers(response);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("" + error);
                    }

                });

                RequestQueue queue = Volley.newRequestQueue(BeforeChat.this);
                queue.add(request);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        final SwipeRefreshLayout rl = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        rl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {

                usersList.setClickable(false);
                StringRequest request = new StringRequest(Request.Method.GET, DB_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        listUsers(response);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("" + error);
                    }

                });
                queue.add(request);

                rl.setRefreshing(false);
                usersList.setClickable(true);

            }
        });

        StringRequest request = new StringRequest(Request.Method.GET, DB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                listUsers(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("" + error);
            }

        });

        queue.add(request);
        System.out.println(queue.getSequenceNumber());
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nextActivityChat = true;
                UserInfo.chatWith = al.get(position);
                startActivity(new Intent(BeforeChat.this, Chat.class));
            }
        });

        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                reference.child(UserInfo.username).child("online").setValue(false);
                startActivity(new Intent(BeforeChat.this, Login.class));
            }
        });
    }
    /*
    protected void onPause(){
        super.onPause();
        if (!nextActivityChat){
            reference.child(UserInfo.username).child("online").setValue(false);
        }
    }
    */
    protected void onResume(){
        super.onResume();
        nextActivityChat = false;
    }
    protected void onRestart(){
        super.onRestart();
        nextActivityChat = false;
        reference.child(UserInfo.username).child("online").setValue(true);
    }
    protected void onStop(){
        super.onStop();
        if (!nextActivityChat){
            reference.child(UserInfo.username).child("online").setValue(false);
        }

    }
    public void listUsers(String response){
        try{
            JSONObject response_json = new JSONObject(response);
            Iterator i = response_json.keys();
            String user = "";

            al.clear();
            try{
                usersList.getAdapter().notifyAll();
            }catch (Exception e){

            }
            total_users = 0;
            while(i.hasNext()){
                user = i.next().toString();
                // condition, add two conditions
               // System.out.println(response_json.getJSONObject(user).get("online").toString().equals("true"));
                if (!user.equals(UserInfo.username) && response_json.getJSONObject(user).get("online").toString().equals("true") && !(response_json.getJSONObject(user).get("gender").toString().equals(UserInfo.gender))){
                    al.add((user));
                    total_users++;
                }

            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        if (total_users < 1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));

        }
        user_count.setText("Users avaliable: " + total_users);
        PD.dismiss();
    }
}
