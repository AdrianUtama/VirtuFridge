package virtufridge.virtufridge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {
    //Trial Push Edit 2
    ArrayList<String> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ListView listview;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("ShoppingList");



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference("ShoppingList");
                            DatabaseReference users = root.child("ShoppingList");
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.child("ShoppingList").child(shoppingItem.getText().toString()).exists()) {
                                        Toast.makeText(ShoppingList.this, "Item is already in your list", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String key = myRef.child("ShoppingList").push().getKey();
                                        myRef.child(key).setValue(shoppingItem.getText().toString());
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
