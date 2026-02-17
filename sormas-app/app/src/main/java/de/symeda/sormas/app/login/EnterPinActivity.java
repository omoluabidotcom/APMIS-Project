/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.login;

import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.ActivityEnterPinLayoutBinding;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.settings.SettingsActivity;
import de.symeda.sormas.app.util.NavigationHelper;

public class EnterPinActivity extends AppCompatActivity implements NotificationContext {

	public static final String CALLED_FROM_SETTINGS = "calledFromSettings";
	public static final String REINITIALIZE_APP = "reinitializeApp";

	private boolean calledFromSettings;
	private boolean reinitializeApp;

	private String lastEnteredPIN;
	private boolean confirmedCurrentPIN;
	private boolean triedAgain;
	private EditText[] pinFields;
	private ProgressDialog progressDialog = null;
	private ProgressDialog reInitializeprogressDialog;

	private ActivityEnterPinLayoutBinding binding;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_pin_layout);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			if (params.containsKey(CALLED_FROM_SETTINGS)) {
				calledFromSettings = params.getBoolean(CALLED_FROM_SETTINGS);
			}

			if (params.containsKey(REINITIALIZE_APP)) {
				reinitializeApp = params.getBoolean(REINITIALIZE_APP);
			}
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// sync will be done by other activities anyway...
		//progressDialog = SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, EnterPinActivity.this, null);
	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (reInitializeprogressDialog != null && reInitializeprogressDialog.isShowing()) {
			reInitializeprogressDialog.dismiss();
		}

		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();

		final TextView headline = (TextView) findViewById(R.id.pin_headline_createOrEnter);
		final TextView hint = (TextView) findViewById(R.id.pin_hint_createOrEnter);

		// Hide back to settings button
		findViewById(R.id.action_backToSettings).setVisibility(calledFromSettings ? View.VISIBLE : View.GONE);

		// Hide the forgot PIN button?
		findViewById(R.id.action_forgotPIN).setVisibility(!calledFromSettings && triedAgain ? View.VISIBLE : View.GONE);

		// Adjust headline and hint
		String savedPIN = ConfigProvider.getPin();
		if (savedPIN == null) {
			// Hide the forgot PIN button
			findViewById(R.id.action_forgotPIN).setVisibility(View.GONE);
			if (lastEnteredPIN == null) {
				headline.setText(R.string.heading_create_pin);
				hint.setText(R.string.hint_create_pin);
			} else {
				headline.setText(R.string.heading_confirm_pin);
				hint.setText(R.string.hint_create_pin_again);
			}
		} else {
			if (calledFromSettings) {
				if (confirmedCurrentPIN) {
					if (lastEnteredPIN == null) {
						headline.setText(R.string.heading_create_new_pin);
						hint.setText(R.string.hint_new_pin);
					} else {
						headline.setText(R.string.heading_confirm_pin);
						hint.setText(R.string.hint_new_pin_again);
					}
				} else {
					headline.setText(R.string.heading_enter_pin);
					hint.setText(R.string.hint_enter_current_pin);
				}
			} else {
				headline.setText(R.string.heading_enter_pin);
				hint.setText(R.string.hint_enter_authentication_pin);
			}
		}

		pinFields = new EditText[] {
			(EditText) findViewById(R.id.pin_char1),
			(EditText) findViewById(R.id.pin_char2),
			(EditText) findViewById(R.id.pin_char3),
			(EditText) findViewById(R.id.pin_char4) };

		// Clear the PIN entry fields in case the activity is resumed after an unsuccessful
		// submit attempt or when it has to be entered a second time
		for (int i = 0; i < pinFields.length; i++) {
			pinFields[i].setText("");
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// using this, because onKeyDown does not receiver ENTER key event

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// support hardware keyboard inputs

			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_0:
				enterNumber("0");
				break;
			case KeyEvent.KEYCODE_1:
				enterNumber("1");
				break;
			case KeyEvent.KEYCODE_2:
				enterNumber("2");
				break;
			case KeyEvent.KEYCODE_3:
				enterNumber("3");
				break;
			case KeyEvent.KEYCODE_4:
				enterNumber("4");
				break;
			case KeyEvent.KEYCODE_5:
				enterNumber("5");
				break;
			case KeyEvent.KEYCODE_6:
				enterNumber("6");
				break;
			case KeyEvent.KEYCODE_7:
				enterNumber("7");
				break;
			case KeyEvent.KEYCODE_8:
				enterNumber("8");
				break;
			case KeyEvent.KEYCODE_9:
				enterNumber("9");
				break;
			case KeyEvent.KEYCODE_ENTER:
				submit(null);
				break;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	public void enterNumber(View view) {
		enterNumber(((Button) view).getText());
	}

	public void enterNumber(CharSequence number) {
		if (number.length() != 1 || !Character.isDigit(number.charAt(0))) {
			throw new IllegalArgumentException(number + " is not a single number");
		}

		for (int i = 0; i < pinFields.length; i++) {
			if (pinFields[i].length() == 0) {
				pinFields[i].setText(number);
				break;
			}
		}
	}

	public void deleteNumber(View view) {
		for (int i = pinFields.length - 1; i >= 0; i--) {
			if (pinFields[i].length() > 0) {
				pinFields[i].setText("");
				break;
			}
		}
	}

	private boolean validateNumber(String number, boolean showSnackbar) {

		if (number.length() != 4) {
			if (showSnackbar) {
				NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_too_short);
			}
			return false;
		}

		boolean consecutiveNumbers = Pattern.matches("(0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210)", number);
		if (consecutiveNumbers) {
			if (showSnackbar) {
				NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_no_consecutive);
			}
			return false;
		}

		boolean sameNumbers = Pattern.matches("\\d*?(\\d)\\1{2,}\\d*", number);
		if (sameNumbers) {
			if (showSnackbar) {
				NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_no_same);
			}
			return false;
		}

		return true;
	}

	public void submit(View view) {
		String enteredPIN = "";
		for (int i = 0; i < pinFields.length; i++) {
			enteredPIN += pinFields[i].getText().toString();
		}

		String savedPIN = ConfigProvider.getPin();

		if (savedPIN == null) {
			if (lastEnteredPIN == null) {
				// validate the entered pin
				if (!validateNumber(enteredPIN, true)) {
					onResume();
					return;
				}
				// Store the entered PIN and restart the activity because the user has to enter it twice
				lastEnteredPIN = enteredPIN;
				onResume();
			} else {
				// Check whether the two entered PINs match - if yes, store it and process the login,
				// otherwise display an error message and restart the activity
				if (lastEnteredPIN.equals(enteredPIN)) {
					ConfigProvider.setPin(enteredPIN);
					ConfigProvider.setAccessGranted(true);
					NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_pin_correct_loading);
					finish();
				} else {
					lastEnteredPIN = null;
					NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_not_matching);
					onResume();
				}
			}
		} else {
			if (calledFromSettings) {
				if (confirmedCurrentPIN) {
					if (lastEnteredPIN == null) {
						// validate the entered pin
						if (!validateNumber(enteredPIN, true)) {
							onResume();
							return;
						}
						// Store the entered PIN and restart the activity because the user has to enter it twice
						lastEnteredPIN = enteredPIN;
						onResume();
					} else {
						if (lastEnteredPIN.equals(enteredPIN)) {
							ConfigProvider.setPin(enteredPIN);
							NotificationHelper.showNotification(binding, NotificationType.INFO, R.string.message_pin_changed);
							finish();
						} else {
							lastEnteredPIN = null;
							NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_not_matching);
							onResume();
						}
					}
				} else {
					if (enteredPIN.equals(savedPIN)) {
						confirmedCurrentPIN = true;
						onResume();
					} else {
						NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_wrong);
						triedAgain = true;
						onResume();
					}
				}
			}
			else if(reinitializeApp) {
				// Process the login if the PIN is correct, otherwise display an error message and restart the activity
				if (enteredPIN.equals(savedPIN)) {
					ConfigProvider.setAccessGranted(true);
					NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_pin_correct_loading_to_reinitialize);
try {
	showReinitializeLoadingDialog();
}catch (Exception e){
	Log.e(getClass().getName(), "Exception from dialog initialization", e);
	NotificationHelper.showNotification(binding, NotificationType.ERROR,
			"Failed to initialize reinitialization dialog: " + e.getMessage());

}
//					finish();
				} else {
					NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_wrong);
					triedAgain = true;
					onResume();
				}
			}
			else {
				// Process the login if the PIN is correct, otherwise display an error message and restart the activity
				if (enteredPIN.equals(savedPIN)) {
					ConfigProvider.setAccessGranted(true);
					NotificationHelper.showNotification(binding, NotificationType.SUCCESS, R.string.message_pin_correct_loading);
					finish();
				} else {
					NotificationHelper.showNotification(binding, NotificationType.ERROR, R.string.message_pin_wrong);
					triedAgain = true;
					onResume();
				}
			}
		}
	}

	private void showReinitializeLoadingDialog() {
		if (isFinishing() || isDestroyed()) {
			Log.e(getClass().getName(), "Cannot show dialog - activity is finishing or destroyed");
			return;
		}

		reInitializeprogressDialog = new ProgressDialog(this);
		reInitializeprogressDialog.setTitle("Checking Connection");
		reInitializeprogressDialog.setMessage("Verifying network connectivity...");
		reInitializeprogressDialog.setCancelable(false);
		reInitializeprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		try {
			reInitializeprogressDialog.show();
		} catch (Exception e) {
			Log.e(getClass().getName(), "Failed to show dialog", e);
			throw e;
		}

		// Check connection and synchronize using the same pattern as LoginActivity
		RetroProvider.connectAsyncHandled(this, true, true, result -> {
			if (Boolean.TRUE.equals(result)) {
				// Connection successful, update UI and start sync
				runOnUiThread(() -> {

					System.out.println("Syncronizing data ");
					reInitializeprogressDialog.setTitle("Reinitializing App");
					reInitializeprogressDialog.setMessage("Synchronizing APMIS error logs with server. This may take a while, please wait...");
				});

				// Start synchronization
				System.out.println("Syncronizing data  calll ");


				SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.CompleteAndRepull, getApplicationContext(), (syncFailed, syncFailedMessage) -> {

					// Always disconnect after sync
					RetroProvider.disconnect();

					System.out.println("Dosconnecting sync ------");


					runOnUiThread(() -> {
//						if (reInitializeprogressDialog != null && reInitializeprogressDialog.isShowing()) {
//							reInitializeprogressDialog.dismiss();
//						}
						if (!syncFailed) {
							// Sync successful, proceed with database clearing and restart
							System.out.println("Syncronizing did not fail ");


							if (!isFinishing() && !isDestroyed() && reInitializeprogressDialog != null && reInitializeprogressDialog.isShowing()) {
								reInitializeprogressDialog.setTitle("Sync Completed");
								reInitializeprogressDialog.setMessage("✓ Device information and error logs synchronized successfully!");

								// Wait 7 seconds before proceeding to database clearing
								new android.os.Handler().postDelayed(() -> {
									if (!isFinishing() && !isDestroyed() && reInitializeprogressDialog != null) {
										// Update dialog for database clearing phase
										reInitializeprogressDialog.setTitle("Restoring Database");
										reInitializeprogressDialog.setMessage("Restoring device data ...");

										// Wait another 7 seconds before actually clearing
										new android.os.Handler().postDelayed(() -> {
											if (!isFinishing() && !isDestroyed() && reInitializeprogressDialog != null) {
												reInitializeprogressDialog.setTitle("Restore Successful");
												reInitializeprogressDialog.setMessage("✓ App restored successfully! Restarting APMIS...");

												// Final delay before restart
												new android.os.Handler().postDelayed(() -> {
													if (!isFinishing() && !isDestroyed() && reInitializeprogressDialog != null) {
														reInitializeprogressDialog.dismiss();
//														restartApp();
														clearDatabaseAndRestart();
													}
												}, 7000); // 7 seconds
											}
										}, 7000); // 7 seconds
									}
								}, 7000); // 7 seconds
							}

//							clearDatabaseAndRestart();
						} else {
							// Sync failed
							NotificationHelper.showNotification(EnterPinActivity.this, NotificationType.ERROR,
									syncFailedMessage != null ? syncFailedMessage : getString(R.string.error_synchronization));
						}
					});
				});
			} else {
				// Connection failed
				runOnUiThread(() -> {
					if (reInitializeprogressDialog != null && reInitializeprogressDialog.isShowing()) {
						reInitializeprogressDialog.dismiss();
					}
					showConnectionErrorWithRetry();
				});
			}
		});
	}


	private void restartApp() {
		Intent intent = getPackageManager()
				.getLaunchIntentForPackage(getPackageName());

//		Intent intent = new Intent(this, SettingsActivity.class);

		if (intent != null) {
			intent.addFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP |
							Intent.FLAG_ACTIVITY_CLEAR_TASK |
							Intent.FLAG_ACTIVITY_NEW_TASK
			);
			startActivity(intent);
		}

		finishAffinity(); // Close all existing activities
	}


	private void showConnectionErrorWithRetry() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connection Error");
		builder.setMessage("No network connection available. Please check your internet connection and try again.");
		builder.setCancelable(false);

		// Try Again button
		builder.setPositiveButton("Try Again", (dialog, which) -> {
			dialog.dismiss();
			// Retry the reinitialization process
			showReinitializeLoadingDialog();
		});

		// Cancel button
		builder.setNegativeButton("Cancel", (dialog, which) -> {
			dialog.dismiss();
			NotificationHelper.showNotification(binding, NotificationType.INFO,
					"Reinitialization cancelled. You can try again later.");
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void clearDatabaseAndRestart() {
		try {
			de.symeda.sormas.app.backend.common.DatabaseHelper.dropDatabase();
			de.symeda.sormas.app.backend.config.ConfigProvider.clearUserLogin();
			de.symeda.sormas.app.backend.config.ConfigProvider.clearPin();
			de.symeda.sormas.app.backend.common.DatabaseHelper.clearConfigTable();

			// Restart the app by launching LoginActivity
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);

			// Finish current activity
			finish();

		} catch (Exception e) {
			if (!isFinishing() && !isDestroyed()) {
				Log.e(getClass().getName(), "Error during database clearing", e);
				NotificationHelper.showNotification(binding, NotificationType.ERROR,
						"Error during reinitialization: " + e.getMessage());
			}
		}
	}

	public void backToSettings(View view) {
		NavigationHelper.goToSettings(view.getContext());
	}

	public void forgotPIN(final View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
		builder.setPositiveButton(view.getContext().getResources().getText(R.string.action_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ConfigProvider.clearUserLogin();
				ConfigProvider.clearPin();
				Intent intent = new Intent(view.getContext(), LoginActivity.class);
				startActivity(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(view.getContext().getResources().getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.setCancelable(true);
		dialog.setTitle(view.getContext().getResources().getText(R.string.heading_reset_PIN).toString());
		dialog.setMessage(view.getContext().getResources().getText(R.string.info_reset_pin).toString());
		dialog.show();
	}

	@Override
	public View getRootView() {
		if (binding != null)
			return binding.getRoot();

		return null;
	}
}
