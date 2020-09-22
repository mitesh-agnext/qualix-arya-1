package com.specx.scan.ui.reference;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.app.ui.base.BaseDialog;
import com.data.app.reference.ReferenceType;
import com.specx.scan.R;
import com.specx.scan.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.data.app.reference.ReferenceType.FACTORY;
import static com.data.app.reference.ReferenceType.PREVIOUS;

public class ReferenceDialog extends BaseDialog {

    private Unbinder unbinder;
    private Callback callback;

    @BindView(R2.id.btn_ok)
    Button btnOk;

    @BindView(R2.id.rb_factory)
    RadioButton rbFactory;

    @BindView(R2.id.rb_previous)
    RadioButton rbPrevious;

    @BindView(R2.id.rb_new)
    RadioButton rbNew;

    @OnClick(R2.id.btn_ok)
    void ok(View view) {
        if (rbFactory.isChecked()) {
            userManager.setReferenceType(FACTORY.code);
        } else if (rbPrevious.isChecked()) {
            userManager.setReferenceType(PREVIOUS.code);
        } else if (rbNew.isChecked()) {
            if (callback != null) {
                callback.showReferenceScanScreen();
            }
        }

        dismiss();
    }

    @OnClick(R2.id.btn_cancel)
    public void dismiss() {
        super.dismiss();
    }

    public static ReferenceDialog newInstance() {
        return new ReferenceDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_reference, container, false);
        unbinder = ButterKnife.bind(this, dialogView);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!TextUtils.isEmpty(userManager.getScanReference())) {
            rbPrevious.setEnabled(true);
        }

        if (ReferenceType.from(userManager.getReferenceType()) != FACTORY) {
            rbPrevious.setChecked(true);
        }

        return dialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    public interface Callback {

        void showReferenceScanScreen();

    }
}