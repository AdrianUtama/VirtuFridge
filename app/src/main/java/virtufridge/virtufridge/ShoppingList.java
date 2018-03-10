package virtufridge.virtufridge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {
    //Trial Push Edit 2
    ArrayList<String> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView listview;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("ShoppingList");



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = myRef.child("ShoppingList").push().getKey();
                myRef.child(key).setValue("Apples");


            }
        });
        //Creates a list view of all items for the customer to read from the database and
        listview=(ListView)findViewById(R.id.listView);
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getKey();

                String shoppingItem = dataSnapshot.getValue(String.class);
                list.add(shoppingItem);
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
