/*
 * @project: Numberplate Scanner - https://gitlab.com/obrymec/number_plate_scanner
 * @author: Obrymec - obrymecsprinces@gmail.com
 * @fileoverview: The home screen.
 * @file: MainScreen.java
 * @created: 2024-04-27
 * @updated: 2024-05-16
 * @supported: ANDROID
 * @version: 0.2.7
 */

/// Package name.
package org.cacybernet.numberplatescanner.presentation;

/// Java dependencies.
import java.io.IOException;
import java.util.Objects;
import java.util.Locale;

/// Android dependencies.
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable;
import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.content.Intent;
import android.widget.Button;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;

/// Library dependencies.
import com.squareup.okhttp.ResponseBody;
import org.json.JSONException;
import org.json.JSONArray;

/// Custom dependencies.
import org.cacybernet.numberplatescanner.utils.Preferences;
import org.cacybernet.numberplatescanner.utils.System;
import org.cacybernet.numberplatescanner.R;

/**
 * The home page for camera launching.
 */
@SuppressWarnings("unused")
public class MainScreen extends AppCompatActivity {
  /// Attributes.
  private static final String TAG = "MainScreen";
  private ImageView uploadSettingsBtn = null;
  private EditText descriptionInput = null;
  private LinearLayout uploadScreen = null;
  private ImageView homeSettingsBtn = null;
  private Button uploadLauncherBtn = null;
  private LinearLayout homeScreen = null;
  private TextView resolutionText = null;
  private ImageView capturedImage = null;
  private Button homeLauncherBtn = null;
  private ImageView backArrowBtn = null;
  private static final int DELAY = 100;
  private TextView weightText = null;
  private TextView dateText = null;
  private TextView timeText = null;
  private Button pickerBtn = null;
  private Button uploadBtn = null;
  private String imagePath = null;
  private Bitmap capture = null;

  /**
   * Quit application definitely.
   */
  private void quitApp () {
    // Closes all activities.
    this.finishAffinity();
    // Clears app variables.
    java.lang.System.exit(0);
  }

  /**
   * Called when we want to quit the last app activity.
   * @noinspection deprecation
   */
  @SuppressLint({"InflateParams", "MissingSuperCall"})
  @Override
  public void onBackPressed () {
    // Whether upload screen is displayed.
    if (this.uploadScreen.getVisibility() == View.VISIBLE) this.toggleScreens();
    // Otherwise.
    else {
      // Creates a custom dialog box.
      System.getInstance().initializeDialog((controls, popup) -> {
        // Listens `click` event on `cancel` button.
        controls.findViewById(R.id.cancel).setOnClickListener(view -> popup.cancel());
        // Listens `click` event on `quit` button.
        controls.findViewById(R.id.quit).setOnClickListener(view -> this.quitApp());
      }, this, true, 24, R.layout.quit_popup);
    }
  }

  /**
   * Destroys active focus and hides device native keyboard.
   */
  private void clearActiveFocus () {
    // Gets element that has the active focus.
    final View view = this.getWindow().getCurrentFocus();
    // Puts the focus to the global container.
    this.uploadScreen.requestFocus();
    // Gets native device keyboard manager.
    final InputMethodManager manager = (InputMethodManager) this.getSystemService(
      Context.INPUT_METHOD_SERVICE
    );
    // Whether all conditions get match.
    if (manager != null && view != null) manager.hideSoftInputFromWindow(
      view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
    );
  }

  /**
   * Controls `upload` button activation regardless image capture.
   */
  private void adaptUploadButtonActivation () {
    // Whether image capture is not defined.
    if (this.capture == null) {
      // Disables button uploader.
      this.setButtonState(
        R.drawable.disabled_button, R.color.neutral_6, false, this.uploadBtn
      );
    // Otherwise.
    } else {
      // Enables button uploader.
      this.setButtonState(
        R.drawable.button_bg, R.color.neutral_1, true, this.uploadBtn
      );
    }
  }

  /**
   * Displays the content of the intent to send to upload screen.
   * @param intent The intent object instance to expect.
   */
  private void displayIntentContent (@NonNull Intent intent) {
    // Displays image size.
    Log.d(TAG, Objects.requireNonNull(intent.getStringExtra("size")));
    // Displays current date.
    Log.d(TAG, Objects.requireNonNull(intent.getStringExtra("date")));
    // Displays current time.
    Log.d(TAG, Objects.requireNonNull(intent.getStringExtra("time")));
    // Displays image resolution.
    Log.d(
      TAG, String.format(
        Locale.US, "%s x %s",
        intent.getStringExtra("width"),
        intent.getStringExtra("height")
      )
    );
  }

