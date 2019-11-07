package com.dtu.capstone2.ereading.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.ui.account.login.LoginFragment;
import com.dtu.capstone2.ereading.ui.utils.BaseActivity;

public class ManagerAccountContainerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_account_container);
        replaceFragment(R.id.layoutManagerAccountContainerActivity, new LoginFragment(), false, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.anim_slide_new_in_left, R.animator.anim_slide_old_out_right);
    }
}
