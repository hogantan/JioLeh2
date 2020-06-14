package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.chat.MessagePage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

public class OtherUserView extends AppCompatActivity {

    private String profileUID;
    private String profileUsername;
    private userProfileViewModel viewModel;
    private TextView tv_username, tv_age, tv_gender, tv_location;
    private ImageView iv_ProfilePic;

    private Button message;

    private Button btn_message, btn_review, btn_report;

    private Toolbar toolbar;

    private UserProfileViewPagerAdapter pagerAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_view);
        initialise();

         

        Intent intent = getIntent();
        //the intent that opens this must put extra as "user_id" the user's id
        //this is the current profile user id not the current user
        profileUID = intent.getStringExtra("user_id");
        profileUsername = intent.getStringExtra("username");

        initialiseToolbar();


        viewModel= new ViewModelProvider(this).get(userProfileViewModel.class);
        viewModel.getUser(profileUID).observe(this, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                fill(userProfile);
            }
        });

        initialiaseViewPagerAndTab(profileUID);

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMessagePage = new Intent(OtherUserView.this, MessagePage.class);
                goToMessagePage.putExtra("user_id",profileUID);
                goToMessagePage.putExtra("username",profileUsername);
                startActivity(goToMessagePage);
            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewPage = new Intent(OtherUserView.this, ReviewPage.class);
                reviewPage.putExtra("username",profileUsername);
                reviewPage.putExtra("user_id",profileUID);
                startActivity(reviewPage);
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent reportPage = new Intent(OtherUserView.this,ReportUserPage.class);
                reportPage.putExtra("username",profileUsername);
                reportPage.putExtra("user_id",profileUID);
                startActivity(reportPage);
            }
        });

    }

    public void fill(UserProfile userProfile) {
        tv_username.setText(userProfile.getUsername());
        tv_age.setText(userProfile.getAge());
        tv_gender.setText(userProfile.getGender());
        tv_location.setText(userProfile.getLocation());

        if (!userProfile.getImageUrl().equals("") && userProfile.getImageUrl() != null) {
            Picasso.get().load(userProfile.getImageUrl()).into(iv_ProfilePic);
        }
    }

    public void initialise(){
        tv_username = findViewById(R.id.tv_profilePageUsername);
        tv_age = findViewById(R.id.tv_profilePageAge);
        tv_gender = findViewById(R.id.tv_profilePageGender);
        tv_location = findViewById(R.id.tv_profilePageLocation);
        iv_ProfilePic = findViewById(R.id.iv_userProfilePageImage);
        btn_message = findViewById(R.id.message_other_user);
        btn_review = findViewById(R.id.write_review_other_user);
        btn_report = findViewById(R.id.report_other_user);
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.include_top_app_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(this.profileUsername);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialiaseViewPagerAndTab(String uid) {
        viewPager2 = findViewById(R.id.userProfile_viewPager);
        pagerAdapter = new UserProfileViewPagerAdapter(this,uid);
        viewPager2.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.userProfile_tabLayout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setText("About Me");
                        break;
                    }
                    case 1: {
                        tab.setText("Listings");
                        break;
                    }
                    case 2: {
                        tab.setText("Reviews");
                        break;
                    }
                }
            }
        });

        tabLayoutMediator.attach();

    }




}
