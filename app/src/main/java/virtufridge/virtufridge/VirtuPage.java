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

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VirtuPage extends AppCompatActivity {
    private Boolean isFabOpen = false;
    private TextView tv_complete, tv_add;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
    String currentUserId = "";
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(currentUserId);
    DatabaseReference users = root.child("VirtuFridge");
    ArrayList<String> itemlist=new ArrayList<>();
    ArrayList<String> keylist = new ArrayList<>();
    Calendar currentCal = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtu_page);

        currentCal.getTime().setMonth(currentCal.getTime().getMonth()+1);

        tv_add = (TextView) findViewById(R.id.textView_add);
        tv_complete = (TextView) findViewById(R.id.textView_complete);


        if(currentUser != null){
            currentUserId = currentUser.getUid();
        }
        else{
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }




        final ListView listview;
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.mytextview,itemlist);
        listview=(ListView)findViewById(R.id.listView2);

        DatabaseReference userItems = root.child(currentUserId).child("VirtuFridge");
        userItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot key: snapshot.getChildren()){
                    if(!itemlist.contains(key.getValue().toString())){

                        HashMap<String, Long> timeHashMap = (HashMap)key.child("Expiration Date").getValue();
                        Calendar itemCal = Calendar.getInstance();
                        long tempmonth = timeHashMap.get("month") ;
                        long tempdate = timeHashMap.get("date");
                        long tempyear = timeHashMap.get("year");
                        itemCal.getTime().setMonth((int)tempmonth);
                        itemCal.getTime().setDate((int)tempdate);
                        itemCal.getTime().setYear((int)tempyear);

                        long currmonth = currentCal.getTime().getMonth();
                        long currdate = currentCal.getTime().getDate();
                        long curryear = currentCal.getTime().getYear();
                        //currentCal.compareTo (itemCal)

                        if(tempyear - curryear > 0){
                        }
                        else if (tempyear - curryear == 0){
                            if(tempmonth - currmonth > 0){

                            }
                            else if (tempmonth - currmonth == 0){
                                if(tempdate - currdate < 5 && tempdate - currdate > 0){
                                    Toast.makeText(VirtuPage.this, "Item " + key.child("Item Name").getValue(String.class) + " is about to expire", Toast.LENGTH_SHORT).show();
                                }
                                else if(tempdate - currdate <= 0){
                                    Toast.makeText(VirtuPage.this, "Item " + key.child("Item Name").getValue(String.class) + " expired", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(VirtuPage.this, "Item " + key.child("Item Name").getValue(String.class) + " expired", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(VirtuPage.this, "Item " + key.child("Item Name").getValue(String.class) + " expired", Toast.LENGTH_SHORT).show();
                        }


                        keylist.add(key.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setClickable(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String key = keylist.get(position);
                itemlist.remove(position);
                root.child(currentUserId).child("VirtuFridge").child(key).removeValue();
                adapter.notifyDataSetChanged();
            }
        });

        listview.setAdapter(adapter);

        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(currentUserId);
        DatabaseReference users = root.child("VirtuFridge");
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("Expiration Date").getValue() != null){
                    String key = dataSnapshot.getKey();
                    String shoppingItem = dataSnapshot.child("Item Name").getValue(String.class);
                    HashMap<String, Long> timeHashMap = (HashMap)dataSnapshot.child("Expiration Date").getValue();
                    String month = Long.toString(timeHashMap.get("month")+1);
                    String day =  Long.toString(timeHashMap.get("date"));
                    String year =  Long.toString(timeHashMap.get("year")-100);
                    String expirationDate = month+"/"+day+"/"+year;
                    itemlist.add(shoppingItem+"\n"+expirationDate);
                    keylist.add(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Doesn't need to be implemented right now
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String shoppingItem = dataSnapshot.child("Item Name").getValue(String.class);
                for (int i = 0; i < adapter.getCount(); i++) {
                    if(adapter.getItem(i).equals(shoppingItem)) {
                        //adapter.remove(adapter.getItem(i));
                        keylist.remove(dataSnapshot.getKey());
                        //itemlist.remove(shoppingItem);
                        break;

                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}

