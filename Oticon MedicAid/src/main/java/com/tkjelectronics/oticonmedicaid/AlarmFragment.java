package com.tkjelectronics.oticonmedicaid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AlarmFragment extends Fragment {
    Button dismissBtn;
    TextView mTextView;
    boolean alarmFlag;

    public AlarmFragment() {
        this(false);
    }

    public AlarmFragment(boolean alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        dismissBtn = (Button) view.findViewById(R.id.dismissButton);
        mTextView = (TextView) view.findViewById(R.id.reminderText);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (alarmFlag) {
            mTextView.setVisibility(View.VISIBLE);
            dismissBtn.setText(getString(R.string.dismiss));
            ((MedicAidActivity) getActivity()).startMediaPlayer();
        } else
            mTextView.setVisibility(View.GONE);

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setVisibility(View.GONE);
                dismissBtn.setText(getString(R.string.buttonDefault));
                alarmFlag = false;
                ((MedicAidActivity) getActivity()).stopMediaPlayer();
            }
        });
    }
}
