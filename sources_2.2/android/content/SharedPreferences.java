/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content;

import java.util.Map;

/**
 * Interface for accessing and modifying preference data returned by {@link
 * Context#getSharedPreferences}.  For any particular set of preferences,
 * there is a single instance of this class that all clients share.
 * Modifications to the preferences must go through an {@link Editor} object
 * to ensure the preference values remain in a consistent state and control
 * when they are committed to storage.
 *
 * <p><em>Note: currently this class does not support use across multiple
 * processes.  This will be added later.</em>
 *
 * @see Context#getSharedPreferences
 */
public interface SharedPreferences {
    /**
     * Interface definition for a callback to be invoked when a shared
     * preference is changed.
     */
    public interface OnSharedPreferenceChangeListener {
        /**
         * Called when a shared preference is changed, added, or removed. This
         * may be called even if a preference is set to its existing value.
         * 
         * @param sharedPreferences The {@link SharedPreferences} that received
         *            the change.
         * @param key The key of the preference that was changed, added, or
         *            removed.
         */
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
    }
    
    /**
     * Interface used for modifying values in a {@link SharedPreferences}
     * object.  All changes you make in an editor are batched, and not copied
     * back to the original {@link SharedPreferences} or persistent storage
     * until you call {@link #commit}.
     */
    public interface Editor {
        /**
         * Set a String value in the preferences editor, to be written back once
         * {@link #commit} is called.
         * 
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor putString(String key, String value);
        
        /**
         * Set an int value in the preferences editor, to be written back once
         * {@link #commit} is called.
         * 
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor putInt(String key, int value);
        
        /**
         * Set a long value in the preferences editor, to be written back once
         * {@link #commit} is called.
         * 
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor putLong(String key, long value);
        
        /**
         * Set a float value in the preferences editor, to be written back once
         * {@link #commit} is called.
         * 
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor putFloat(String key, float value);
        
        /**
         * Set a boolean value in the preferences editor, to be written back
         * once {@link #commit} is called.
         * 
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor putBoolean(String key, boolean value);

        /**
         * Mark in the editor that a preference value should be removed, which
         * will be done in the actual preferences once {@link #commit} is
         * called.
         * 
         * <p>Note that when committing back to the preferences, all removals
         * are done first, regardless of whether you called remove before
         * or after put methods on this editor.
         * 
         * @param key The name of the preference to remove.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor remove(String key);

        /**
         * Mark in the editor to remove <em>all</em> values from the
         * preferences.  Once commit is called, the only remaining preferences
         * will be any that you have defined in this editor.
         * 
         * <p>Note that when committing back to the preferences, the clear
         * is done first, regardless of whether you called clear before
         * or after put methods on this editor.
         * 
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        Editor clear();

        /**
         * Commit your preferences changes back from this Editor to the
         * {@link SharedPreferences} object it is editing.  This atomically
         * performs the requested modifications, replacing whatever is currently
         * in the SharedPreferences.
         * 
         * <p>Note that when two editors are modifying preferences at the same
         * time, the last one to call commit wins.
         * 
         * @return Returns true if the new values were successfully written
         * to persistent storage.
         */
        boolean commit();
    }

    /**
     * Retrieve all values from the preferences.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     *
     * @throws NullPointerException
     */
    Map<String, ?> getAll();

    /**
     * Retrieve a String value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * 
     * @throws ClassCastException
     */
    String getString(String key, String defValue);
    
    /**
     * Retrieve an int value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * 
     * @throws ClassCastException
     */
    int getInt(String key, int defValue);
    
    /**
     * Retrieve a long value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     * 
     * @throws ClassCastException
     */
    long getLong(String key, long defValue);
    
    /**
     * Retrieve a float value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     * 
     * @throws ClassCastException
     */
    float getFloat(String key, float defValue);
    
    /**
     * Retrieve a boolean value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     * 
     * @throws ClassCastException
     */
    boolean getBoolean(String key, boolean defValue);

    /**
     * Checks whether the preferences contains a preference.
     * 
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     *         otherwise false.
     */
    boolean contains(String key);
    
    /**
     * Create a new Editor for these preferences, through which you can make
     * modifications to the data in the preferences and atomically commit those
     * changes back to the SharedPreferences object.
     * 
     * <p>Note that you <em>must</em> call {@link Editor#commit} to have any
     * changes you perform in the Editor actually show up in the
     * SharedPreferences.
     * 
     * @return Returns a new instance of the {@link Editor} interface, allowing
     * you to modify the values in this SharedPreferences object.
     */
    Editor edit();
    
    /**
     * Registers a callback to be invoked when a change happens to a preference.
     * 
     * @param listener The callback that will run.
     * @see #unregisterOnSharedPreferenceChangeListener
     */
    void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);
    
    /**
     * Unregisters a previous callback.
     * 
     * @param listener The callback that should be unregistered.
     * @see #registerOnSharedPreferenceChangeListener
     */
    void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);
}
