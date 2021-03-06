package com.point2points.kdusurveysystem.adapter.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.point2points.kdusurveysystem.Fragment.LecturerFragmentPagerActivity;
import com.point2points.kdusurveysystem.Login;
import com.point2points.kdusurveysystem.RecyclerView.RecyclerViewLecturer;
import com.point2points.kdusurveysystem.adapter.util.RecyclerLetterIcon;
import com.point2points.kdusurveysystem.admin.AdminToolbarDrawer;
import com.point2points.kdusurveysystem.datamodel.Admin;
import com.point2points.kdusurveysystem.datamodel.Lecturer;
import com.point2points.kdusurveysystem.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.StringTokenizer;

public class RecyclerLecturerTabAdapter extends RecyclerSwipeAdapter<RecyclerLecturerTabAdapter.SimpleViewHolder> {

    private static final String TAG = "RecyclerLecturerAdapter";

    private static final String EXTRA_LECTURER_NAME = "com.point2points.kdusurveysystem.lecturer_name";
    private static final String EXTRA_LECTURER_ID = "com.point2points.kdusurveysystem.lecturer_id";

    static DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    static Query query;

    //relogin after delete data
    Admin admin = new Admin();

    String UID;
    String adminEmail;

    private Activity mActivity;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static boolean lecturerRetrieval = false;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, RecyclerViewLecturer.class);
        lecturerRetrieval = true;
        return intent;
    }

    public static String lecturerNameRetrieval(Intent result) {
        return result.getStringExtra(EXTRA_LECTURER_NAME);
    }

    public static String lecturerIDRetrieval(Intent result) {
        return result.getStringExtra(EXTRA_LECTURER_ID);
    }

    private void setLecturerName(String lecturerName, String lecturerID) {
        AdminToolbarDrawer.tabIdentifier = AdminToolbarDrawer.tabIdentifierMutex;
        Intent data = new Intent();
        data.putExtra(EXTRA_LECTURER_NAME, lecturerName);
        data.putExtra(EXTRA_LECTURER_ID, lecturerID);
        mActivity.setResult(Activity.RESULT_OK, data);
        mActivity.finish();
        Log.e("set ", "lecturer name");
    }

    public class SimpleViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView textViewFullName;
        TextView textViewID;
        TextView textViewEmail;
        TextView textViewPoint;
        TextView textViewUid;
        ImageButton buttonDelete;
        ImageButton buttonEdit;
        ImageView letterimage;

        public SimpleViewHolder(final View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            textViewFullName = (TextView) itemView.findViewById(R.id.lecturer_fullname_text_view);
            textViewID = (TextView) itemView.findViewById(R.id.lecturer_ID_text_view);
            textViewEmail = (TextView) itemView.findViewById(R.id.lecturer_email_text_view);
            textViewPoint = (TextView) itemView.findViewById(R.id.lecturer_point_text_view);
            buttonDelete = (ImageButton) itemView.findViewById(R.id.delete);
            buttonEdit = (ImageButton) itemView.findViewById(R.id.edit);
            letterimage = (ImageView) itemView.findViewById(R.id.letter_icon);
            textViewUid = (TextView) itemView.findViewById(R.id.lecturer_uid_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lecturerItemOnClickListener(view);
                }
            });
        }
    }

    private Context mContext;
    private static ArrayList<Lecturer> lecturers = new ArrayList<>();
    public static ArrayList<Lecturer> LecturerDataset = new ArrayList<>();

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);
    public RecyclerLecturerTabAdapter(Context context) {
        this.mContext = context;
        mActivity = (Activity) mContext;

        //String key = ref.push().getKey();
        FirebaseLecturerDataRetrieval();
    }

    public static void sortingData(int sortoption) {

        //FirebaseLecturerDataRetrieval();

        switch (sortoption) {
            case 1:
                Collections.sort(LecturerDataset, new Comparator<Lecturer>() {
                    @Override
                    public int compare(Lecturer lecturer1, Lecturer lecturer2) {
                        return (lecturer1.getFullName().substring(0, 1).toUpperCase() + lecturer1.getFullName().substring(1)).compareTo(lecturer2.getFullName().substring(0, 1).toUpperCase() + lecturer2.getFullName().substring(1));
                    }
                });

                break;
            case 2:
                Collections.sort(LecturerDataset, new Comparator<Lecturer>() {
                    @Override
                    public int compare(Lecturer lecturer1, Lecturer lecturer2) {
                        return (lecturer1.getFullName().substring(0, 1).toUpperCase() + lecturer1.getFullName().substring(1)).compareTo(lecturer2.getFullName().substring(0, 1).toUpperCase() + lecturer2.getFullName().substring(1));
                    }
                });
                Collections.reverse(LecturerDataset);
                break;
            case 3:
                Collections.sort(LecturerDataset, new Comparator<Lecturer>() {
                    @Override
                    public int compare(Lecturer lecturer1, Lecturer lecturer2) {
                        return lecturer1.getDate().compareTo(lecturer2.getDate());
                    }
                });
                Collections.reverse(LecturerDataset);
                break;
            case 4:
                Collections.sort(LecturerDataset, new Comparator<Lecturer>() {
                    @Override
                    public int compare(Lecturer lecturer1, Lecturer lecturer2) {
                        return lecturer1.getDate().compareTo(lecturer2.getDate());
                    }
                });
                break;
            default:
                break;
        }
        RecyclerViewLecturer.notifyDataChanges();
    }

    public static void LecturerArrayListUpdate(ArrayList updatedLecturers) {
        lecturers = updatedLecturers;
        RecyclerViewLecturer.notifyDataChanges();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lecturer_recycler_view_items, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        final Lecturer item = LecturerDataset.get(position);
        String fullname = (item.fullName).substring(0, 1).toUpperCase() + (item.fullName).substring(1);
        String email = item.emailAddress;
        String ID = item.lecturer_ID;
        String point = item.point;
        String uid = item.uid;
        StringTokenizer tokens = new StringTokenizer(point, ".");
        point = tokens.nextToken();

        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Swing).duration(1000).delay(100).playOn(layout.findViewById(R.id.edit));
                YoYo.with(Techniques.Swing).duration(1000).delay(100).playOn(layout.findViewById(R.id.delete));
            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                if (!lecturerRetrieval) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + viewHolder.textViewFullName.getText().toString());
                    Intent intent = LecturerFragmentPagerActivity.newIntent(viewHolder.swipeLayout.getContext(), viewHolder.textViewUid.getText().toString());
                    viewHolder.swipeLayout.getContext().startActivity(intent);
                } else if (lecturerRetrieval) {
                    final Toast toastOnDoubleClick = Toast.makeText(mContext, viewHolder.textViewFullName.getText().toString() + " Selected.", Toast.LENGTH_SHORT);
                    toastOnDoubleClick.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toastOnDoubleClick.cancel();
                        }
                    }, 1500);

                    String lecturerName = viewHolder.textViewFullName.getText().toString();
                    String lecturerID = viewHolder.textViewID.getText().toString();
                    lecturerRetrieval = false;
                    setLecturerName(lecturerName, lecturerID);
                }
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final Context context = view.getContext();

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Confirm deleting " + item.getLecturer_ID() + "?");

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Log.d("Deletion", viewHolder.textViewUid.getText().toString());
                                //removeUserFromAuth(item.getEmailAddress(), item.getPassword());

                                removeUserFromAuth(item.getEmailAddress(), item.getPassword());

                                ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("users").child("lecturer").child(viewHolder.textViewUid.getText().toString()).removeValue();
                                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                                lecturers.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, LecturerDataset.size());
                                mItemManger.closeAllItems();
                                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewFullName.getText().toString() + "!", Toast.LENGTH_SHORT).show();
                                LecturerDataset = lecturers;

                                RecyclerViewLecturer.offProgressBar();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LecturerFragmentPagerActivity.newIntent(viewHolder.swipeLayout.getContext(), viewHolder.textViewUid.getText().toString());
                viewHolder.swipeLayout.getContext().startActivity(intent);
            }
        });

        TextDrawable drawable = RecyclerLetterIcon.GenerateRecyclerLetterIcon(fullname, 64);

        viewHolder.letterimage.setImageDrawable(drawable);
        viewHolder.textViewFullName.setText(fullname);
        viewHolder.textViewID.setText(ID);
        viewHolder.textViewEmail.setText(email);
        viewHolder.textViewPoint.setText("Point: " + point);
        viewHolder.textViewUid.setText(uid);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return LecturerDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        LecturerDataset = new ArrayList<Lecturer>();

        if (charText.length() == 0) {
            LecturerDataset = lecturers;
        } else {
            for (Lecturer lecturer : lecturers) {
                if (lecturer.getFullName().toLowerCase(Locale.getDefault()).contains(charText)
                        || lecturer.getEmailAddress().toLowerCase(Locale.getDefault()).contains(charText)
                        || lecturer.getLecturer_ID().toLowerCase(Locale.getDefault()).contains(charText)
                        || lecturer.getUsername().toLowerCase(Locale.getDefault()).contains(charText)
                        || lecturer.getSchoolName().toLowerCase(Locale.getDefault()).contains(charText)
                        || lecturer.getSchoolNameShort().toLowerCase(Locale.getDefault()).contains(charText)) {
                    LecturerDataset.add(lecturer);
                }
            }
        }
        RecyclerViewLecturer.notifyDataChanges();
    }

    public static void FirebaseLecturerDataRetrieval() {
        ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("users").child("lecturer");
        query = ref;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                RecyclerViewLecturer.onProgressBar();
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    boolean found = false;
                    for (Lecturer lecturer : lecturers) {
                        if (lecturer.getUid() == postSnapshot.getValue(Lecturer.class).getUid()) {
                            found = true;
                        }
                    }
                    if (!found) {
                        lecturers.add(postSnapshot.getValue(Lecturer.class));
                        Log.e("Get Data", (postSnapshot.getValue(Lecturer.class).getFullName()));
                    }
                }
                if (lecturers.size() == snapshot.getChildrenCount()) {

                    LecturerDataset = lecturers;
                    Collections.sort(LecturerDataset, new Comparator<Lecturer>() {
                        @Override
                        public int compare(Lecturer lecturer1, Lecturer lecturer2) {
                            return lecturer1.getDate().compareTo(lecturer2.getDate());
                        }
                    });
                    Collections.reverse(LecturerDataset);

                    RecyclerViewLecturer.offProgressBar();
                    RecyclerViewLecturer.notifyDataChanges();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });
    }

    public void removeUserFromAuth(final String email, final String password) {

        RecyclerViewLecturer.onProgressBar();

        //FirebaseUser Adminuser = FirebaseAuth.getInstance().getCurrentUser();
        //UID = Adminuser.getUid(); // Get logged in admin's user ID through checking current logged in user's ID. Doesn't work because other logins can take place through account creation.

        UID = Login.loginUID;  // Get logged in admin's user ID from Login class
        adminEmail = Login.loginEmail;

        FirebaseAuth.getInstance().signOut();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                        } else {
                            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(email, password);

                            currentUser.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            currentUser.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "User account deleted.");
                                                            }
                                                        }
                                                    });

                                            reauthAdmin();
                                        }
                                    });
                        }
                    }
                });
    }

    public void reauthAdmin() {
        ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("users").child("admin");
        query = ref;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Log.d(TAG, "EVENT LISTENER RUN"); // Troubleshooting.
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Log.d(TAG, "DATA SNAPSHOT RUN"); // Troubleshooting.
                    if (UID.equals(postSnapshot.getValue(Admin.class).getAdminUid())) {
                        admin = postSnapshot.getValue(Admin.class);
                        Log.e("Get Data", (postSnapshot.getValue(Admin.class).getAdminName()));

                        //Log.d(TAG, admin.getAdminEmail() + " | " + admin.getAdminPassword()); // Troubleshooting.
                        mAuth.signInWithEmailAndPassword(admin.getAdminEmail(), admin.getAdminPassword())
                                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Log.d(TAG, "Error reauthenticating!");
                                        } else {
                                            Log.d(TAG, "Reauthenticated to " + admin.getAdminEmail() + " admin account");
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });
    }

    public void lecturerItemOnClickListener(View view) {

        final Toast toastItemOnClick;

        if (!lecturerRetrieval) {
            toastItemOnClick = Toast.makeText(mContext, "Double Tap to Edit the data", Toast.LENGTH_SHORT);
            toastItemOnClick.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toastItemOnClick.cancel();
                }
            }, 500);
        } else if (lecturerRetrieval) {
            toastItemOnClick = Toast.makeText(mContext, "Double Tap to select the data", Toast.LENGTH_SHORT);
            toastItemOnClick.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toastItemOnClick.cancel();
                }
            }, 500);
        }
    }
}
