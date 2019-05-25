package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lc.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

public class DialogFg extends DialogFragment {
    private View view;
    private TextView tv0, tv1, tv2, tv3, tv4;
    private LinearLayout dialogFgLlBg;
    private DialogFgCallback dialogFgCallback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogFgCallback=(DialogFgCallback)context;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.dialogfg_layout, container,false);
        dialogFgLlBg =view.findViewById(R.id.dialogFgLlBg);
        tv0=view.findViewById(R.id.dialogTv0);
        tv1=view.findViewById(R.id.dialogTv1);
        tv2=view.findViewById(R.id.dialogTv2);
        tv3=view.findViewById(R.id.dialogTv3);
        tv4=view.findViewById(R.id.dialogTv4);
        List<TextView> textViewList= new ArrayList<>();
        textViewList.add(tv0);
        textViewList.add(tv1);
        textViewList.add(tv2);
        textViewList.add(tv3);
        textViewList.add(tv4);
        dialogFgCallback.DialogSendData(dialogFgLlBg, textViewList);
        return view;
    }
    public List<TextView> getTextViewFromView() {
        List<TextView> textViewList= new ArrayList<>();
        textViewList.add(tv0);
        textViewList.add(tv1);
        textViewList.add(tv2);
        textViewList.add(tv3);
        textViewList.add(tv4);
        return textViewList;
    }
    public interface DialogFgCallback{
        public void DialogSendData(LinearLayout linearLayout, List<TextView> tvList);
    }
}
