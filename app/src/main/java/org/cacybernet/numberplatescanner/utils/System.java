/*
 * @project: Numberplate Scanner - https://gitlab.com/obrymec/number_plate_scanner
 * @fileoverview: Provides commons methods for android system.
 * @author: Obrymec - obrymecsprinces@gmail.com
 * @created: 2024-04-29
 * @updated: 2024-05-29
 * @supported: ANDROID
 * @file: System.java
 * @version: 0.1.2
 */

/// Package name.
package org.cacybernet.numberplatescanner.utils;

/// Java dependencies.
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.io.File;

/// Android dependencies.
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.inputmethod.EditorInfo;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.graphics.Color;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;
import android.util.Base64;
import android.view.Window;
import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.net.Uri;

/// Library dependencies.
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Request;

/// Custom dependencies.
import org.cacybernet.numberplatescanner.R;

/**
 * Provides commons methods for android system handing.
 */
@SuppressWarnings("unused")
public final class System {
  /// Attributes.
  private static final String TAG = "CustomSystem";
  private static System instance = null;
  private String cameraImgPath = null;
  public interface DialogEvent {
    void onInit(View controls, Dialog popup);
  }
  public interface FormSuccessEvent {
    void onSuccess(Response response);
  }
  public interface FormErrorEvent {
    void onError(Exception error);
  }
  public interface InputSubmitEvent {
    void onSubmit(EditText input);
  }

  /**
   * Blocks any object instantiation.
   */
  private System () {}

