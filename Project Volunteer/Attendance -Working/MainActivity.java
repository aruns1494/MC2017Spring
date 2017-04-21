package varsha.example.com.firebasetutorial;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    EditText userID, eventID;
    Button button;
    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = (EditText)findViewById(R.id.uID);
        eventID = (EditText)findViewById(R.id.eID);

        button = (Button) findViewById(R.id.button);
        status = (TextView) findViewById(R.id.result);

        //boolean flag = false;
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final String uID = userID.getText().toString();
                final String evID = eventID.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Event2").child(evID).child("userList");
                // IF EVENT ID exists and USER is registered for the EVENT ID
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String S = dataSnapshot.getValue(String.class);
                        //status.setText(evID+S);
                        String[] listS = S.split(",");
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(listS));
                        if(list.contains(uID))
                        {
                            status.setTextColor(Color.GREEN);
                            status.setText(uID+"Success");
                        }
                        else
                        {
                            status.setTextColor(Color.RED);
                            status.setText("FAILURE");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
