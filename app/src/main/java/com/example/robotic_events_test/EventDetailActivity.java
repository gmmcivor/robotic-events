package com.example.robotic_events_test;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault());
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = FirebaseFirestore.getInstance();

        ImageView image   = findViewById(R.id.detailImage);
        TextView title    = findViewById(R.id.detailTitle);
        TextView when     = findViewById(R.id.detailWhen);
        TextView where    = findViewById(R.id.detailWhere);
        TextView price    = findViewById(R.id.detailPrice);
//        TextView status   = findViewById(R.id.detailStatus);
        TextView category = findViewById(R.id.detailCategory);
        TextView org      = findViewById(R.id.detailOrganizer);
        TextView cap      = findViewById(R.id.detailCapacity);
        TextView desc     = findViewById(R.id.detailDescription);
        TextView waitlistCount = findViewById(R.id.detailWaitlistCount); // NEW

        String id          = getIntent().getStringExtra("id");
        String t           = safe(getIntent().getStringExtra("title"));
        String d           = safe(getIntent().getStringExtra("description"));
        long dateTime      = getIntent().getLongExtra("dateTime", 0L);
        String loc         = safe(getIntent().getStringExtra("location"));
        String cat         = safe(getIntent().getStringExtra("category"));
        String organizerId = safe(getIntent().getStringExtra("organizerId"));
        int totalCapacity  = getIntent().getIntExtra("totalCapacity", 0);
        String st          = safe(getIntent().getStringExtra("status"));
        int imgResId       = getIntent().getIntExtra("imageResId", 0);
        double pr          = getIntent().getDoubleExtra("price", 0.0);

        if (imgResId != 0) image.setImageResource(imgResId);
        title.setText(t.isEmpty() ? "(Untitled)" : t);
        when.setText(dateTime > 0 ? sdf.format(new Date(dateTime)) : "");
        where.setText(loc);
        price.setText(pr > 0 ? String.format(Locale.getDefault(), "$%.2f", pr) : "Free");
//        status.setText(st);
        category.setText(cat);
        org.setText(organizerId);
        cap.setText(String.valueOf(totalCapacity));
        desc.setText(d);

        // NEW: Load waitlist count
        loadWaitlistCount(id, waitlistCount);
    }

    private void loadWaitlistCount(String eventId, TextView waitlistCount) {
        if (eventId == null || eventId.isEmpty()) {
            waitlistCount.setText("Waitlist: 0 users");
            return;
        }

        db.collection("waitlists")
                .whereEqualTo("eventId", eventId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        waitlistCount.setText("Waitlist: Error loading");
                        return;
                    }

                    if (value != null) {
                        int count = value.size();
                        waitlistCount.setText("Waitlist: " + count + (count == 1 ? " user" : " users"));
                    } else {
                        waitlistCount.setText("Waitlist: 0 users");
                    }
                });
    }

    private String safe(String s) { return s == null ? "" : s; }
}