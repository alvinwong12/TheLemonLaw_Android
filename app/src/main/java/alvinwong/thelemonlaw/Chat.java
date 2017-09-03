package alvinwong.thelemonlaw;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;


public class Chat extends AppCompatActivity {

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference, reference1, reference2;
    private final String DB_URL = "https://thelemonlaw-feb88.firebaseio.com";
    TextView chatWith;

    //= db.getReference("https://thelemonlaw-feb88.firebaseio.com/users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        chatWith = (TextView)findViewById(R.id.chatWith);
        chatWith.setText("Chatting with: " + UserInfo.chatWith);


        reference1 =  db.getReference("/messages/" + UserInfo.username + "_" + UserInfo.chatWith);
        reference2 =  db.getReference("/messages/" + UserInfo.chatWith + "_" + UserInfo.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserInfo.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<Map<String, String>> map_gti = new GenericTypeIndicator<Map<String, String>>(){};
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue(map_gti);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserInfo.username)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox(UserInfo.chatWith + ":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }
    protected void onStop(){
        super.onStop();
        reference = db.getReference("users");
        reference.child(UserInfo.username).child("online").setValue(false);
    }
    protected void onRestart(){
        super.onRestart();
        reference = db.getReference("users");
        reference.child(UserInfo.username).child("online").setValue(true);
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);
        textView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            //textView.setBackgroundResource(R.drawable.chat_bubble_me);
            //textView.setBackgroundColor(3);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            //textView.setBackgroundResource(R.drawable.chat_bubble_other);
            //textView.setBackgroundColor(4);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
