package alvinwong.thelemonlaw;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class Main extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectFragment(item);
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pushFragment(new Home());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);
    }
    public void sendMessage(View v){
        System.out.println("Start button clicked");
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }
    protected void selectFragment(MenuItem item){
        item.setChecked(!item.isChecked());
        switch (item.getItemId()){
            case R.id.about:
                pushFragment(new About());
                break;
            case R.id.home:
                pushFragment(new Home());
                break;
            case R.id.contact:
                pushFragment (new Contact());
                break;

        }
    }

    protected void pushFragment(Fragment fragment){
        if (fragment == null){
            return;
        }
        FragmentManager fragmentManager =getFragmentManager();
        if (fragmentManager != null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null){
                ft.replace(R.id.content, fragment);
                ft.commit();
            }
        }

    }

}
