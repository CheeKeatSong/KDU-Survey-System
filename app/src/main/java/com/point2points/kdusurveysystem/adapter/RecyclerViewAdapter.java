package com.point2points.kdusurveysystem.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.point2points.kdusurveysystem.Lecturer;
import com.point2points.kdusurveysystem.R;
import com.point2points.kdusurveysystem.model.ExampleModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    public interface Listener {
        void onExampleModelClicked(ExampleModel model);
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView textViewFullName;
        TextView textViewID;
        TextView textViewEmail;
        TextView textViewPoint;
        ImageButton buttonDelete;
        ImageButton  buttonEdit;
        ImageView letterimage;


        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            textViewFullName = (TextView) itemView.findViewById(R.id.lecturer_fullname_text_view);
            textViewID = (TextView) itemView.findViewById(R.id.lecturer_ID_text_view);
            textViewEmail = (TextView) itemView.findViewById(R.id.lecturer_email_text_view);
            textViewPoint = (TextView) itemView.findViewById(R.id.lecturer_point_text_view);
            buttonDelete = (ImageButton ) itemView.findViewById(R.id.delete);
            buttonEdit = (ImageButton ) itemView.findViewById(R.id.edit);
            letterimage = (ImageView) itemView.findViewById(R.id.letter_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + textViewFullName.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textViewFullName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //SortedList
   /* private final SortedList<ExampleModel> mSortedList = new SortedList<>(ExampleModel.class, new SortedList.Callback<ExampleModel>() {
        @Override
        public int compare(ExampleModel a, ExampleModel b) {
            return mComparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(ExampleModel oldItem, ExampleModel newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(ExampleModel item1, ExampleModel item2) {
            return item1.getId() == item2.getId();
        }
    });

    //we first need a way to add or remove items to the Adapter. For this purpose we can add methods to the Adapter which allow us to add and remove items to the SortedList
    public void add(ExampleModel model) {
        mSortedList.add(model);
    }

    public void remove(ExampleModel model) {
        mSortedList.remove(model);
    }

    public void add(List<ExampleModel> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<ExampleModel> models) {
        mSortedList.beginBatchedUpdates();
        for (ExampleModel model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<ExampleModel> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final ExampleModel model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }
    //add or remove end

    private final LayoutInflater mInflater;
    private final Comparator<ExampleModel> mComparator;*/

    private Context mContext;
    private ArrayList<Lecturer> mDataset;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context, ArrayList<Lecturer> objects) {
        this.mContext = context;
        this.mDataset = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_items, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        Lecturer item = mDataset.get(position);
        String fullname = (item.fullName).substring(0, 1).toUpperCase() + (item.fullName).substring(1);
        String email = item.emailAddress;
        String ID = item.lecturer_ID;
        String point = item.point;
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
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewFullName.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewFullName.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        String firstletter = fullname.substring(0,1);

        String color = "#ff0400";

        switch (firstletter){
            case "A":color = "#ff0400";break;
            case "B":color = "#ff8800";break;
            case "C":color = "#ffdd00";break;
            case "D":color = "#bfeb00";break;
            case "E":color = "#59ff00";break;
            case "F":color = "#00ff7b";break;
            case "G":color = "#00eede";break;
            case "H":color = "#0095ff";break;
            case "I":color = "#0015ff";break;
            case "J":color = "#a200ed";break;
            case "K":color = "#ff12ef";break;
            case "L":color = "#e500a1";break;
            case "M":color = "#ff655c";break;
            case "N":color = "#ffcc00";break;
            case "O":color = "#b80c0c";break;
            case "P":color = "#b8590c";break;
            case "Q":color = "#27850a";break;
            case "R":color = "#ae81f2";break;
            case "S":color = "#97a0ff";break;
            case "T":color = "#642f95";break;
            case "U":color = "#ce40c2";break;
            case "V":color = "#f584c8";break;
            case "W":color = "#01517d";break;
            case "X":color = "#d5d5d5";break;
            case "Y":color = "#ffc756";break;
            case "Z":color = "#239fa5";break;
        }

        int android_color = Color.parseColor(color);

                TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .useFont(Typeface.DEFAULT)
                .fontSize(64)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(firstletter,android_color);

        viewHolder.letterimage.setImageDrawable(drawable);
        viewHolder.textViewFullName.setText(fullname);
        viewHolder.textViewID.setText(ID);
        viewHolder.textViewEmail.setText(email);
        viewHolder.textViewPoint.setText("Point: " + point);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
