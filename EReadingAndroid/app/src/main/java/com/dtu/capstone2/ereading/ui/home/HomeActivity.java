package com.dtu.capstone2.ereading.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.network.request.AccountLoginRequest;
import com.dtu.capstone2.ereading.network.response.Token;
import com.dtu.capstone2.ereading.ui.MainViewModel;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {
    private EReadingRepository localRepository = new EReadingRepository();
    private String tk = "";
    private TextView tvTest;
    private String text;
    private ArrayList listFavoriteWord = new ArrayList();
    private ArrayList listFavoriteId = new ArrayList();
    private Button btnTest;

    MainViewModel.MainActivityViewModel MainActivityVMD = new MainViewModel.MainActivityViewModel();
    //test text selection action
    // Tracks current contextual action mode
    private ActionMode currentActionMode;
    // Define the callback when ActionMode is activated
    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Actions");
            mode.getMenuInflater().inflate(R.menu.actions_textview, menu);
            return true;
        }

        // Called each time the action mode is shown.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int min = 0;
            int max = tvTest.getText().length();
            if (tvTest.isFocused()) {
                final int selStart = tvTest.getSelectionStart();
                final int selEnd = tvTest.getSelectionEnd();

                min = Math.max(0, Math.min(selStart, selEnd));
                max = Math.max(0, Math.max(selStart, selEnd));
            }
            // Perform your definition lookup with the selected text
            final CharSequence selectedText = tvTest.getText().subSequence(min, max);
            // Finish and close the ActionMode
            switch (item.getItemId()) {
                case R.id.menu_choose:
                    listFavoriteWord.add(selectedText);
                    Toast.makeText(HomeActivity.this, "Chọn!" + selectedText, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_add:
                    for (int i = 0; i < listFavoriteWord.size(); i++) {
                        Log.e("list", String.valueOf(listFavoriteWord.get(i)));
                    }
                    Toast.makeText(HomeActivity.this, "Add!" + selectedText, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_request:
                    Toast.makeText(HomeActivity.this, "Request!" + selectedText, Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null; // Clear current action mode
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        tvTest = findViewById(R.id.tvTest);

        tvTest.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentActionMode != null) {
                    return false;
                }
                startActionMode(modeCallBack);
                v.setSelected(true);
                return true;
            }
        });

        SpannableString spannableString = new SpannableString("Islamic terror group has lost its last territory in Syria, but its breeding ground still thrives");
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Log.e("xxx", "okok");
            }
        }, 10, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTest.setText(spannableString);
        tvTest.setMovementMethod(LinkMovementMethod.getInstance());

        localRepository.login(new AccountLoginRequest("admin", "admin123456"))
                .subscribeOn(Schedulers.io())
                //                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Token>() {
                    @Override
                    public void accept(Token token) throws Exception {
                        tk = token.getToken();
                        Log.e("xxx", "accept: " + token);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
        MainActivityVMD.addFavoriteMD(1, 1);
    }
}
