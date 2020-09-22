package com.custom.app.ui.password.change;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.app.ui.base.BaseFragment;
import com.core.app.rule.OldPassword;
import com.core.app.util.AlertUtil;
import com.core.app.util.Util;
import com.custom.app.R;
import com.custom.app.ui.logout.LogoutDialog.Callback;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public class ChangePasswordFragment extends BaseFragment implements ChangePasswordView, ValidationListener {

    private Callback callback;
    private Unbinder unbinder;
    private Validator validator;

    @Inject
    ChangePasswordPresenter presenter;

    @Order(1)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_password_msg)
    @OldPassword(sequence = 2, messageResId = R.string.password_same_msg)
    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;

    @Order(2)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_password_msg)
    @Password(sequence = 2, messageResId = R.string.invalid_password_msg)
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;

    @Order(3)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_password_msg)
    @ConfirmPassword(sequence = 2, messageResId = R.string.password_mismatch_msg)
    @BindView(R.id.et_confirm_pwd)
    EditText etConfirmPwd;

    @OnClick(R.id.btn_cancel)
    void cancel() {
        getFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.btn_save)
    void save(View view) {
        Util.hideSoftKeyboard(view);
        validator.validate();
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_pwd, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.registerAnnotation(OldPassword.class);
        validator.setValidationListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);
    }

    @Override
    public void onValidationSucceeded() {
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();

        AlertUtil.showActionAlertDialog(context(), "Alert",
                "Once the password is changed successfully, you will need to login again. Still want to proceed?",
                getString(R.string.btn_no), getString(R.string.btn_yes), (dialog, which) -> presenter.changePassword(oldPwd, newPwd));
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String[] listMessage = error.getCollatedErrorMessage(context()).split("\n");
            String msg = listMessage[0];

            if (view instanceof EditText) {
                ((EditText) view).setError(msg);
                view.requestFocus();
            } else {
                showMessage(msg);
            }
        }
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void showLoginScreen() {
        if (callback != null) {
            callback.showLoginScreen();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }
}