  /**
   * Toggles home view with upload view.
   */
  private void toggleScreens () {
    // Clears active focus whether needed.
    this.clearActiveFocus();
    // Whether home screen is visible.
    if (this.homeScreen.getVisibility() == View.VISIBLE) {
      // Shows upload screen.
      this.uploadScreen.setVisibility(View.VISIBLE);
      // Hides it.
      this.homeScreen.setVisibility(View.GONE);
    // Otherwise.
    } else {
      // Shows home screen.
      this.homeScreen.setVisibility(View.VISIBLE);
      // Hides it.
      this.uploadScreen.setVisibility(View.GONE);
    }
  }

  /**
   * Displays error message box.
   */
  private void displayErrorPopup (int title, int message) {
    // Creates custom dialog.
    System.getInstance().initializeDialog((controls, popup) -> {
      // Sets popup message.
      ((TextView) controls.findViewById(R.id.message)).setText(this.getString(message));
      // Sets popup title.
      ((TextView) controls.findViewById(R.id.title)).setText(this.getString(title));
      // Listens `click` event on `cancel` button.
      controls.findViewById(R.id.cancel).setOnClickListener(view -> popup.cancel());
      // Listens `click` event on `retry` button.
      controls.findViewById(R.id.retry).setOnClickListener(view -> {
        // Starts generating process.
        new Handler().postDelayed(this::uploadImage, DELAY);
        // Closes current popup.
        popup.cancel();
      });
    }, this, true, 24, R.layout.error_popup);
  }

  /**
   * Changes left icon, text, state and background of a button.
   * @param colorBg The drawable background color.
   * @param color The color of left icon and text.
   * @param enabled Whether button is disabled or not.
   * @param button An object instance of a button.
   */
  private void setButtonState (
    int colorBg, int color, boolean enabled, @NonNull Button button
  ) {
    // Gets `send` icon vector drawable.
    final Drawable sendIcon = ContextCompat.getDrawable(this, R.drawable.send);
    // Sets background color.
    button.setBackground(ContextCompat.getDrawable(this, colorBg));
    // Sets text color.
    button.setTextColor(ContextCompat.getColor(this, color));
    // Sets button interaction state.
    button.setEnabled(enabled);
    if (sendIcon != null) button.setCompoundDrawablesWithIntrinsicBounds(
      sendIcon, null, null, null
    );
    // Sets left icon color.
    DrawableCompat.setTint(
      DrawableCompat.wrap(button.getCompoundDrawables()[0]),
      ContextCompat.getColor(this, color)
    );
  }

  /**
   * Clears all labels and inputs in the display.
   */
  private void clearScreenContent () {
    // Sets resolution visibility.
    this.resolutionText.setVisibility(View.GONE);
    // Clears image view capture.
    this.capturedImage.setImageBitmap(null);
    // Clears description field.
    this.descriptionInput.setText("");
    // Clears resolution text.
    this.resolutionText.setText("");
    // Clears image size.
    this.weightText.setText("--");
    // Clears capture date.
    this.dateText.setText("--");
    // Clears capture time.
    this.timeText.setText("--");
    // Clears captured image.
    this.capture = null;
    // Checks image capture.
    this.adaptUploadButtonActivation();
  }

  /**
   * Updates the upload screen when an image has been captured.
   * @param imageData The intent to use to update screen.
   */
  private void updateScreen (@NonNull Intent imageData) {
    // Gets captured image as a bitmap.
    this.capture = (Bitmap) Objects.requireNonNull(imageData.getExtras()).get("capture");
    // Displays image size.
    this.weightText.setText(imageData.getStringExtra("size"));
    // Displays current date.
    this.dateText.setText(imageData.getStringExtra("date"));
    // Displays current time.
    this.timeText.setText(imageData.getStringExtra("time"));
    // Gets image real path.
    this.imagePath = imageData.getStringExtra("path");
    // Sets resolution visibility.
    this.resolutionText.setVisibility(View.VISIBLE);
    // Displays captured image.
    this.capturedImage.setImageBitmap(capture);
    // Checks captured image.
    this.adaptUploadButtonActivation();
    // Displays resolution.
    this.resolutionText.setText(
      String.format(
        Locale.US, "%s x %s",
        imageData.getStringExtra("width"),
        imageData.getStringExtra("height")
      )
    );
  }

