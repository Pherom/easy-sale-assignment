package com.pherom.easysaleassignment.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pherom.easysaleassignment.R;
import com.pherom.easysaleassignment.model.User;
import com.pherom.easysaleassignment.viewmodel.UserViewModel;
import com.squareup.picasso.Picasso;

public class EditUserActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private TextView userTitleView;
    private EditText userFirstNameEditText;
    private EditText userLastNameEditText;
    private EditText userEmailEditText;
    private ImageView userAvatarView;
    private ActivityResultLauncher<Intent> selectImageResultLauncher;
    private String avatarUrl = "test";
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        userTitleView = findViewById(R.id.userTitleView);
        userFirstNameEditText = findViewById(R.id.userFirstNameEditText);
        userLastNameEditText = findViewById(R.id.userLastNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userAvatarView = findViewById(R.id.userAvatarView);

        Bundle bundle = getIntent().getExtras();
        fillFormWithBundle(bundle);

        selectImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onImageSelection);
        setUpTextWatchers();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void fillFormWithBundle(Bundle bundle) {
        if (bundle != null) {
            id = bundle.getInt("id");
            String firstName = bundle.getString("firstName");
            String lastName = bundle.getString("lastName");
            userTitleView.setText(String.format("%s %s", firstName, lastName));
            userFirstNameEditText.setText(firstName);
            userLastNameEditText.setText(lastName);
            userEmailEditText.setText(bundle.getString("email"));
            avatarUrl = bundle.getString("avatar");
            if (URLUtil.isNetworkUrl(avatarUrl) || URLUtil.isContentUrl(avatarUrl)) {
                Picasso.get().load(avatarUrl).into(userAvatarView);
            }
            else {
                Uri avatarUri = Uri.parse(avatarUrl);
                userAvatarView.setImageURI(avatarUri);
            }
        }
    }

    private void onImageSelection(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            getApplicationContext().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            userAvatarView.setImageURI(imageUri);
            avatarUrl = data.getData().toString();
        }
    }

    private void setUpTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userTitleView.setText(String.format("%s %s", userFirstNameEditText.getText().toString(), userLastNameEditText.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        userFirstNameEditText.addTextChangedListener(textWatcher);
        userLastNameEditText.addTextChangedListener(textWatcher);
    }

    public void confirmUser(View v) {
        if (validateInput()) {
            User editedUser = new User(userEmailEditText.getText().toString(),
                    userFirstNameEditText.getText().toString(),
                    userLastNameEditText.getText().toString(),
                    avatarUrl);
            if (id != -1) {
                editedUser.setId(id);
                userViewModel.update(editedUser);
            }
            else {
                userViewModel.insert(editedUser);
            }
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_input), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        String inputFirstName = userFirstNameEditText.getText().toString();
        String inputLastName = userLastNameEditText.getText().toString();
        String inputEmail = userEmailEditText.getText().toString();
        boolean result = true;

        if (inputFirstName.isEmpty()) {
            userFirstNameEditText.setError(getString(R.string.error_first_name_field_required));
            result = false;
        }
        if (inputLastName.isEmpty()) {
            userLastNameEditText.setError(getString(R.string.error_last_name_field_required));
            result = false;
        }
        if (inputEmail.isEmpty()) {
            userEmailEditText.setError(getString(R.string.error_email_field_required));
            result = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            userEmailEditText.setError(getString(R.string.error_invalid_email));
            result = false;
        }
        return result;
    }

    public void cancelUser(View v) {
        finish();
    }

    public void selectImage(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        selectImageResultLauncher.launch(Intent.createChooser(intent, getString(R.string.chooser_title)));
    }
}