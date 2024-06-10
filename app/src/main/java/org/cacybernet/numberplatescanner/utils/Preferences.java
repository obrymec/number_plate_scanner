/*
 * @project: Numberplate Scanner - https://gitlab.com/obrymec/number_plate_scanner
 * @fileoverview: Provides commons methods for native android preferences.
 * @author: Obrymec - obrymecsprinces@gmail.com
 * @file: Preferences.java
 * @created: 2024-04-29
 * @updated: 2024-05-11
 * @supported: ANDROID
 * @version: 0.0.1
 */

/// Package name.
package org.cacybernet.numberplatescanner.utils;

/// Android dependencies.
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

/**
 * Provides methods to manages native android preferences.
 */
@SuppressWarnings("unused")
public final class Preferences {
  /// Attributes.
  private static final String PREFS_NAME = "NP_SCANNER_PREFS";
  private static Preferences instance = null;

  /**
   * Blocks any object instantiation.
   */
  private Preferences () {}

  /**
   * Displays a log message for debugging treatments.
   * @param message The message to be printed on console.
   */
  @SuppressWarnings("all")
  private void debug (String message) {
    // Whether verbose mode is enabled.
    if (false) Log.d(PREFS_NAME, message);
  }

  /**
   * Returns a single instance of this class.
   * @return Preferences
   */
  @SuppressWarnings("all")
  public static Preferences getInstance () {
    // Whether there are no instance.
    if (instance == null) instance = new Preferences();
    // Sends that unique instance.
    return instance;
  }

  /**
   * Saves a key with its value into preferences.
   * @param key The data identifier key name.
   * @param value The stored data value.
   * @param ctx The current activity context.
   */
  public void save (String key, String value, AppCompatActivity ctx) {
    // Whether key and value are defined.
    if (ctx != null && value != null && key != null) {
      // Gets shared preferences.
      final SharedPreferences prefs = ctx.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
      );
      // Creating an editor object to write on the file.
      final SharedPreferences.Editor editor = prefs.edit();
      // Storing the key and its value as a data.
      editor.putString(key, value);
      // Commits changes.
      editor.apply();
    // Otherwise.
    } else this.debug("Unable to save {" + key + "}.");
  }

  /**
   * Loads value associated to a key from prefs.
   * @param key The data identifier key name.
   * @param init The default value to send for unexpected cases.
   * @param ctx The current activity context.
   * @return String
   */
  public String load (String key, String init, AppCompatActivity ctx) {
    // Whether key and value are defined.
    if (ctx != null && key != null) {
      // Gets shared preferences.
      final SharedPreferences prefs = ctx.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
      );
      // Fetches associated value.
      return prefs.getString(key, init);
    // Otherwise.
    } else {
      // Makes a debug log.
      this.debug("Unable to load {" + key + "}.");
      // No data found.
      return init;
    }
  }
}