  /**
   * Fix first behaviours when activity get started.
   */
  private void fixFirstBehaviours () {
    // Listens `click` event on `uploadSettings` button.
    this.uploadSettingsBtn.setOnClickListener(view -> System.getInstance().openSettings(this));
    // Listens `click` event on `uploadLauncher` button.
    this.uploadLauncherBtn.setOnClickListener(view -> System.getInstance().launchPicker(this));
    // Listens `click` event on `homeSettings` button.
    this.homeSettingsBtn.setOnClickListener(view -> System.getInstance().openSettings(this));
    // Listens `click` event on `homeLauncher` button.
    this.homeLauncherBtn.setOnClickListener(view -> System.getInstance().launchCamera(this));
    // Listens `click` event on `picker` button.
    this.pickerBtn.setOnClickListener(view -> System.getInstance().launchPicker(this));
    // Listens `click` event on `backArrow` button.
    this.backArrowBtn.setOnClickListener(view -> this.toggleScreens());
    // Manages focus on description field.
    System.getInstance().manageInputFocus(this.descriptionInput, this);
    // Listens `click` event on `upload` button.
    this.uploadBtn.setOnClickListener(view -> this.uploadImage());
    // Checks image capture.
    this.adaptUploadButtonActivation();
    // Listens `submit` event on input.
    System.getInstance().trackInputSubmitEvent(
      this.descriptionInput, input -> this.uploadImage()
    );
    // Asks for required permissions.
    this.requestPermissions(
      new String[] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
      }, 200
    );
  }

  /**
   * Called when this activity is loaded and ready to use.
   * @param savedInstanceState The saved instance state.
   */
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    // Calls his parent method.
    super.onCreate(savedInstanceState);
    // Binds the xml file to this java class.
    this.setContentView(R.layout.main_screen);
    // Changes native status bars color.
    System.getInstance().setBarsColor(R.color.primary_900, this);
    // Gets upload settings button.
    this.uploadSettingsBtn = this.findViewById(R.id.uploadSettings);
    // Gets upload launcher button.
    this.uploadLauncherBtn = this.findViewById(R.id.uploadLauncher);
    // Gets resolution text.
    this.resolutionText = this.findViewById(R.id.resolutionText);
    // Gets home settings button.
    this.homeSettingsBtn = this.findViewById(R.id.homeSettings);
    // Gets home launcher button.
    this.homeLauncherBtn = this.findViewById(R.id.homeLauncher);
    // Gets description input.
    this.descriptionInput = this.findViewById(R.id.description);
    // Gets captured image.
    this.capturedImage = this.findViewById(R.id.capturedImage);
    // Gets home screen.
    this.uploadScreen = this.findViewById(R.id.uploadScreen);
    // Gets back arrow button.
    this.backArrowBtn = this.findViewById(R.id.backArrow);
    // Gets home screen.
    this.homeScreen = this.findViewById(R.id.homeScreen);
    // Gets weight text.
    this.weightText = this.findViewById(R.id.weightText);
    // Gets date text.
    this.dateText = this.findViewById(R.id.dateText);
    // Gets time text.
    this.timeText = this.findViewById(R.id.timeText);
    // Gets picker button.
    this.pickerBtn = this.findViewById(R.id.picker);
    // Gets uploader button.
    this.uploadBtn = this.findViewById(R.id.upload);
    // Fix first behaviours.
    this.fixFirstBehaviours();
  }

  /**
   * Called when activity is resumed.
   */
  @Override
  protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
    // Calls the parent method.
    super.onActivityResult(requestCode, resultCode, data);
    // Whether all are okay.
    if (resultCode == RESULT_OK) {
      // Creates an intent that contains extracted image data.
      Intent intent = null;
      // Whether request code is `202`.
      if (requestCode == 202) {
        // Extracts captured image whether needed.
        intent = System.getInstance().extractCapture(data, this);
      // Whether request code is `201`
      } else if (requestCode == 201) {
        // Tries to convert selected image into bitmap format.
        try {
          // The loaded image from gallery or camera photos.
          final Intent imageIntent = System.getInstance().extractImage(data, this);
          // Extracts loaded image whether needed.
          intent = System.getInstance().extractCapture(imageIntent, this);
        // An error occurred.
        } catch (IOException error) {
          // Displays exception origin.
          System.getInstance().displayError(error);
          // Image loading error.
          new Handler().postDelayed(
            () -> this.displayErrorPopup(
              R.string.load_error_title, R.string.load_error_message
            ), DELAY
          );
        }
      }
      // Whether we have something.
      if (intent != null) {
        // Expects intent content.
        this.displayIntentContent(intent);
        // Updates screen data.
        this.updateScreen(intent);
        // Go to upload screen.
        if (this.homeScreen.getVisibility() == View.VISIBLE) this.toggleScreens();
      }
    }
  }

  /**
   * Displays success message box.
   */
  private void displaySuccessPopup (JSONArray content) {
    // Creates custom dialog.
    System.getInstance().initializeDialog((controls, popup) -> {
      // Listens `click` event on `validate` button.
      controls.findViewById(R.id.validate).setOnClickListener(view -> popup.cancel());
      // Gets server response displayer.
      final TextView displayer = controls.findViewById(R.id.response);
      // Tries to extract server response data.
      try {
        // Shows server response displayer.
        displayer.setVisibility(View.VISIBLE);
        // Whether content really exists.
        if (content.length() > 0) {
          // The number plate list.
          final StringBuilder platesList = new StringBuilder();
          // The last element index.
          final int lastIndex = (content.length() - 1);
          // Building data to be displayed.
          for (int pos = 0; pos < content.length(); pos++) {
            // The current detected plate number.
            final String plateNumber = (String) content.getJSONObject(pos).get("number");
            // Whether index position isn't at the end.
            if (pos < lastIndex) platesList.append(plateNumber.trim()).append(", ");
            // Otherwise.
            else platesList.append(plateNumber.trim());
          }
          // Displays final generated result.
          displayer.setText(
            String.format(
              Locale.US, this.getString(R.string.detected_plates),
              platesList.toString().trim()
            )
          );
        // Otherwise.
        } else displayer.setText(this.getString(R.string.no_platenumber));
        // Clears screen for next the image.
        this.clearScreenContent();
      // Something wrong.
      } catch (JSONException error) {
        // Displays that error.
        System.getInstance().displayError(error);
        // Hides server response displayer.
        displayer.setVisibility(View.GONE);
        // Server response retrieve error popup.
        new Handler().postDelayed(
          () -> this.displayErrorPopup(
            R.string.retrieve_error_title, R.string.retrieve_error_message
          ), DELAY
        );
      }
    }, this, true, 24, R.layout.success_popup);
  }

  /**
   * Uploads the captured image to the remote back-end server.
   */
  private void uploadImage () {
    // Gets api link from preferences.
    String link = Preferences.getInstance().load("api_link", "", this).trim();
    // Clears active focus.
    this.clearActiveFocus();
    // Whether a link was found.
    if (!link.isEmpty()) {
      // Gets api link parts.
      final String[] parts = (link.contains(":") ? link.split(":") : new String[]{});
      // Whether there are no parts.
      if (parts.length == 0) link = ("http://" + link + ":8000/alpr/lp/");
      // Whether there are one colon.
      else if (parts.length == 2) link = ("http://" + link + "/alpr/lp/");
      // The final retained link.
      final String retainedApiLink = link;
      // Initializes dialog for loader.
      System.getInstance().initializeDialog((controls, popup) -> {
        // Submits form data.
        System.getInstance().submitForm(
          retainedApiLink,
          this.imagePath,
          this.descriptionInput.getText().toString(),
          this.capture,
          new Handler(this.getMainLooper()),
          // All are done.
          response -> {
            // The server response body.
            final ResponseBody body;
            // Tries to get body content.
            try {
              // Gets server response body.
              body = response.body();
              // Gets response content.
              final JSONArray content = new JSONArray(body.string());
              // Shows success popup.
              new Handler().postDelayed(() -> this.displaySuccessPopup(content), DELAY);
              // Free opened stream.
              body.close();
            // Something wrong.
            } catch (IOException | JSONException error) {
              // Displays that detected error.
              System.getInstance().displayError(error);
              // Server response retrieve error popup.
              new Handler().postDelayed(
                () -> this.displayErrorPopup(
                  R.string.retrieve_error_title, R.string.retrieve_error_message
                ), DELAY
              );
            // For any process status.
            } finally {
              // Closes loader.
              popup.dismiss();
            }
          },
          // An error thrown.
          error -> {
            // The retrieved error message.
            final String errorMessage = error.getMessage();
            // Whether that error comes from server.
            if (errorMessage != null && errorMessage.contains("204")) {
              // Shows success popup.
              new Handler().postDelayed(
                () -> this.displaySuccessPopup(new JSONArray()), DELAY
              );
            // Otherwise.
            } else {
              // Image upload error popup.
              new Handler().postDelayed(
                () -> this.displayErrorPopup(
                  R.string.upload_error_title, R.string.upload_error_message
                ), DELAY
              );
            }
            // Closes loader.
            popup.dismiss();
          }
        );
      }, this, false, 64, R.layout.progress_popup);
    // No api link found in settings.
    } else System.getInstance().openSettings(this);
  }
}
