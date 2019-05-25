package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lc.musicplayer.R;

import java.util.ArrayList;
import java.util.List;


public class DialogFgDetails extends DialogFragment {
    public interface DialogFgDetailsCallback{
        void sendDetailsIdList(LinearLayout linearLayout, ImageView imageView, List<TextView> textViewList);
    }
    private DialogFgDetailsCallback dialogFgDetailsCallback;
    private View view;
    private TextView  tv1, tv2, tv3, tv4, tv5;
    private ImageView iv0;
    private LinearLayout detailsFgLlBg;
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogFgDetailsCallback=(DialogFgDetailsCallback)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.dialog_item_details_layout, container, false);
        detailsFgLlBg = view.findViewById(R.id.detailsFgLlBg);
        iv0 = view.findViewById(R.id.detailsIv0);
        tv1 = view.findViewById(R.id.detailsTv1);
        tv2 = view.findViewById(R.id.detailsTv2);
        tv3 = view.findViewById(R.id.detailsTv3);
        tv4 = view.findViewById(R.id.detailsTv4);
        tv5 = view.findViewById(R.id.detailsTv5);
        List<TextView> mTextViewList = new ArrayList<>();
        mTextViewList.add(tv1);
        mTextViewList.add(tv2);
        mTextViewList.add(tv3);
        mTextViewList.add(tv4);
        mTextViewList.add(tv5);
        dialogFgDetailsCallback.sendDetailsIdList(detailsFgLlBg ,iv0, mTextViewList);
        return view;
    }
}
