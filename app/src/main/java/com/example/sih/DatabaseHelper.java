package com.example.sih;

import android.content.Context;
import android.net.Uri;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper {
    private final FirebaseFirestore firestore;
    private final StorageReference storage;

    public DatabaseHelper(Context ctx){
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public interface SaveCallback { void onSuccess(); void onFailure(Exception e); }
    public interface BreedDetailsCallback { void onResult(String description, String imageUrl); void onFailure(Exception e); }
    public interface SyncAllCallback { void onComplete(); }
    public interface EntriesCallback { void onResult(List<Entry> entries); }

    public static class Entry {
        public String id, breed, timestamp;
        public boolean synced;
        public float confidence;
        public Entry(String id,String breed,String timestamp,boolean synced,float confidence){this.id=id;this.breed=breed;this.timestamp=timestamp;this.synced=synced;this.confidence=confidence;}
    }

    public void saveBreedResult(String breed, float confidence, Uri imageUri, SaveCallback cb) {
        String imgName = "images/"+ UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.child(imgName);
        ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
            Map<String,Object> data = new HashMap<>();
            data.put("breed", breed);
            data.put("confidence", confidence);
            data.put("timestamp", Timestamp.now());
            data.put("imageUrl", uri.toString());
            data.put("synced", true);
            String uid = "demoUser"; // replace with FirebaseAuth.getInstance().getCurrentUser().getUid()
            firestore.collection("users").document(uid).collection("entries").add(data)
                    .addOnSuccessListener(docRef -> cb.onSuccess())
                    .addOnFailureListener(cb::onFailure);
        }).addOnFailureListener(cb::onFailure)).addOnFailureListener(cb::onFailure);
    }

    public void getBreedDetails(String breed, BreedDetailsCallback cb) {
        firestore.collection("breeds").document(breed.toLowerCase()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) cb.onResult(doc.getString("description"), doc.getString("imageUrl"));
                    else cb.onResult(null,null);
                }).addOnFailureListener(cb::onFailure);
    }

    public void getLocalEntries(EntriesCallback cb) {
        // TODO: integrate Room local DB. For prototype return empty list.
        cb.onResult(new ArrayList<>());
    }

    public void syncAllUnsynced(SyncAllCallback cb) {
        // TODO: implement actual sync. For now call complete.
        cb.onComplete();
    }
}
