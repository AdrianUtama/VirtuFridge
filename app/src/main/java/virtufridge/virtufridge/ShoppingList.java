package virtufridge.virtufridge;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    ArrayList<String> list=new ArrayList<>();
    ArrayList<String> keylist = new ArrayList<>();
    final HashMap<String, String> itemKeyMap = new HashMap<>();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

    TextView tv_date;
    Calendar mCurrentDate;
    boolean userEnteredData;
    int day, month, year;

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

        GregorianCalendar trial = new GregorianCalendar(2018,11-1,25);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy");
        Log.d("Date: ", sdf.format(trial.getTime()));
        String currentUserId = "";
        if(currentUser != null){
            currentUserId = currentUser.getUid();
            Log.d("Fucking Log In", currentUser.getUid());
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
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,list);
        //adapter.notifyDataSetChanged();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(currentUserId);

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
                                month = mCurrentDate.get(Calendar.MONTH) + 1;
                                year = mCurrentDate.get(Calendar.YEAR);



                                tv_date.setText(Integer.toString(month)+"/"+Integer.toString(day)+"/"+Integer.toString(year));

                                for(String item : list){
                                    Log.d("ItemListItem:",item);
                                }
                                for(String item: tempItemList){
                                    Log.d("tempItemListItem",item);
                                }
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
                                                Log.d("Alert dimissed", "Dismissed");
                                            }
                                            else{
                                            Log.d("Button Click", "Button Clicked");

                                            Log.d("TV Month:", Integer.toString(month));
                                            Log.d("TV Day:", Integer.toString(day));
                                            Log.d("TV Year:", Integer.toString(year));
                                            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(finalCurrentUserId3);
                                            DatabaseReference users = root.child("ShoppingList");
                                            Log.d("TempItemList 0",tempItemList.get(0));


                                            final Calendar cal = Calendar.getInstance();
                                            cal.clear();
                                            cal.set(Calendar.YEAR, year);
                                            cal.set(Calendar.MONTH, month - 1);
                                            cal.set(Calendar.DATE, day);


                                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot item: dataSnapshot.getChildren()) {
                                                        Log.d("Key Name", item.getKey());
                                                        Log.d("Value of Key Name", item.child("Item Name").getValue(String.class));
                                                        Log.d("TempItemListSize", Integer.toString(tempItemList.size()));
                                                        if(tempItemList.size() != 0){
                                                            if(item.child("Item Name").getValue(String.class).equals(tempItemList.get(0))){
                                                                Log.d("Inside the dataChange", "True");
                                                                //Add the expiration date to the database
                                                                //myRef.child("ShoppingList").child(item.getKey()).child("Expiration Date").setValue(cal.getTime());
                                                                Log.d("Adding Expiration", "True");
                                                                myRef.child("VirtuFridge").child(item.getKey()).child("Item Name").setValue(tempItemList.get(0));
                                                                myRef.child("VirtuFridge").child(item.getKey()).child("Expiration Date").setValue(cal);
                                                                myRef.child("ShoppingList").child(item.getKey()).removeValue();
                                                                Toast.makeText(ShoppingList.this, "Item moved to VirtuFridge", Toast.LENGTH_SHORT).show();
                                                                tempItemList.remove(tempItemList.get(0));
                                                                if(tempItemList.size() == 0){
                                                                    alert.cancel();
                                                                    Log.d("Alert dimissed", "Dismissed");
                                                                }
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
                                            else{

                                                if(!tempItemList.isEmpty()){
                                                    itemForExpiration.setText("Enter Expiration of Item: " + tempItemList.get(0));
                                                }
                                            }


//                                            users.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot snapshot) {
//                                                    Log.d("Inside onDataChange", "Inside onDataChange Expiration");
//                                                    for(DataSnapshot key: snapshot.getChildren()){
//                                                        Log.d("TIL.get(0)", tempItemList.get(0));
//                                                        Log.d("Item Key",key.getKey().toString());
//                                                        Log.d("Item",key.child("Item Name").getValue().toString());
//                                                        itemKeyMap.put(key.getKey().toString(), key.child("Item Name").getValue().toString());
//                                                        if (key.child("Item Name").getKey().toString().equals(tempItemList.get(0))){
//                                                            Log.d("TempItemList 0:",tempItemList.get(0));
//                                                            Calendar cal = Calendar.getInstance();
//                                                            cal.clear();
//
//                                                            cal.set(Calendar.YEAR, year);
//                                                            cal.set(Calendar.MONTH, month);
//                                                            cal.set(Calendar.DATE, day);
//                                                            myRef.child("ShoppingList").child(key.getKey()).child("Expiration Date").setValue(cal.getTime());
//                                                        }
//                                                    }
////                                                    if (foundData) {
////                                                        Toast.makeText(ShoppingList.this, "Item already exists", Toast.LENGTH_SHORT).show();
////                                                    }else{
////                                                        String key = myRef.child("ShoppingList").push().getKey();
////                                                        myRef.child("ShoppingList").child(key).setValue(shoppingItem.getText().toString());
////                                                        shoppingItem.getText().clear();
////                                                        Toast.makeText(ShoppingList.this, "Item added to your list", Toast.LENGTH_SHORT).show();
////
////                                                    }
//                                                }
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });

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
                                                monthOfYear += 1;
                                                tv_date.setText(Integer.toString(monthOfYear)+"/"+Integer.toString(dayOfMonth)+"/"+Integer.toString(yearOfYear));
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
                                DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(finalCurrentUserId1);
                                root.child("AllItems").setValue(list);
                                DatabaseReference users = root.child("VirtuFridge");
                                Intent intent = new Intent(view.getContext(), VirtuPage.class);
                                for (int itemIndex = 0; itemIndex < keylist.size(); itemIndex++){
                                    users.child(keylist.get(itemIndex)).setValue(list.get(itemIndex));
                                }
                                //intent.putExtra("key", theString);
                                startActivity(intent);
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
                            DatabaseReference users = root.child("ShoppingList");
                            Log.d("Inside onClick", "Inside onClick");
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
//                                    Log.d("Entering Shit", "Entering the twilight zone");
//                                    Log.d("Entering Shit", snapshot.getKey().toString());
                                    //DataSnapshot userDataSnapshot = snapshot.child(finalCurrentUserId1);
                                    Log.d("Inside onDataChange", "Inside onDataChange");
                                    Boolean foundData = false;
                                    for(DataSnapshot key: snapshot.getChildren()){
                                        Log.d("Key",key.getValue().toString());
                                        Log.d("Item",key.getKey().toString());
                                        itemKeyMap.put(key.getValue().toString(), key.getKey().toString());
//                                        Log.d("Stupid Fuck", "Here is the fucking key");
//                                        Log.d("Stupid Fuck", key.getValue().toString());
//                                        Log.d("Stupid Fuck", "Here is the string for shopping item");
//                                        Log.d("Stupid Fuck", shoppingItem.getText().toString());
                                        if (key.getValue().equals(shoppingItem.getText().toString())){
                                            foundData = true;
                                            }
                                    }
                                    if (foundData) {
                                        Toast.makeText(ShoppingList.this, "Item already exists", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String key = myRef.child("ShoppingList").push().getKey();
                                        myRef.child("ShoppingList").child(key).child("Item Name").setValue(shoppingItem.getText().toString());
                                        shoppingItem.getText().clear();
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
                //DataSnapshot userDataSnapshot = dataSnapshot.child(finalCurrentUserId);
//                if(s == null){
//                    Log.d("Null Error", "The String for DataSnapshot is null");
//                    return;
//                }
                //DataSnapshot userDataSnapshot = dataSnapshot.child(finalCurrentUserId);
                String key = dataSnapshot.getKey();
                Log.d("Key Value: ", key);
                String shoppingItem = dataSnapshot.child("Item Name").getValue(String.class);
                list.add(shoppingItem);
//                for (String value : list){
//                    Log.d("Items inside list", "Value is " + value);
//                }
                keylist.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                Log.d("Log 5","After adapter change");
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