  /**
   * Checks whether app has permission to use storage.
   * @param ctx The activity context.
   * @return boolean
   */
  private boolean hasStoragePermission (@NonNull AppCompatActivity ctx) {
    // Sends permissions state.
    return ctx.checkSelfPermission(
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * Checks whether app has permission to use camera.
   * @param ctx The activity context.
   * @return boolean
   */
  public boolean hasCameraPermission (@NonNull AppCompatActivity ctx) {
    // Sends permissions state.
    return ctx.checkSelfPermission(
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * Returns a single instance of this class.
   * @return System
   */
  @SuppressWarnings("all")
  public static System getInstance () {
    // Whether there are no instance.
    if (instance == null) instance = new System();
    // Sends that unique instance.
    return instance;
  }

  /**
   * Returns the associated mime type of an image file.
   * @param path The path of the file.
   * @return String
   */
  @NonNull
  private String getImageMimeType (String path) {
    // Creates an instance of file.
    final File image = new File(path);
    // Generates a default mme type regardless image format.
    final String mmeType = ("image/" + image.getName().split("\\.")[1]);
    // Whether the passed image is a .jpeg/jpg.
    return (mmeType.equals("image/jpg") ? "image/jpeg" : mmeType);
  }

  /**
   * Displays an exception data when something wrong.
   * @param error The exception to be displayed.
   */
  public void displayError (@NonNull Exception error) {
    // Displays that exception.
    Log.e(
      TAG, String.format(
        Locale.US, "Error - Message: %s, Type: %s",
        error.getMessage(), error.getClass().getName()
      )
    );
  }

  /**
   * Asks for storage usage permission.
   * @param ctx The activity context.
   * @return boolean
   */
  private boolean askForStoragePermission (AppCompatActivity ctx) {
    // Whether permission hasn't been granted.
    if (!this.hasStoragePermission(ctx)) {
      // Asks permission.
      ctx.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
      // No permission granted.
      return false;
    // The permission has been granted.
    } else return true;
  }

  /**
   * Returns the created camera capture image path.
   * @param ctx The application context.
   * @param image The captured image.
   * @return Uri
   */
  public Uri getImageUri (@NonNull AppCompatActivity ctx, @NonNull Bitmap image) {
    // Let's create an output stream.
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    // Compress bitmap to jpg.
    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    // Sends the final path.
    return Uri.parse(
      MediaStore.Images.Media.insertImage(ctx.getContentResolver(), image, "Title", null)
    );
  }

  /**
   * Asks for camera usage permission.
   * @param ctx The activity context.
   * @return boolean
   */
  private boolean askForCameraPermission (AppCompatActivity ctx) {
    // Whether permission hasn't been granted.
    if (!this.hasCameraPermission(ctx)) {
      // Asks permission.
      ctx.requestPermissions(new String[] {Manifest.permission.CAMERA}, 200);
      // No permission granted.
      return false;
    // The permission has been granted.
    } else return true;
  }

  /**
   * Overrides the default color of native system status bars.
   * @param colorId The new color to be used.
   * @param ctx The activity where this effect must be done.
   */
  public void setBarsColor (int colorId, @NonNull AppCompatActivity ctx) {
    // Gets activity window.
    final Window window = ctx.getWindow();
    // Clears `FLAG_TRANSLUCENT_STATUS` flag.
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    // Adds `FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS` flag to the window.
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    // Sets bottom navigation bar color.
    window.setNavigationBarColor(ContextCompat.getColor(ctx, colorId));
    // Sets top status bar color.
    window.setStatusBarColor(ContextCompat.getColor(ctx, colorId));
  }

  /**
   * Listens `submit` event on an edit text.
   * @param input The field to get listened.
   * @param onSubmit Called when we tap on `return` button from native device keyboard.
   */
  public void trackInputSubmitEvent (@NonNull EditText input, InputSubmitEvent onSubmit) {
    // Tracks native device keyboard `submit` event on that field.
    input.setOnEditorActionListener((v, actionId, event) -> {
      // Whether we detect a tap on `return` button of the keyboard.
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        // Throws `submit` event.
        onSubmit.onSubmit(input);
        // Sends a positive feedback.
        return true;
      }
      // Sends a negative feedback.
      return false;
    });
  }

  /**
   * Opens device native image picker to load an image.
   * @param ctx The activity context.
   */
  public void launchPicker (@NonNull AppCompatActivity ctx) {
    // Asks for storage permission.
    if (this.askForStoragePermission(ctx)) {
      // Creates an instance of an intent for image picker.
      final Intent imagePicker = new Intent(Intent.ACTION_PICK);
      // Sets mme type.
      imagePicker.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
      // Allows .jpeg, jpg and png files only.
      imagePicker.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
      // Pass the request code for future check.
      // noinspection deprecation
      ctx.startActivityForResult(
        Intent.createChooser(imagePicker, ctx.getString(R.string.select_image)), 201
      );
    }
  }

  /**
   * Detects and changes input background drawable regardless focus state.
   * @param input An instance of an edit text.
   * @param ctx The activity context.
   */
  public void manageInputFocus (@NonNull EditText input, AppCompatActivity ctx) {
    // Listens `focus` event on that input.
    input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      // Called when input focus state changed.
      @SuppressLint("UseCompatLoadingForDrawables")
      @Override
      public void onFocusChange (View v, boolean hasFocus) {
        // Whether input gets focus.
        if (hasFocus) {
          // noinspection deprecation
          input.setBackground(ctx.getResources().getDrawable(R.drawable.focused_input_bg));
        // Otherwise.
        } else {
          // noinspection deprecation
          input.setBackground(ctx.getResources().getDrawable(R.drawable.input_bg));
        }
      }
    });
  }

  /**
   * Saves the given api link into local storage.
   * @param newApiLink The api link to contact for submitting image.
   * @param oldApiLink The old saved api link value.
   * @param popup The dialog box object instance.
   * @param ctx The activity context.
   */
  private void saveSettings (
    @NonNull String newApiLink, String oldApiLink, Dialog popup, AppCompatActivity ctx
  ) {
    // Whether we detect some modifications.
    if (!newApiLink.equals(oldApiLink)) {
      // Saves data to preferences.
      Preferences.getInstance().save("api_link", newApiLink, ctx);
      // Makes a toast about save change.
      Toast.makeText(
        ctx, ctx.getString(R.string.settings_save_success), Toast.LENGTH_LONG
      ).show();
    // Otherwise.
    } else {
      // Makes a toast about no change.
      Toast.makeText(
        ctx, ctx.getString(R.string.no_change_detected), Toast.LENGTH_LONG
      ).show();
    }
    // Closes the dialog.
    popup.dismiss();
  }

  /**
   * Returns the real path of a picked file from native picker.
   * @param ctx The application context.
   * @param uri The fetched file uri.
   * @return String
   */
  @Nullable
  public String getRealPath (Uri uri, @NonNull AppCompatActivity ctx) {
    // Gets picker data.
    final String[] proj = {MediaStore.Images.Media.DATA};
    // Gets cursor navigation.
    final Cursor cursor = ctx.getContentResolver().query(uri, proj, null, null, null);
    // Whether that cursor exists.
    if (cursor != null) {
      // Gets element column index.
      final int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      // Refreshes the cursor.
      cursor.moveToFirst();
      // Gets real hidden path.
      final String path = cursor.getString(columnIndex);
      // Free opened stream by cursor.
      cursor.close();
      // Sends retrieved path.
      return path;
    }
    // Sends `null` for others cases.
    return null;
  }

  /**
   * Converts a file binary content into a base64.
   * @param path The path of the file.
   * @return String
   */
  @SuppressWarnings("all")
  @NonNull
  public String getBase64Data (String path) throws IOException {
    // Initializes an output stream.
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    // Creates a stream from the retrieved path.
    final InputStream inputStream = new FileInputStream(path);
    // Allocates a buffer in memory.
    final byte[] buffer = new byte[8192];
    // The file bytes data.
    final byte[] bytes;
    // The cursor to nagivate through bytes.
    int bytesRead;
    // We don't at the end of file.
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      // Creates buffer for each byte.
      output.write(buffer, 0, bytesRead);
    }
    // Gets bytes as an array.
    bytes = output.toByteArray();
    // Sends file as a base64 data url.
    return (
      "data:" + this.getImageMimeType(path) + ";base64," +
      Base64.encodeToString(bytes, Base64.DEFAULT)
    );
  }

