package ru.mirea.khrechkorv.mireaproject.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ru.mirea.khrechkorv.mireaproject.MyWorker;
import ru.mirea.khrechkorv.mireaproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkerFragment newInstance(String param1, String param2) {
        WorkerFragment fragment = new WorkerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorker.class)
                        .build();
        WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest);
        return inflater.inflate(R.layout.fragment_worker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.button2);
        button.setOnClickListener(v -> ClickButton());
    }

    private void ClickButton(){
        OneTimeWorkRequest toastWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(requireContext()).enqueue(toastWorkRequest);
        Toast.makeText(requireContext(), "Процесс запущен, ждите 5 секунд...",
                Toast.LENGTH_SHORT).show();
    }
}