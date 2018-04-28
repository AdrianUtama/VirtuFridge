package virtufridge.virtufridge;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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


public class ShoppingList extends AppCompatActivity{
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView tv_complete, tv_add;
    /*TODO: Data Format for Each User:
        ShoppingList
            |
            |___Key 1: Item 1
            |___Key 2: Item 2
    */
    //Trial Push Edit 2
    String formattedRecommendedItems = "";
    ArrayList<String> list=new ArrayList<>();
    ArrayList<String> keylist = new ArrayList<>();
    final HashMap<String, Long> itemFreqCountList = new HashMap<>();
    final HashMap<String, String> itemKeyMap = new HashMap<>();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

    TextView tv_date;
    Calendar mCurrentDate;
    boolean userEnteredData;
    int day, month, year;
    long freqCount = 0;

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
        setContentView(R.layout.activity_shopping_list);

        String currentUserId = "";
        if(currentUser != null){
            currentUserId = currentUser.getUid();
        }
        else{
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
        }
        final ListView listview;
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.mytextview,list);

        //adapter.notifyDataSetChanged();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(currentUserId);
        final ArrayList<String> recommendedItems = new ArrayList<>();
        DatabaseReference itemFreqRoot = FirebaseDatabase.getInstance().getReference().child(currentUserId);
        DatabaseReference itemFreqRef = itemFreqRoot;
        itemFreqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("NumberOfVisits").getValue() != null){
                    long totalTrips = (long) dataSnapshot.child("NumberOfVisits").getValue();
                    for(DataSnapshot item : dataSnapshot.child("Item Freq").getChildren()){
                        long itemValue = (long)item.getValue();


                        if(((float) itemValue/ totalTrips) >= 0.5){

                            Toast.makeText(ShoppingList.this, "You should buy " + item.getKey(), Toast.LENGTH_SHORT).show();
                            recommendedItems.add(item.getKey());
//                        formattedRecommendedItems = formattedRecommendedItems + item.getKey().toString() +  ", ";
                        }
                    }
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        for(int i = 0; i < recommendedItems.size() - 1; i++){
//            if(i == recommendedItems.size() - 1){
//                formattedRecommendedItems = formattedRecommendedItems + recommendedItems.get(i);
//            }
//            else{
//                formattedRecommendedItems = formattedRecommendedItems + recommendedItems.get(i) + ", ";
//            }
//
//        }

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

        final String finalCurrentUserId1 = currentUserId;
        final String finalCurrentUserId3 = currentUserId;
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShoppingList.this);
                View fillableView = getLayoutInflater().inflate(R.layout.shopping_list_verify_complete, null);
                Button button_yes = (Button) fillableView.findViewById(R.id.button_yes);
                Button button_no = (Button) fillableView.findViewById(R.id.button_no);

                mBuilder.setView(fillableView);
                final AlertDialog alert = mBuilder.create();
                alert.show();
                final ArrayList<String> tempItemList = list;

                button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.cancel();
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShoppingList.this);
                        View fillableView = getLayoutInflater().inflate(R.layout.shopping_list_add_expiration, null);
                        Button button_yes = (Button) fillableView.findViewById(R.id.button_yes2);
                        Button button_no = (Button) fillableView.findViewById(R.id.button_no2);
                        final TextView itemForExpiration = (TextView)findViewById(R.id.itemForExpiration);

                        mBuilder.setView(fillableView);
                        final AlertDialog alert = mBuilder.create();
                        alert.show();

                        button_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.cancel();

                                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShoppingList.this);
                                final View fillableView = getLayoutInflater().inflate(R.layout.shopping_list_input_expiration_date, null);
                                Button button_expiration = (Button) fillableView.findViewById(R.id.button_expiration);

                                tv_date = (TextView) fillableView.findViewById(R.id.textDate);
                                final TextView itemForExpiration = (TextView) fillableView.findViewById(R.id.itemForExpiration);
                                mCurrentDate = Calendar.getInstance();

                                day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                                month = mCurrentDate.get(Calendar.MONTH);
                                year = mCurrentDate.get(Calendar.YEAR);



                                tv_date.setText(Integer.toString(month + 1)+"/"+Integer.toString(day)+"/"+Integer.toString(year));

                                if(tempItemList.isEmpty()){

                                }
                                else{
                                    itemForExpiration.setText("Enter Expiration of Item: " + tempItemList.get(0));

                                    mBuilder.setView(fillableView);
                                    final AlertDialog alert = mBuilder.create();
                                    button_expiration.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            boolean alertDismissed = false;
                                            if(tempItemList.size() == 0){
                                                alertDismissed = true;
                                                alert.cancel();
                                            }
                                            else{
                                            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(finalCurrentUserId3);
                                            DatabaseReference users = root.child("ShoppingList");



                                            final Calendar cal = Calendar.getInstance();
                                            cal.clear();
                                            cal.set(Calendar.YEAR, year);
                                            cal.set(Calendar.MONTH, month);
                                            cal.set(Calendar.DATE, day);



                                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String keyValue = "";
                                                    for(DataSnapshot item: dataSnapshot.getChildren()) {

                                                        if(item.child("Item Name").getValue(String.class).equals(tempItemList.get(0))){
                                                            keyValue = item.getKey();
                                                            break;
                                                        }
                                                        keyValue = item.getKey();
                                                    }
                                                    if(tempItemList.size() != 0){

                                                        if(dataSnapshot.child(keyValue).child("Item Name").getValue(String.class).equals(tempItemList.get(0))){
                                                            myRef.child("VirtuFridge").child(keyValue).child("Item Name").setValue(tempItemList.get(0));
                                                            myRef.child("VirtuFridge").child(keyValue).child("Expiration Date").setValue(cal.getTime());
                                                            final String itemForFreq = tempItemList.get(0).toLowerCase();

                                                            DatabaseReference itemRootRef = FirebaseDatabase.getInstance().getReference().child(finalCurrentUserId3);
                                                            DatabaseReference itemListRef = itemRootRef.child("Item Freq");
                                                            itemListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    boolean foundData = false;
                                                                    int count = 0;
                                                                    for (DataSnapshot item : dataSnapshot.getChildren()){
                                                                        if(itemForFreq.equals(item.getKey().toString().toLowerCase())){
                                                                            foundData = true;

                                                                            myRef.child("Item Freq").child(itemForFreq).setValue((long)(item.getValue()) + 1);

                                                                        }


                                                                    }

                                                                    if (foundData) {
//                                                                        long currentFreqCount = itemFreqCountList.get(itemForFreq);
//                                                                        itemFreqCountList.put(itemForFreq,currentFreqCount + 1);
//                                                                        for (DataSnapshot item : dataSnapshot.getChildren()){
//
//                                                                        }

                                                                    }
                                                                    else{
                                                                        myRef.child("Item Freq").child(itemForFreq).setValue(1);
                                                                        itemFreqCountList.put(itemForFreq,(long) 1);
                                                                    }
                                                                }
                                                                 @Override
                                                                 public void onCancelled(DatabaseError databaseError) {

                                                                 }
                                                            });

                                                            myRef.child("ShoppingList").child(keyValue).removeValue();
                                                            Toast.makeText(ShoppingList.this, "Item moved to VirtuFridge", Toast.LENGTH_SHORT).show();



                                                            tempItemList.remove(tempItemList.get(0));
                                                            if(tempItemList.size() != 0){
                                                                itemForExpiration.setText("Enter Expiration of Item: " + tempItemList.get(0));
                                                            }

                                                            if(tempItemList.size() == 0){
                                                                DatabaseReference rootVisit = FirebaseDatabase.getInstance().getReference().child(finalCurrentUserId3);
                                                                DatabaseReference usersVisit = rootVisit;
                                                                usersVisit.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.child("NumberOfVisits").getValue() == null){
                                                                            myRef.child("NumberOfVisits").setValue(1);
                                                                        }
                                                                        else{
                                                                            freqCount = (long)dataSnapshot.child("NumberOfVisits").getValue();
                                                                            freqCount += 1;
                                                                            myRef.child("NumberOfVisits").setValue(freqCount);
                                                                        }
                                                                    }
                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });

                                                                alert.cancel();
                                                            }
                                                        }
                                                    }


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });




                                            }
                                            if(alertDismissed == true){

                                            }




                                        }
                                    });

                                    alert.show();
                                }


                                tv_date.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(ShoppingList.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int yearOfYear, int monthOfYear, int dayOfMonth) {

                                                tv_date.setText(Integer.toString(monthOfYear + 1)+"/"+Integer.toString(dayOfMonth)+"/"+Integer.toString(yearOfYear));
                                                day = dayOfMonth;
                                                month = monthOfYear;
                                                year = yearOfYear;
                                            }
                                        }, year, month, day);
                                        datePickerDialog.show();
                                    }
                                });
                            }
                        });

                        button_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.cancel();
                            }
                        });
                    }
                });

                button_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.cancel();
                    }
                });
            }
        });

        final String finalCurrentUserId2 = currentUserId;
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShoppingList.this);
                View fillableView = getLayoutInflater().inflate(R.layout.shopping_list_fillable, null);
                final EditText shoppingItem = (EditText) fillableView.findViewById(R.id.itemName);
                Button enterItemButton = (Button) fillableView.findViewById(R.id.enterItemButton);

                enterItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!shoppingItem.getText().toString().isEmpty()){
                            //Check to see if item has already been added
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(finalCurrentUserId2);
                            DatabaseReference users = root;
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Boolean foundData = false;
                                    for(DataSnapshot key: snapshot.child("ShoppingList").getChildren()){
                                        if(shoppingItem.getText().toString().equals(key.child("Item Name").getValue().toString())){
                                            foundData = true;
                                        }
                                        else{
                                            itemKeyMap.put(key.getValue().toString(), key.getKey().toString());
                                        }

                                        for(DataSnapshot item: snapshot.child("VirtuFridge").getChildren()){
                                            if(item.child("Item Name").getValue().equals(shoppingItem.getText().toString())){
                                                foundData = true;
                                            }
                                        }
                                    }
                                    if (foundData) {
                                        Toast.makeText(ShoppingList.this, "Item already exists", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String key = myRef.child("ShoppingList").push().getKey();
                                        myRef.child("ShoppingList").child(key).child("Item Name").setValue(shoppingItem.getText().toString());
                                        shoppingItem.getText().clear();
                                        shoppingItem.setText("");
                                        Toast.makeText(ShoppingList.this, "Item added to your list", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(ShoppingList.this, "Please enter item", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mBuilder.setView(fillableView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        //Creates a list view of all items for the customer to read from the database and display
        listview=(ListView)findViewById(R.id.listView);
        listview.setClickable(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String key = keylist.get(position);
                myRef.child("ShoppingList").child(key).child("Item Name").removeValue();
            }
        });

        listview.setAdapter(adapter);
        final String finalCurrentUserId = currentUserId;
        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(currentUserId);
        DatabaseReference users = root.child("ShoppingList");
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                String shoppingItem = dataSnapshot.child("Item Name").getValue(String.class);
                list.add(shoppingItem);
                keylist.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
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
                        adapter.remove(adapter.getItem(i));
                        keylist.remove(dataSnapshot.getKey());
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
