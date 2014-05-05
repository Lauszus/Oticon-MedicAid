package com.tkjelectronics.oticonmedicaid;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

// TODO: Play alarm sound and then dismiss notification using button
// TODO: Make notification
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
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setVisibility(View.GONE);
                dismissBtn.setText(getString(R.string.buttonDefault));
                alarmFlag = false;
                ((MedicAidActivity) getActivity()).stopMediaPlayer();
            }
        });
        mTextView = (TextView) view.findViewById(R.id.reminderText);

        if (alarmFlag)
            setAlarm();
        else
            mTextView.setVisibility(View.GONE);

        return view;
    }

    private void setAlarm() {
        mTextView.setVisibility(View.VISIBLE);
        dismissBtn.setText(getString(R.string.dismiss));

        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(((MedicAidActivity) getActivity()), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
/*
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Could not get audio focus.
        }
*/
    }
}