  /**
   * Analyses and extracts chosen image data from chooser.
   * @param data The loaded result from picker.
   * @param ctx The activity context.
   * @return Intent
   */
  @SuppressLint("IntentWithNullActionLaunch")
  @Nullable
  public Intent extractImage (Intent data, AppCompatActivity ctx) throws IOException {
    // Whether we have a selected image.
    if (data != null) {
      // Gets image path from data.
      final Uri selectedImageUri = data.getData();
      // Whether we have a valid path.
      if (selectedImageUri != null) {
        // Converts the selected image into bitmap.
        final Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(
          ctx.getContentResolver(), selectedImageUri
        );
        // Creates an intent for image data.
        final Intent imageData = new Intent();
        // Adds bitmap full path.
        imageData.putExtra("path", this.getRealPath(selectedImageUri, ctx));
        // Adds that bitmap format.
        imageData.putExtra("data", selectedImageBitmap);
        // Sends generated intent.
        return imageData;
      }
    }
    // Sends nothing for others cases.
    return null;
  }

  /**
   * Open settings popup to put api link.
   * @param ctx The activity context.
   */
  public void openSettings (AppCompatActivity ctx) {
    // Creates custom dialog box.
    this.initializeDialog((controls, popup) -> {
      // Listens `click` event on `cancel` button.
      controls.findViewById(R.id.cancel).setOnClickListener(view -> popup.cancel());
      // Gets api link from preferences.
      final String apiLink = Preferences.getInstance().load("api_link", "", ctx);
      // Gets api link input.
      final EditText apiLinkInput = controls.findViewById(R.id.apiLink);
      // Manages input focus.
      this.manageInputFocus(apiLinkInput, ctx);
      // Sets input default value.
      apiLinkInput.setText(apiLink);
      // Listens `click` event on `save` button.
      controls.findViewById(R.id.save).setOnClickListener(
        view -> this.saveSettings(apiLinkInput.getText().toString(), apiLink, popup, ctx)
      );
      // Listens `submit` event on input.
      this.trackInputSubmitEvent(
        apiLinkInput, input -> this.saveSettings(
          apiLinkInput.getText().toString(), apiLink, popup, ctx
        )
      );
    }, ctx, true, 24, R.layout.settings_popup);
  }

