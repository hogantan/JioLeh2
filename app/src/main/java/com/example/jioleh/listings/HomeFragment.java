package com.example.jioleh.listings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jioleh.LinesOfChecks;
import com.example.jioleh.R;
import com.example.jioleh.location.NearByActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private ImageView search;
    private ImageView findNearBy;
    private RecyclerView activity_list;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ActivityAdapter adapter;
    private View currentView;

    private LinesOfChecks linesOfChecks = new LinesOfChecks();

    // viewModel for jioActivity
    private JioActivityViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_home,container,false);
        initialise();
        initialiseRecyclerView();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentDate = Calendar.getInstance().getTime();
                System.out.println(currentDate);
                Intent nextActivity = new Intent(getContext(), SearchJioActivity.class);
                startActivity(nextActivity);
            }
        });

        findNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getContext(), NearByActivity.class);
                startActivity(nextActivity);
            }
        });

        return currentView;
    }

    private void initialise() {
        search = currentView.findViewById(R.id.ivSearchActivity);
        findNearBy = currentView.findViewById(R.id.ivFindNearby);
        swipeRefreshLayout = currentView.findViewById(R.id.swipeContainer);
    }

    private void initialiseRecyclerView() {
        adapter = new ActivityAdapter();
        activity_list = currentView.findViewById(R.id.rvActivityList);
        activity_list.setHasFixedSize(true);
        activity_list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        activity_list.setAdapter(adapter);

        activity_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                    viewModel.getMoreActivities();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Creates a ViewModel
        viewModel = new ViewModelProvider(this).get(JioActivityViewModel.class);
        //First Line of check
        linesOfChecks.checkActivityExpiry();
        linesOfChecks.checkActivityCancelledConfirmed();

        //observe for changes in database
        viewModel.getListOfJioActivities().observe(getViewLifecycleOwner(), new Observer<List<JioActivity>>() {
            @Override
            public void onChanged(List<JioActivity> jioActivities) {
                adapter.setData(jioActivities);
                //adapter.submitList(jioActivities);
                //is there another way to do this?
                adapter.notifyDataSetChanged();
            }
        });

        //Third line of check
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                linesOfChecks.checkActivityExpiry();
                linesOfChecks.checkActivityCancelledConfirmed();

                viewModel.refreshActivities();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
