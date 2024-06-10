/*
 * @project: Numberplate Scanner - https://gitlab.com/obrymec/number_plate_scanner
 * @author: Obrymec - obrymecsprinces@gmail.com
 * @fileoverview: The startup screen.
 * @file: SplashScreen.java
 * @created: 2024-04-27
 * @updated: 2024-05-11
 * @supported: ANDROID
 * @version: 0.0.2
 */

/// Package name.
package org.cacybernet.numberplatescanner;

/// Android dependencies.
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

/// Custom dependencies.
import org.cacybernet.numberplatescanner.presentation.MainScreen;
import org.cacybernet.numberplatescanner.utils.System;

/**
 * The application splash screen for the first startup.
 */
@SuppressLint("CustomSplashScreen")
@SuppressWarnings("unused")
public class SplashScreen extends AppCompatActivity {
  /**
   * Called when this activity is loaded and ready to use.
   * @param savedInstanceState The saved instance state.
   */
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    // Calls his parent method.
    super.onCreate(savedInstanceState);
    // Binds the xml file to this java class.
    this.setContentView(R.layout.splash_screen);
    // Changes native status bars color.
    System.getInstance().setBarsColor(R.color.primary_900, this);
    // Waits for (03) seconds before go to dashboard.
    new Handler().postDelayed(() -> {
      // Go to dashboard.
      this.startActivity(new Intent(this, MainScreen.class));
      // Destroys splash screen.
      this.finish();
    // After (03) seconds.
    }, 3000);
  }
}
