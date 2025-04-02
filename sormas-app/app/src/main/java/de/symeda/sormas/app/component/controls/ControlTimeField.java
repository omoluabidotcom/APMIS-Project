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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.fragment.app.FragmentManager;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.util.DateFormatHelper;

public class ControlTimeField extends ControlPropertyEditField<Date> {

	// Views
	protected EditText input;

	// Listeners
	private InverseBindingListener inverseBindingListener;

	// Other fields
	private FragmentManager fragmentManager;
	private SimpleDateFormat timeFormat;
	private Date cachedTime;
	private boolean is24HourFormat;

	// Constructors
	public ControlTimeField(Context context) {
		super(context);
		init();
	}

	public ControlTimeField(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ControlTimeField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
		this.is24HourFormat = true; // Default to 24-hour format
	}

	/**
	 * Shows a time picker dialog linked with the value of this field.
	 */
//	private void showTimePicker() {
//		if (fragmentManager == null) {
//			Log.e(getClass().getName(), "Tried to show time picker before setting fragment manager");
//			return;
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		if (getValue() != null) {
//			calendar.setTime(getValue());
//		}
//
//		TimePickerDialog timePickerDialog = new TimePickerDialog(
//				getContext(),
//				(view, hourOfDay, minute) -> {
//					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//					calendar.set(Calendar.MINUTE, minute);
//					setValue(calendar.getTime());
//				},
//				calendar.get(Calendar.HOUR_OF_DAY),
//				calendar.get(Calendar.MINUTE),
//				is24HourFormat
//		);
//
//		timePickerDialog.show();
//	}

	/**
	 * Shows a date fragment linked with the value of this field.
	 * You need to set the fragment manager with initializeDateField before calling this method.
	 */
	private void showTimeFragment() {
		if (fragmentManager == null) {
			Log.e(getClass().getName(), "Tried to show date fragment before setting fragment manager");
			return;
		}

		ControlTimePickerFragment fragment = new ControlTimePickerFragment();
		fragment.setOnTimeSetListener(new  TimePickerDialog.OnTimeSetListener() {
			/**
			 * Called when the user is done setting a new time and the dialog has
			 * closed.
			 *
			 * @param view      the view associated with this listener
			 * @param hourOfDay the hour that was set
			 * @param minute    the minute that was set
			 */
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				if (cachedTime == null) {
					cachedTime = new Date();
				}
				input.setText(DateHelper.formatTime(DateHelper.parseDate(input.getText().toString(), timeFormat)));

			}

		});
		fragment.setOnClearListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				input.setText(null);
			}
		});

		Bundle dateBundle = new Bundle();
		dateBundle.putSerializable(ControlDatePickerFragment.KEY_DATE, this.getFieldValue());
		fragment.setArguments(dateBundle);
		fragment.show(fragmentManager, getResources().getText(R.string.hint_select_a_date).toString());
	}

	private void setUpOnClickListener() {
		input.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled()) {
					return;
				}

//                showOrHideNotifications(v.hasFocus());

				//if (v.hasFocus()) {
				showTimeFragment();
				//}
			}
		});
	}

	public void initializeTimeField(FragmentManager fm) {
		this.fragmentManager = fm;
	}

	// Overrides
	@Override
	public Date getValue() {
		return (Date) super.getValue();
	}

	@Override
	protected Date getFieldValue() {
		if (StringUtils.isEmpty(input.getText())) {
			return null;
		}

		try {
			return timeFormat.parse(input.getText().toString());
		} catch (ParseException e) {
			Log.e(getClass().getName(), "Could not parse time: " + input.getText());
			return null;
		}
	}

	@Override
	protected void setFieldValue(Date value) {
		cachedTime = value;

		if (value == null) {
			input.setText(null);
		} else {
			input.setText(timeFormat.format(value));
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		input.setEnabled(enabled);
		if (label != null) {
			label.setEnabled(enabled);
		}
	}

	@Override
	public void setHint(String value) {
		this.hint = value;
		input.setHint(value);
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlTimeField, 0, 0);

			try {
				is24HourFormat = true ;//a.getBoolean(R.styleable.is24HourFormat, true);
			} finally {
				a.recycle();
			}
		}
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater != null) {
			inflater.inflate(R.layout.control_time_picker_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		((ControlTimeField) nextView).input.requestFocus();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initInput(false, true);
	}
	protected void initInput(boolean isIntegerFlag, boolean isRequired) {
		input = (EditText) this.findViewById(R.id.time_input);
		input.setInputType(InputType.TYPE_NULL);
		input.setTextAlignment(getTextAlignment());
		//if(isIntegerFlag){
		setHint("");
		//	}
		if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
			input.setGravity(getGravity());
		}

		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (inverseBindingListener != null) {
					inverseBindingListener.onChange();
				}
				onValueChanged();
			}
		});

		addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				if (!isLiveValidationDisabled()) {
					((ControlDateField) field).setErrorIfOutOfDateRange();
				}
			}
		});


		required = isRequired;

		CharSequence valx = input.getText();
		if(valx == null && required){
			setSoftRequired(true);
			input.setError("!");
			return;
		}

//       setUpOnFocusChangeListener();
		setUpOnClickListener();
	}


	private void validateTime(boolean isRequired) {
		if (getValue() == null && isRequired) {
			enableErrorState("Time is required");
		} else {
			disableErrorState();
		}
	}

	// Data binding, getters & setters
	@BindingAdapter("value")
	public static void setValue(ControlTimeField view, Date time) {
		view.setFieldValue(time);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Date getValue(ControlTimeField view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(ControlTimeField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	@BindingAdapter("timeFormat")
	public static void setTimeFormat(ControlTimeField field, SimpleDateFormat timeFormat) {
		field.timeFormat = timeFormat;
	}

	@BindingAdapter("is24HourFormat")
	public static void setIs24HourFormat(ControlTimeField field, boolean is24HourFormat) {
		field.is24HourFormat = is24HourFormat;
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

}