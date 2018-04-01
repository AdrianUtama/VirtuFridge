package virtufridge.virtufridge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtuPage extends AppCompatActivity {
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView tv_complete, tv_add;

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab2.setClickable(false);
            fab3.setClickable(false);
            tv_add.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            tv_complete.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            tv_complete.setVisibility(View.GONE);
            tv_add.setVisibility(View.GONE);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab2.setClickable(true);
            fab3.setClickable(true);
            tv_add.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            tv_complete.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            tv_complete.setVisibility(View.VISIBLE);
            tv_add.setVisibility(View.VISIBLE);
            isFabOpen = true;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtu_page);


        tv_add = (TextView) findViewById(R.id.textView_add);
        tv_complete = (TextView) findViewById(R.id.textView_complete);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab_open = AnimationUtils.loadAnimation(this, R.layout.fab_open);
        fab_close = AnimationUtils.loadAnimation(this,R.layout.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this,R.layout.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this,R.layout.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}

