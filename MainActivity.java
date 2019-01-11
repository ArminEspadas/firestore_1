package com.example.armin.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final String KEY_TITLE= "title";
    private static final String KEY_DESCRIPTION= "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference noteRef= db.collection("Notebook").document("My First Note");
    private ListenerRegistration noteListener;
    private CollectionReference notebookRef= db.collection("Notebook");

    private EditText editTextPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle= findViewById(R.id.edit_text_title);
        editTextPriority = findViewById(R.id.edit_text_priority);
        editTextDescription= findViewById(R.id.edit_text_description);
        textViewData= findViewById(R.id.text_view_data);

    }

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!= null){

                    return;
                }

                String data= "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                    Note note = documentSnapshot.toObject(Note.class);

                    note.setDocumentId(documentSnapshot.getId());

                    String documentId = note.getDocumentId();

                    String title = note.getTitle();
                    String description = note.getDescription();

                    int priority = note.getPriority();
                    data +="ID: " + documentId +
                            "\nTitle: " + title + "\nDescription: "+ description + "\n\n"
                            + "\nPriority: " + priority + "\n\n";

                    //Modificar el archivo específico
                //    notebookRef.document(documentId);
                }

                textViewData.setText(data);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        noteListener.remove();
    }

    public void addNote(View v){

        String title= editTextTitle.getText().toString();
        String description= editTextDescription.getText().toString();

        if ( editTextPriority.length()== 0){

            editTextPriority.setText("0");
        }

        int priority = Integer.parseInt(editTextPriority.getText().toString());

        Note note = new Note(title, description, priority);

        notebookRef.add(note);


    }


    public void loadNotes( View v){


       Task task1 = notebookRef.whereLessThan("priority", 2)
                .orderBy("priority")
                .get();

        Task task2 = notebookRef.whereGreaterThan("priority", 5)
                .orderBy("priority")
                .get();

        Task <List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {

                String data ="";

                for (QuerySnapshot queryDocumentSnapshots : querySnapshots ) {


                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());

                        String documentId = note.getDocumentId();

                        String title = note.getTitle();
                        String description = note.getDescription();
                        int priority = note.getPriority();

                        data += "ID: " + documentId +
                                "\nTitle: " + title + "\nDescription: " + description + "\n\n"
                                + "\nPriority: " + priority + "\n\n";

                    }
                }
                textViewData.setText(data);


            }
        });
    }

  public void saveNote(View v){

       /* String title= editTextTitle.getText().toString();
        String description= editTextDescription.getText().toString();


        Note note = new Note(title, description);


        noteRef.set(note)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });
        */


    }

    public  void updateDescription (View v){

        String description = editTextDescription.getText().toString();

        //Map<String, Object> note = new HashMap<>() ;

       // note.put(KEY_DESCRIPTION, description);
       //noteRef.set(note, SetOptions.merge());

        noteRef.update(KEY_DESCRIPTION, description);


    }


    public void deleteDescription( View v){

     //   Map<String, Object> note = new HashMap<>();
     //   note.put(KEY_DESCRIPTION, FieldValue.delete());

    //    noteRef.update(note);
        noteRef. update(KEY_DESCRIPTION, FieldValue.delete() );

    }


    public void deleteNote (View v){

        noteRef. delete();

    }

    public void loadNote( View v){

        /*

        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                   if (documentSnapshot.exists()){

                       Note note = documentSnapshot.toObject(Note.class);

                       String title = note.getTitle();
                       String description = note.getDescription();
                       textViewData.setText("Title: " + title + "\n" + "Description: "+ description);

                   }else{
                       Toast.makeText(MainActivity.this, "Document doesn´t exist!", Toast.LENGTH_SHORT).show();

                   }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());

            }
        });
 */


    }

}
