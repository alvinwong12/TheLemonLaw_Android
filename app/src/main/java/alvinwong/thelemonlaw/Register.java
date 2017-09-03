package alvinwong.thelemonlaw;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Alvin on 8/31/2017.
 */

public class Register {
    private static final String DB_URL = "https://thelemonlaw-feb88.firebaseio.com/users";
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static DatabaseReference reference;

    private static void write() throws JSONException {

        UserJson user = new UserJson();
        user.put("gender", UserInfo.gender);
        user.put("age", UserInfo.age);
        user.put("online", true);

        reference.child(UserInfo.username).setValue(user);
        /*
        reference.child(UserInfo.username).child("gender").setValue(UserInfo.gender);
        reference.child(UserInfo.username).child("age").setValue(UserInfo.age);
        reference.child(UserInfo.username).child("online").setValue(true);
        */
    }
    public static void Run(Context login_context){
        reference = db.getReference("users");
        try{
            write();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //RequestQueue rQueue = Volley.newRequestQueue(login_context);
        //rQueue.add(request);
    }
}

class UserJson{
    String gender;
    long age;
    boolean online;
    long time = System.currentTimeMillis();

    public UserJson(){
    }
    public void put(String key, Object value){
        if (key.equals("gender")){
            gender = value.toString();
        }
        else if (key.equals("age")){
            age = Long.parseLong(value.toString());
        }
        else if (key.equals("online")){
            online = Boolean.parseBoolean(value.toString());
        }
    }
    public String getGender(){
        return this.gender;
    }
    public long getAge(){
        return this.age;
    }
    public boolean getOnline(){
        return this.online;
    }
    public long getTime(){
        return this.time;
    }

}