  /**
   * Initializes and builds a dialog for custom use.
   * @param onInit Called when dialog is fulled initialized.
   * @param ctx The activity context.
   * @param dismissible Whether we can tap outside the dialog to close it.
   * @param padding Overrides padding between window and dialog.
   * @param view The xml file to be used to represent the popup.
   */
  public void initializeDialog (
    @NonNull DialogEvent onInit,
    AppCompatActivity ctx,
    boolean dismissible,
    int padding,
    int view
  ) {
    // Creates an alert builder.
    final AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.DialogTheme);
    // Uses a custom controls view.
    final View controls = ctx.getLayoutInflater().inflate(view, null);
    // Disables cancelable.
    builder.setCancelable(dismissible);
    // Sets default view.
    builder.setView(controls);
    // Let's build the dialog.
    final Dialog dialog = builder.create();
    // Disables cancelable.
    dialog.setCanceledOnTouchOutside(dismissible);
    // Gets dialog window.
    final Window window = dialog.getWindow();
    // Sets default dialog margins and background color.
    if (window != null) window.setBackgroundDrawable(
      new InsetDrawable(new ColorDrawable(Color.TRANSPARENT), padding)
    );
    // Calls `init` event.
    onInit.onInit(controls, dialog);
    // Displays the custom dialog.
    dialog.show();
  }

  /**
   * Launches device native camera.
   * @param ctx The activity context.
   */
  public void launchCamera (AppCompatActivity ctx) {
    // Whether required permissions are granted.
    if (this.askForCameraPermission(ctx) && this.askForStoragePermission(ctx)) {
      // Tries to launch native camera.
      try {
        // Initializes image file to be generated after camera capture.
        final File image = File.createTempFile(
          String.format(
            Locale.US, "%s_%s",
            DateTime.getInstance().parseDate(LocalDate.now()),
            DateTime.getInstance().parseTime(LocalTime.now())
          ).replace(':', '_').replace('/', '_'), ".jpg",
          ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        );
        // Gets that absolute path for future use after image generation.
        this.cameraImgPath = image.getAbsolutePath();
        // Let's create an intent for camera.
        final Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Grants authorities to get generated image file uri.
        camera.putExtra(
          MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
            ctx, "org.cacybernet.numberplatescanner.fileprovider", image
          )
        );
        // noinspection deprecation
        ctx.startActivityForResult(camera, 202);
      // Something wrong.
      } catch (IOException error) {
        // Prints that error.
        this.displayError(error);
      }
    }
  }

  /**
   * Analyses and extracts captured image data from camera.
   * @param data The capture result from camera.
   * @param ctx The activity context.
   * @return Intent
   */
  @SuppressLint("IntentWithNullActionLaunch")
  @Nullable
  public Intent extractCapture (Intent data, AppCompatActivity ctx) {
    // Whether we have a captured image.
    if (data != null) {
      // Gets saved bundle.
      final Bundle bundle = data.getExtras();
      // Whether bundle exists.
      if (bundle != null) {
        // Gets captured image as a bitmap.
        final Bitmap image = (Bitmap) bundle.get("data");
        // Whether an image is really detected.
        if (image != null) {
          // Let's create an intent to pass some data.
          final Intent imageData = new Intent();
          // Sends date.
          imageData.putExtra("date", DateTime.getInstance().parseDate(LocalDate.now()));
          // Sends time.
          imageData.putExtra("time", DateTime.getInstance().parseTime(LocalTime.now()));
          // Calculates image size.
          final double size = (image.getHeight() * image.getWidth() * 3 * 0.000001);
          // Sends image size.
          imageData.putExtra("size", (String.format(Locale.US, "%.2f", size) + " MB"));
          // Sends height.
          imageData.putExtra("height", (image.getHeight() + "pixels"));
          // Sends width.
          imageData.putExtra("width", (image.getWidth() + "pixels"));
          // Sends real path.
          imageData.putExtra("path", data.getStringExtra("path"));
          // Sends capture.
          imageData.putExtra("capture", image);
          // Sends generated intent.
          return imageData;
        }
      }
    // Whether is a camera capture.
    } else if (this.cameraImgPath != null) {
      // Initializes a capture intent.
      final Intent captureIntent = new Intent();
      // Gets temporally image file as a bitmap.
      captureIntent.putExtra("data", BitmapFactory.decodeFile(this.cameraImgPath));
      // Gets image file absolute path.
      captureIntent.putExtra("path", this.cameraImgPath);
      // Clears path value.
      this.cameraImgPath = null;
      // Extracts capture data from that image.
      return this.extractCapture(captureIntent, ctx);
    }
    // Sends nothing for others cases.
    return null;
  }

  /**
   * Sends retrieved data from a formulary and uploads a file image.
   * @param apiUrl The remote back-end server API url.
   * @param path The file real path.
   * @param offense The written text inside description field.
   * @param bitmap The file converted into bitmap format.
   * @param handler The activity handler for external call.
   * @param onSuccess Called when request successfully done.
   * @param onError Called when something wrong.
   */
  public void submitForm (
    String apiUrl, String path, String offense, Bitmap bitmap,
    Handler handler, FormSuccessEvent onSuccess,
    FormErrorEvent onError
  ) {
    // Tries to upload data to the remote back-end server.
    try {
      // Creates an instance of file to contains image.
      final File imageFile = new File(path);
      // Generates a default mme type according to image extension.
      String mmeType = ("image/" + imageFile.getName().split("\\.")[1]);
      // Whether the passed image is a .jpeg/jpg.
      mmeType = (mmeType.equals("image/jpg") ? "image/jpeg" : mmeType);
      // Prepares binary stream for loaded image.
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      // Whether the given image is a .jpeg/jpg.
      if (mmeType.equals("image/jpeg")) bitmap.compress(
        Bitmap.CompressFormat.JPEG, 100, stream
      );
      // Otherwise.
      else bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
      // Gets bitmap as an array of bytes (Binary format).
      final byte[] bitmapdata = stream.toByteArray();
      // Initializes a client http request.
      final OkHttpClient client = new OkHttpClient();
      // Sets default timeout connection.
      client.setConnectTimeout(180, TimeUnit.SECONDS);
      // Sets default write timeout.
      client.setWriteTimeout(180, TimeUnit.SECONDS);
      // Sets default read timeout.
      client.setReadTimeout(180, TimeUnit.SECONDS);
      // Initializes a request for form submission.
      final RequestBody body = new MultipartBuilder()
        .type(MultipartBuilder.FORM).addFormDataPart("offense", offense)
        .addFormDataPart(
          "carimg", imageFile.getName(),
          RequestBody.create(MediaType.parse(mmeType), bitmapdata)
        ).build();
      // The real request to be performed to server.
      final Request request = new Request.Builder().url(apiUrl).post(body).build();
      // Makes that request with provided form data.
      client.newCall(request).enqueue(new Callback() {
        // Called when an error throw on requesting.
        @Override
        public void onFailure (Request request, final IOException error) {
          // Throws `error` event.
          handler.post(() -> onError.onError(error));
        }
        // Called when request is done with no errors.
        @Override
        public void onResponse (Response response) {
          // Throws `success` event.
          handler.post(() -> onSuccess.onSuccess(response));
        }
      });
    // An error throw.
    } catch (Exception error) {
      // Throws `error` event.
      handler.post(() -> onError.onError(error));
    }
  }
}
