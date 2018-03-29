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


public class ShoppingList extends AppCompatActivity{
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
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

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        final ListView listview;
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,list);
        adapter.notifyDataSetChanged();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("ShoppingList");

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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShoppingList.this);
                View fillableView = getLayoutInflater().inflate(R.layout.shopping_list_fillable, null);
                final EditText shoppingItem = (EditText) fillableView.findViewById(R.id.itemName);
                Button enterItemButton = (Button) fillableView.findViewById(R.id.enterItemButton);

                enterItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!shoppingItem.getText().toString().isEmpty()){
                            //Check to see if item has already been added
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference users = root.child("ShoppingList");

                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
//                                    Log.d("Entering Shit", "Entering the twilight zone");
//                                    Log.d("Entering Shit", snapshot.getKey().toString());
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
                                        myRef.child(key).setValue(shoppingItem.getText().toString());
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
                myRef.child(key).removeValue();
            }
        });
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("Stupid Fuck", "Inside On ListView Click");
//                Object itemElement = listview.getItemAtPosition(position);
//                String itemElementString =(String)itemElement;//As you are using Default String Adapter
//                Log.d("Stupid Fuck", itemElementString);
//                Log.d("TESTING FUCKING KEY", itemKeyMap.get(itemElementString));
//                myRef.child("ShoppingList").child(itemKeyMap.get(itemElementString)).removeValue();
//                adapter.notifyDataSetChanged();
//                //new code below
//
//
//            }
//        });
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Object listViewElement = listview.getItemAtPosition(position);
//                final String elementString =(String)listViewElement;//As you are using Default String Adapter
//                DatabaseReference root = FirebaseDatabase.getInstance().getReference();
//                final DatabaseReference users = root.child("ShoppingList");
////                users.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot snapshot) {
//////                                    Log.d("Entering Shit", "Entering the twilight zone");
//////                                    Log.d("Entering Shit", snapshot.getKey().toString());
////                        Boolean foundData = false;
////                        for(DataSnapshot key: snapshot.getChildren()){
//////                                        Log.d("Stupid Fuck", "Here is the fucking key");
//////                                        Log.d("Stupid Fuck", key.getValue().toString());
//////                                        Log.d("Stupid Fuck", "Here is the string for shopping item");
//////                                        Log.d("Stupid Fuck", shoppingItem.getText().toString());
////                            if (key.getValue().equals(elementString)){
////                                users.child(key.toString()).removeValue();
////                            }
////                        }
////                    }
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////
////                    }
////                });
//
//                Log.d("Stupid Delete Shit", "Item was fucking clicked");
//                Log.d("Stupid Delete Shit", elementString);
//            }
//        });

        listview.setAdapter(adapter);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getKey();

                String shoppingItem = dataSnapshot.getValue(String.class);
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
                String shoppingItem = dataSnapshot.getValue(String.class);
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
