package com.example.myfakedouyinapplication.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class UserActionsDialog extends BottomSheetDialog {
    private User user;
    private OnUserActionListener listener;
    private boolean isSpecial = false;

    private TextView displayNameTextView;
    private TextView noteUserInfoTextView;
    private LinearLayout switchContainer;
    private View switchThumb;
    private Button setNoteButton;
    private Button unfollowButton;

    public interface OnUserActionListener {
        void onSpecialChanged(String userId, boolean isSpecial);

        void onNoteChanged(String userId, String note);

        void onUnfollow(String userId);
    }

    public UserActionsDialog(@NonNull Context context, User user, OnUserActionListener listener) {
        super(context, R.style.TopRoundedBottomSheet);
        this.user = user;
        this.listener = listener;
        isSpecial = user.isSpecial();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_actions);

        setupWindowProperties();

        initViews();
        setupData();
        setupListeners();
    }

    private void setupWindowProperties() {
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(0);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initViews() {
        displayNameTextView = findViewById(R.id.dialog_display_name);
        noteUserInfoTextView = findViewById(R.id.dialog_user_info);
        switchContainer = findViewById(R.id.special_switch_container);
        switchThumb = findViewById(R.id.special_switch_thumb);
        setNoteButton = findViewById(R.id.btn_set_note);
        unfollowButton = findViewById(R.id.btn_unfollow_dialog);
    }

    private void setupData() {
        updateUserInfoDisplay();
        updateSwitchState(isSpecial);
    }

    private void updateUserInfoDisplay() {
        String note = user.getNote();
        if (note != null && !note.isEmpty()) {
            displayNameTextView.setText(note);
            noteUserInfoTextView.setText("名字：" + user.getUsername() + " | ID：" + user.getUserId());
        } else {
            displayNameTextView.setText(user.getUsername());
            noteUserInfoTextView.setText("ID：" + user.getUserId());
        }
    }

    private void updateSwitchState(boolean isSpecial) {
        if (isSpecial) {
            switchContainer.setBackgroundResource(R.drawable.switch_track_on);
            switchThumb.setTranslationX(switchContainer.getWidth() - switchThumb.getWidth() - switchContainer.getPaddingRight());
        } else {
            switchContainer.setBackgroundResource(R.drawable.switch_track);
            switchThumb.setTranslationX(switchContainer.getPaddingLeft());
        }
    }

    private void toggleSpecialState() {
        isSpecial = !isSpecial;
        updateSwitchState(isSpecial);
        if (listener != null) {
            listener.onSpecialChanged(user.getUserId(), isSpecial);
        }
    }

    private void showEditNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("编辑备注");

        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_note, null);
        builder.setView(dialogView);

        final EditText noteEditText = dialogView.findViewById(R.id.edit_note_input);
        noteEditText.setText(user.getNote());
        noteEditText.setHint("为 " + user.getUsername() + " 添加备注");

        noteEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                noteEditText.post(() -> {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(noteEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });

        builder.setPositiveButton("保存", (dialog, which) -> {
            String newNote = noteEditText.getText().toString().trim();
            if (listener != null) {
                listener.onNoteChanged(user.getUserId(), newNote);
                user.setNote(newNote);
                updateUserInfoDisplay();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        noteEditText.requestFocus();
    }

    private void setupListeners() {
        switchContainer.setOnClickListener(v -> toggleSpecialState());
        setNoteButton.setOnClickListener(v -> showEditNoteDialog());
        unfollowButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUnfollow(user.getUserId());
            }
            dismiss();
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        switchContainer.post(() -> updateSwitchState(isSpecial));
    }
}
