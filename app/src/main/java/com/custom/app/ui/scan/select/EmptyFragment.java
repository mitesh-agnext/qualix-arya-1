package com.custom.app.ui.scan.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.app.ui.base.BaseFragment;
import com.custom.app.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EmptyFragment extends BaseFragment {

    private Unbinder unbinder;

    public static EmptyFragment newInstance(String message) {
        EmptyFragment fragment = new EmptyFragment();

        Bundle args = new Bundle();
        args.putString("Message", message);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        TextView tvTitle = rootView.findViewById(R.id.tv_title);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}