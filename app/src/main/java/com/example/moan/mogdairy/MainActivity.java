package com.example.moan.mogdairy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


//deal with conflict or just not allow left delete;


public class MainActivity extends BaseActivity {
    public static String defaultLocation = "wuhan";
    private List<Diary> diaryList;
    public static final int WHERE = 1;
    public static final int space = 20;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    public static DiaryAdapter diaryAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        diaryList.clear();
        List<Diary> diaries = LitePal.findAll(Diary.class);
        diaryList.addAll(diaries);
        diaryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //TODO
        //make it can be dragged


        final FloatingActionButton createDiary = (FloatingActionButton) findViewById(R.id.create_diary);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        diaryList = LitePal.findAll(Diary.class);
        recyclerView = (RecyclerView) findViewById(R.id.total);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        diaryAdapter = new DiaryAdapter(diaryList);
        recyclerView.setAdapter(diaryAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecration(space));


        navigationView = (NavigationView) findViewById(R.id.nav);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onClickItem(menuItem.getItemId());
                return true;
            }
        });
        View headView = navigationView.inflateHeaderView(R.layout.nav_header);
        CircleImageView profile = (CircleImageView) headView.findViewById(R.id.user_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "to be done", Toast.LENGTH_SHORT).show();
                //TODO
            }
        });

        createDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WritingActivity.class);
                intent.putExtra("where", WHERE);
                startActivity(intent);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder) {
                //disable dragging
                //no conflict management so I just let it can be deleted only
                //with left movement
                int dragFlags = 0;
                int swipeFlags = ItemTouchHelper.LEFT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                //disable dragging so I do nothing
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                Snackbar.make(recyclerView, "Delete Diary ?", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#2e8b57"))
                        .setAction("DELETE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDiary((diaryList.get(viewHolder.getAdapterPosition())).getId());
                                diaryList.remove(viewHolder.getAdapterPosition());
                                //diaryAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                diaryAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                diaryAdapter.notifyItemRangeChanged(0, diaryList.size());
                            }
                        })
                        .show();
                diaryAdapter.notifyDataSetChanged();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

//    private void setProfile() {
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
//                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission
//                    .WRITE_EXTERNAL_STORAGE}, 1);
//        } else {
//            //TODO
//        }
//    }
//    public void openAlbum() {
//
//    }

    private void onClickItem(int id) {
        switch (id) {
            case R.id.nav_info:
                //TODO
                Toast.makeText(MainActivity.this, "to be done", Toast.LENGTH_SHORT).show();
                //show app info
                //use popupwindow
                break;
            case R.id.nav_location:
                //TODO
                getLocationMannually();
                //change your defaultLocation(for weather
                break;
            case R.id.nav_profile:
                //TODO
                Toast.makeText(MainActivity.this, "to be done", Toast.LENGTH_SHORT).show();
                //change the user profile
                break;
            case R.id.nav_task:
                //TODO
                Toast.makeText(MainActivity.this, "to be done", Toast.LENGTH_SHORT).show();
                //show clock info
                break;
            case R.id.nav_sort_by_dictonary:
                sortByDictionary();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.nav_sort_by_date:
                sortByDate();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
        }

    }

    private void sortByDictionary() {
        diaryList.clear();
        List<Diary> diaries = LitePal.order("title asc").find(Diary.class);
        diaryList.addAll(diaries);
        diaryAdapter.notifyDataSetChanged();
    }

    private void sortByDate() {
        diaryList.clear();
        List<Diary> diaries = LitePal.order("date asc").find(Diary.class);
        diaryList.addAll(diaries);
        diaryAdapter.notifyDataSetChanged();
    }

    private void deleteDiary(int id) {
        LitePal.delete(Diary.class, id);
    }

    private void getLocationMannually() {
        final EditText editText = new EditText(MainActivity.this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(editText)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        defaultLocation = editText.getText().toString();
                    }
                })
                .create()
                .show();

        //TODO
        //if user put in a defaultLocation which is not existing
        //make the user know
    }

}