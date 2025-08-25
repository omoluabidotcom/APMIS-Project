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

package de.symeda.sormas.app.component.controls;

import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class ControlTimeField extends ControlPropertyEditField<String> {

	private boolean isTextChangeFromCode = false;

	protected TextView label;
	protected EditText input;

	private int inputType;

	protected InverseBindingListener inverseBindingListener;
	private OnClickListener onClickListener;

	protected Date selectedTime;
	protected final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

	public ControlTimeField(Context context) {
		super(context);
	}

	public ControlTimeField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlTimeField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setCursorToRight() {
		input.setSelection(input.getText().length());
	}

	private void setUpOnEditorActionListener() {
		input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int definedActionId = v.getImeActionId();
				if (definedActionId == EditorInfo.IME_ACTION_NONE) {
					return false;
				}

				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					int id = getNextFocusForwardId();
					if (id != NO_ID) {
						View nextView = v.getRootView().findViewById(id);
						if (nextView != null && nextView.getVisibility() == VISIBLE) {
							if (nextView instanceof ControlTimeField) {
								requestFocusForContentView(nextView);
							} else if (nextView instanceof ControlPropertyField) {
								((ControlPropertyField) nextView).requestFocusForContentView(nextView);
							} else {
								nextView.requestFocus();
							}
						}
					}

					return true;
				}

				return false;
			}
		});
	}

	private void setUpOnFocusChangeListener() {
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!v.isEnabled()) {
					return;
				}

				showOrHideNotifications(hasFocus);

				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				if (imm != null) {
					if (hasFocus) {
						changeVisualState(VisualState.FOCUSED);
						imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
						// Prevent the content from being automatically selected
						input.setSelection(input.getText().length(), input.getText().length());
						if (onClickListener != null) {
							input.setOnClickListener(onClickListener);
						}
					} else {
						if (hasError) {
							changeVisualState(VisualState.ERROR);
						} else {
							changeVisualState(VisualState.NORMAL);
						}
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						input.setOnClickListener(null);
					}
				}
			}
		});
	}

	private void initializeOnClickListener() {
		if (onClickListener != null) {
			return;
		}

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled()) {
					return;
				}

				showOrHideNotifications(v.hasFocus());

				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				if (imm != null) {
					if (v.hasFocus()) {
						imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
						//// Prevent the content from being automatically selected
						//input.setSelection(input.getText().length(), input.getText().length());
					} else {
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					}
				}
			}
		};
	}

	@Override
	public String getValue() {
		return (String) super.getValue();
	}

	@Override
	protected String getFieldValue() {
		if (input.getText() == null) {
			return null;
		}
		return input.getText().toString();
	}

	@Override
	protected void setFieldValue(String value) {
		isTextChangeFromCode = true;
		input.setText(value);
		isTextChangeFromCode = false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled); // this has to be called first
		input.setEnabled(enabled);
		label.setEnabled(enabled);
	}

	@Override
	public void setHint(String hint) {
		this.hint = hint;
		input.setHint(hint);
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		initInput(false, false, false, null, null, false, false);
	}

	protected void initInput(boolean isIntegerFlag, boolean isRequired, boolean isRange, Integer minValue, Integer maxValue, Boolean isExpression, Boolean warnOnError) {

		required = isRequired;

		CharSequence valx = input.getText();
		if (valx == null && required) {
			return;
		}


		input.addTextChangedListener(new TextWatcher() {

			String beforeData = "";
			String onChangeData = "";
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				beforeData = charSequence+"";
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				onChangeData = charSequence+"";
			}

			@Override
			public void afterTextChanged(Editable editablex) {
				// Only process if change wasn't from code
				if (!isTextChangeFromCode) {
					if (inverseBindingListener != null) {
						inverseBindingListener.onChange();
					}
					onValueChanged();
				}

			}
		});

		setUpOnEditorActionListener();
		setUpOnFocusChangeListener();
		initializeOnClickListener();

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (getHint() == null) {
			setHint(getPrefixCaption());
		}
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		if (nextView != null) {
			((ControlTimeField) nextView).input.requestFocus();
			((ControlTimeField) nextView).setCursorToRight();
		}
	}

	@Override
	protected void changeVisualState(VisualState state) {
		if (getUserEditRight() != null && !ConfigProvider.hasUserRight(getUserEditRight())) {
			state = VisualState.DISABLED;
		}

		if (this.visualState == state) {
			return;
		}

		visualState = state;

		int labelColor = getResources().getColor(state.getLabelColor());
		Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
		int textColor = getResources().getColor(state.getTextColor());
		int hintColor = getResources().getColor(state.getHintColor());

		if (drawable != null) {
			drawable = drawable.mutate();
		}

		label.setTextColor(labelColor);
		setBackground(drawable);

		if (state != VisualState.ERROR) {
			input.setTextColor(textColor);
			input.setHintTextColor(hintColor);
		}

		setEnabled(state != VisualState.DISABLED);
	}

	@Override
	public void setBackgroundResource(int resId) {
		setBackgroundResourceFor(input, resId);
	}

	@Override
	public void setBackground(Drawable background) {
		setBackgroundFor(input, background);
	}

	@BindingAdapter("value")
	public static void setValue(ControlTimeField view, String text) {
		view.setFieldValue(text);
	}

	@BindingAdapter("value")
	public static void setValue(ControlTimeField view, Integer integerValue) {
		if (integerValue != null) {
			view.setFieldValue(String.valueOf(integerValue));
		} else {
			view.setFieldValue(null);
		}
	}

	@BindingAdapter("value")
	public static void setValue(ControlTimeField view, Float floatValue) {
		if (floatValue != null) {
			view.setFieldValue(String.valueOf(floatValue));
		} else {
			view.setFieldValue(null);
		}
	}

	@BindingAdapter("value")
	public static void setValue(ControlTimeField view, Double doubleValue) {
		if (doubleValue != null) {
			view.setFieldValue(String.valueOf(doubleValue));
		} else {
			view.setFieldValue(null);
		}
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static String getValue(ControlTimeField view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(ControlTimeField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	public String getHint() {
		if (input.getHint() == null) {
			return null;
		}

		return input.getHint().toString();
	}

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
		if (input != null)
			input.setInputType(inputType);
	}

	/** Called during constructor to inflate and initialize the layout */
	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater.from(context).inflate(R.layout.control_time_picker_layout, this, true);
		label = findViewById(R.id.control_time_label);
		input = findViewById(R.id.control_time_input);

		input.setInputType(InputType.TYPE_NULL);
		input.setFocusable(false);
		input.setClickable(true);

		input.setTextAlignment(getTextAlignment());
		input.setGravity(getGravity());

		input.setOnClickListener(v -> showTimePickerDialog());
	}

	/** Default label and prefix method override points */
	protected String getPrefixCaption() {
		return "";
	}

	protected String getPrefixDescription() {
		return "";
	}

	public int getTextAlignment() {
		return TEXT_ALIGNMENT_TEXT_START;
	}

	public int getGravity() {
		return Gravity.CENTER_VERTICAL;
	}

	protected void showTimePickerDialog() {
		final Calendar calendar = Calendar.getInstance();
		if (selectedTime != null) {
			calendar.setTime(selectedTime);
		}

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		TimePickerDialog timePickerDialog = new TimePickerDialog(
				getContext(),
				(TimePicker view, int hourOfDay, int minuteOfDay) -> {
					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minuteOfDay);
					selectedTime = calendar.getTime();
					updateInput();
				},
				hour,
				minute,
				true
		);

		timePickerDialog.show();
	}

	private void updateInput() {
		input.setText(selectedTime != null ? timeFormat.format(selectedTime) : "");
	}

	public void setTime(@Nullable Date time) {
		this.selectedTime = time;
		updateInput();
	}

	@Nullable
	public Date getTime() {
		return selectedTime;
	}

	public void setLabel(String labelText) {
		label.setText(labelText);
	}
	public void showError(String message) {
		input.setError(message);
	}
	protected void initializeTimeField(FragmentManager fm) {}
}
