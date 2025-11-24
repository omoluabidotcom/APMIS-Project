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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class ControlTextEditFieldRange extends ControlPropertyEditField<String> {

    // Views

    protected EditText input;

    // Attributes

    private boolean singleLine;
    private int maxLines;
    private int maxLength;
    private int minLength;
    private boolean textArea;
    private int inputType;
    boolean isInternalChange = false;


    private boolean enableZeroHandling = false;


    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private OnClickListener onClickListener;
 
    boolean isUpdatingText = false;
    private TextWatcher textWatcher;

 
    // Constructors

    public ControlTextEditFieldRange(Context context) {
        super(context);
    }

    public ControlTextEditFieldRange(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlTextEditFieldRange(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    public void setCursorToRight() {
        input.setSelection(input.getText().length());
    }

    /**
     * Handles clicks on the buttons to switch to the next view.
     */
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
                            if (nextView instanceof ControlTextEditFieldRange) {
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

    // Overrides

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
        System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 111111111111111111111111111111111");
        if (isUpdatingText) {
            System.out.println("Preventing recursion - isUpdatingText is true");
            System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 22222222222222222222222222222222");
            return;
        }

        // Check if value is actually different
        String currentValue = input.getText().toString();

        // Handle null values and empty strings consistently
        String newValue = (value == null) ? "" : value;
        String current = (currentValue == null) ? "" : currentValue;
        System.out.println(newValue + " SETFIELDVALUEEEEEEEEEEEEEEEEE " + currentValue + "EEEEEEEEE 3333333333333 " + current);
        if (current.equals(newValue)) {
            System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 444444444444444444444444444");
            return; // No change needed
        }

        System.out.println("String value being set--------- " + newValue);

        isUpdatingText = true;
        try {
            System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 55555555555555555555555555");
            // Remove listener before setText to prevent TextWatcher from triggering
            if (textWatcher != null) {
                input.removeTextChangedListener(textWatcher);
                System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 666666666666666666666666");
            }

            if (newValue.equals("0")) {
                input.setText("");
            } else {
                input.setText("");
                input.setText(newValue);
            }
//            input.setText(null);

            // IMPORTANT: Reset error state when value is set programmatically
            if (newValue.isEmpty() || "0".equals(newValue)) {
                input.setError(null);
                disableErrorState();
                System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 7777777777777777777777");
            }

            // Re-add listener after setText
            if (textWatcher != null) {
                input.addTextChangedListener(textWatcher);
                System.out.println("SETFIELDVALUEEEEEEEEEEEEEEEEE 888888888888888888888888888");
            }
        } finally {
            isUpdatingText = false;
        }
    }
//
//    @Override
//    protected void setFieldValue(String value) {
//        if (isUpdatingText) {
//            System.out.println("Preventing recursion - isUpdatingText is true");
//            return;
//        }
//
//        // Check if value is actually different
//        String currentValue = input.getText().toString();
//        if (currentValue.equals(value == null ? "" : value)) {
//            return; // No change needed
//        }
//
//        System.out.println("String value being set--------- " + value);
//
//        isUpdatingText = true;
//        try {
//            // Remove listener before setText
//            if (textWatcher != null) {
//                input.removeTextChangedListener(textWatcher);
//            }
//
//            input.setText(value);
//
//            // Re-add listener after setText
//            if (textWatcher != null) {
//                input.addTextChangedListener(textWatcher);
//            }
//        } finally {
//            isUpdatingText = false;
//        }
//    }

//    @Override
//    protected void setFieldValue(String value) {
//        if (isUpdatingText) return;
//
//        String currentValue = input.getText().toString();
//        if (currentValue.equals(value)) return; // Don't update if same
//
//        isUpdatingText = true;
//        try {
//            input.setText(value);
//        } finally {
//            isUpdatingText = false;
//        }
//    }

    // Add this method for safe expression updates
    public void setFieldValueFromExpression(String value) {
        isUpdatingText = true;
        try {
            System.out.println("Setting value from expression: " + value);
            input.setText(value);
        } finally {
            isUpdatingText = false;
        }
     }

//
//    public void setFieldAsError(String errorMessage) {
//        enableErrorState(errorMessage);
////        setErrorIfEmptyRange();
//    }

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
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlTextEditField, 0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.ControlTextEditField_singleLine, true);
                maxLines = a.getInt(R.styleable.ControlTextEditField_maxLines, 1);
                textArea = a.getBoolean(R.styleable.ControlTextEditField_textArea, false);
                maxLength = a.getInt(
                        R.styleable.ControlTextEditField_maxLength,
                        textArea ? FieldConstraints.CHARACTER_LIMIT_BIG : FieldConstraints.CHARACTER_LIMIT_UUID_MIN);

                minLength = a.getInt(
                        R.styleable.ControlTextEditField_minLength,
                        textArea ? FieldConstraints.CHARACTER_LIMIT_BIG : FieldConstraints.CHARACTER_LIMIT_DEFAULT);
                inputType = a.getInt(R.styleable.ControlTextEditFieldRange_inputType, InputType.TYPE_CLASS_NUMBER);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (textArea) {
                inflater.inflate(R.layout.control_textfield_edit_multi_row_layout, this);
            } else if (isSlim()) {
                inflater.inflate(R.layout.control_textfield_edit_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_textfield_edit_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initInput(true, false, true, null, null, false, false);
    }


 
//    protected void initInput(boolean isIntegerFlag, boolean isRequired, boolean isRange, Integer minValue, Integer maxValue, Boolean isExpression, Boolean warnOnError) {
//
//        input = (EditText) this.findViewById(R.id.text_input);
//        input.setTextAlignment(getTextAlignment());
//        if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
//            input.setGravity(getGravity());
//        }
//
//        if (isIntegerFlag) {
//            // SET STRICT INPUT TYPE - NO DECIMALS ALLOWED
//            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//
//            // Create a strict filter that only allows digits 0-9
//            InputFilter digitFilter = new InputFilter() {
//                @Override
//                public CharSequence filter(CharSequence source, int start, int end,
//                                           Spanned dest, int dstart, int dend) {
//                    // Allow deletion
//                    if (source.length() == 0) {
//                        return null;
//                    }
//
//                    // Check each character being added
//                    StringBuilder filteredString = new StringBuilder();
//                    for (int i = start; i < end; i++) {
//                        char c = source.charAt(i);
//                        // ONLY allow digits 0-9 - explicitly block everything else
//                        if (c >= '0' && c <= '9') {
//                            filteredString.append(c);
//                        } else {
//                            // Block decimal points, minus signs, spaces, etc.
//                            return "";
//                        }
//                    }
//                    return filteredString.toString();
//                }
//            };
//
//            // Apply filters
//            InputFilter[] filters;
//            if (getMaxLength() >= 0) {
//                filters = new InputFilter[]{
//                        new InputFilter.LengthFilter(getMaxLength()),
//                        digitFilter
//                };
//            } else {
//                filters = new InputFilter[]{digitFilter};
//            }
//            input.setFilters(filters);
//
//            // REMOVE DigitsKeyListener as it might conflict
//            // input.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
//
//        }

    public void initInput(boolean isIntegerFlag, boolean isRequired, boolean isRange, Integer minValue, Integer maxValue, Boolean isExpression, Boolean warnOnError) {
         input = (EditText) this.findViewById(R.id.text_input);
        input.setTextAlignment(getTextAlignment());
        if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getGravity());
        }

        if (isIntegerFlag) {
            // SET STRICT INPUT TYPE - NO DECIMALS ALLOWED
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

 
            InputFilter digitFilter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    // Allow deletion
                    if (source.length() == 0) {
                        return null;
                    }

                    // Check each character being added
                    StringBuilder filteredString = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
 
                        // ONLY allow digits 0-9
                        if (c >= '0' && c <= '9') {
                            filteredString.append(c);
                        } else {
                            // Block everything else
                            return "";
                        }
                    }

                    // Handle leading zeros
                    String result = filteredString.toString();
                    String newText = dest.subSequence(0, dstart) + result + dest.subSequence(dend, dest.length());

                    // Prevent multiple leading zeros
                    if (newText.length() > 1 && newText.startsWith("0")) {
                        return "";
                    }

                    return result;
                }
            };

             InputFilter[] filters;
            if (getMaxLength() >= 0) {
                filters = new InputFilter[]{
                        new InputFilter.LengthFilter(getMaxLength()),
                        digitFilter
                };
            } else {
                filters = new InputFilter[]{digitFilter};
            }
            input.setFilters(filters);
 
        }else if (getMaxLength() >= 0) {
             input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getMaxLength())});
        }

        required = isRequired;
 


        textWatcher = new TextWatcher() {
             String beforeData = "";
            String onChangeData = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeData = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onChangeData = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();

 
                System.out.println("Text changed to: '" + text + "'");

                // If we're already updating text programmatically, skip processing
                if (isUpdatingText) {
                    System.out.println("Skipping - programmatic update in progress");
                    return;
                }

                // Allow empty field
                if (text.isEmpty()) {
                    if (inverseBindingListener != null) {
                        inverseBindingListener.onChange();
                    }
                    onValueChanged();
                    return;
                }

                // STRICTER REGEX PROTECTION FOR INTEGER FIELDS
                if (isIntegerFlag) {
                    System.out.println("isIntegerFlag: " + isIntegerFlag);

                    // Allow "0" as a valid value - don't clean it
                    if (text.equals("0")) {
                        System.out.println("Single zero detected - allowing");
                        // Continue with normal processing
                    }
                    else if (!text.matches("^\\d+$")) {
                        System.out.println("Invalid characters detected, cleaning...");

                        isUpdatingText = true;

                        String cleanedText = text.replaceAll("[^0-9]", "");

                        System.out.println("cleanedText: '" + cleanedText + "'");

                        // Handle leading zeros for numbers other than single zero
                        if (cleanedText.length() > 1 && cleanedText.startsWith("0")) {
                            System.out.println("Removing leading zeros");
                             cleanedText = cleanedText.replaceFirst("^0+", "");
                            if (cleanedText.isEmpty()) cleanedText = "0";
                        }
 
                        // Only update if different and not empty
                        if (!cleanedText.equals(text) && !cleanedText.isEmpty()) {
                            System.out.println("Updating text from '" + text + "' to '" + cleanedText + "'");

                            // Remove listener to prevent recursion
                            if (textWatcher != null) {
                                input.removeTextChangedListener(textWatcher);
                            }

                            input.setText(cleanedText);
                            input.setSelection(cleanedText.length());

                            // Re-add listener after modification
                            if (textWatcher != null) {
                                input.addTextChangedListener(textWatcher);
                            }

                            NotificationHelper.showNotification((NotificationContext) getContext(), WARNING, "Only whole numbers are allowed");
                        } else {
                            System.out.println("No update needed - text is already clean");
                        }

                        isUpdatingText = false;
                        return; // Skip the rest of the processing for this change
                    }
                }

                // Only proceed with normal processing if we didn't modify the text
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                 onValueChanged();

                // Your existing range validation logic...
                if (isRange && minValue != null && maxValue != null && !text.isEmpty()) {
                    try {
                        int valxx = Integer.parseInt(text);
                        if (valxx < minValue || valxx > maxValue) {
                            if (warnOnError) {
                                NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING,
                                        "Number must be between " + minValue + " and " + maxValue);
                            } else {
                                input.setError("Number must be between " + minValue + " and " + maxValue);
                                enableErrorState("Number must be between " + minValue + " and " + maxValue);
                            }
                        } else {
                            input.setError(null);
                            disableErrorState();
                        }
                    } catch (NumberFormatException e) {
 
                        input.setError("Please enter a valid number");
                        enableErrorState("Please enter a valid number");
                    }
                }
            }
 
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String text = editable.toString();
//
//                System.out.println("Text changed to: '" + text + "'");
//
//                // Allow empty field - don't process if text is empty
//                if (text.isEmpty()) {
//                    if (inverseBindingListener != null) {
//                        inverseBindingListener.onChange();
//                    }
//                    System.out.println("ext.isEmpty()Text changed to: '" + text + "'");
//
//
//                    onValueChanged();
//                    return;
//                }
//
//                // STRICTER REGEX PROTECTION FOR INTEGER FIELDS
//
//                System.out.println("eisIntegerFlagxt.isEmpty()Text changed to: '" + isIntegerFlag + "'");
//
//                if (isIntegerFlag) {
//
//                    System.out.println("!text.matches(\"^\\\\d+$\") ()Text changed to: '" + text.matches("^\\d+$") + "'");
//
//                    if (text.matches("^\\d+$")) {
//                        isUpdatingText = true; // PREVENT RECURSION
//
//                        String cleanedText = text.replaceAll("[^0-9]", "");
//
//                        System.out.println("cleanedTextcleanedText()Text changed to: '" + cleanedText + "'");
//
//
//                        // IMPROVED: Better handling of single zero and leading zeros
//                        if (cleanedText.equals("0")) {
//                            // Allow single zero
//                            cleanedText = "0";
//                        } else if (cleanedText.length() > 1 && cleanedText.startsWith("0")) {
//
//                            System.out.println("cleanedText.length() > 1 && cleanedText.startsWith(\"0\")Text changed to: '" + (cleanedText.length() > 1 && cleanedText.startsWith("0")) + "'");
//
//                            // Remove leading zeros but keep the number
//                            cleanedText = cleanedText.replaceFirst("^0+", "");
//
//                            if (cleanedText.isEmpty()) cleanedText = "0";
//                        }else{
//
//                            System.out.println("00000000.equals(text)Text changed to: '" + cleanedText.equals(text) + "'");
//
//                            cleanedText = cleanedText;
//                        }
//
//                        // Only update if different
//
//                        System.out.println("!cleanedText.equals(text)Text changed to: '" + cleanedText.equals(text) + "'");
//
//                        if (!cleanedText.equals(text)) {
//                            input.setText(cleanedText);
//                            input.setSelection(cleanedText.length());
//
//                            if (!cleanedText.equals(text)) {
//                                NotificationHelper.showNotification((NotificationContext) getContext(), WARNING, "Only whole numbers are allowed");
//                            }
//                        }else{
//                            System.out.println(" clnedText.equals(text)Text changed to: '" + cleanedText.equals(text) + "'");
////                            input.setText(cleanedText);
//                        }
//
//                        isUpdatingText = false; // RESET FLAG
//
////                        return;
//                    }
//                }
//
//                if (inverseBindingListener != null) {
//                    inverseBindingListener.onChange();
//                }
//                onValueChanged();
//
//                // Your existing range validation logic...
//                if (isRange && minValue != null && maxValue != null && !text.isEmpty()) {
//                    try {
//                        int valxx = Integer.parseInt(text);
//                        if (valxx < minValue || valxx > maxValue) {
//                            if (warnOnError) {
//                                NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING,
//                                        "Number must be between " + minValue + " and " + maxValue);
//                            } else {
//                                input.setError("Number must be between " + minValue + " and " + maxValue);
//                                enableErrorState("Number must be between " + minValue + " and " + maxValue);
//                            }
//                        } else {
//                            input.setError(null);
//                            disableErrorState();
//                        }
//                    } catch (NumberFormatException e) {
//                        input.setError("Please enter a valid number");
//                        enableErrorState("Please enter a valid number");
//                    }
//                }
//            }

//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                System.out.println("===================================================== " + editable.toString());
//                System.out.println("===================================================== " + input.getText());
//
//
//                String text = editable.toString();
//
//                // STRICTER REGEX PROTECTION FOR INTEGER FIELDS
////                if (text.equals("0")) {
////                    // Allow single zero, don't clean it
////                    if (inverseBindingListener != null) {
////                        inverseBindingListener.onChange();
////                    }
////                    onValueChanged();
////                    return;
////                }
//
//                // STRICTER REGEX PROTECTION FOR INTEGER FIELDS
//                if (isIntegerFlag && !text.isEmpty()) {
//                    if (!text.matches("^\\d+$")) {
//                        isUpdatingText = true; // PREVENT RECURSION
//
//                        String cleanedText = text.replaceAll("[^0-9]", "");
//
//                        // Prevent leading zeros (except single zero)
//                        if (cleanedText.length() > 1 && cleanedText.startsWith("0")) {
//                            cleanedText = cleanedText.replaceFirst("^0+", "");
//                            if (cleanedText.isEmpty()) cleanedText = "";
//                        }
//
//                        input.setText(cleanedText);
//                        input.setSelection(cleanedText.length());
//
//                        isUpdatingText = false; // RESET FLAG
//
//                        if (!cleanedText.equals(text)) {
//                            NotificationHelper.showNotification((NotificationContext) getContext(), WARNING, "Only whole numbers are allowed");
//                        }
//                        return;
//                    }
//                }
//
//                if (inverseBindingListener != null) {
//                    inverseBindingListener.onChange();
//                }
//
//                onValueChanged();
//
//                // Your existing range validation logic...
//                if (isRange && minValue != null && maxValue != null && !text.isEmpty()) {
//                    try {
//                        int valxx = Integer.parseInt(text);
//                        if (valxx < minValue || valxx > maxValue) {
//                            if (warnOnError) {
//                                NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING,
//                                        "Number must be between " + minValue + " and " + maxValue);
//                            } else {
//                                input.setError("Number must be between " + minValue + " and " + maxValue);
//                                enableErrorState("Number must be between " + minValue + " and " + maxValue);
//                            }
//                        } else {
//                            input.setError(null);
//                            disableErrorState();
//                        }
//                    } catch (NumberFormatException e) {
//                        // This shouldn't happen with our filters, but just in case
//                        input.setError("Please enter a valid number");
//                        enableErrorState("Please enter a valid number");
//                    }
//                }
//            }

        };

        input.addTextChangedListener(textWatcher);


 
        addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                System.out.println(isLiveValidationDisabled() + " vaue changes isLiveValidationDisabled()----------");
                if (!isLiveValidationDisabled()) {
                    ((ControlTextEditFieldRange) field).setErrorIfEmptyRange();
                }
            }
        });

        setUpOnEditorActionListener();
        setUpOnFocusChangeListener();
        initializeOnClickListener();
    }
 
    private void NumberNumericValueValidator(String errorMessage, String minValue, String maxValue) {
        Integer minValuex = 0;
        Integer maxValuex = 0;
        boolean decimalAllowed;
        boolean onError;


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
            ((ControlTextEditFieldRange) nextView).input.requestFocus();
            ((ControlTextEditFieldRange) nextView).setCursorToRight();
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

 
//    // Data binding, getters & setters
//    @BindingAdapter("value")
//    public static void setValue(ControlTextEditFieldRange view, String text) {
//        if (text == null || text.trim().isEmpty() || text.equals("")) {
//            view.setFieldValue("");
//            return;
//        } else {
//            // STRICTER integer formatting - remove any non-digit characters
//            String cleanedText = text.replaceAll("[^0-9]", "");
//
//            // Prevent leading zeros (except single zero)
//            if (cleanedText.length() > 1 && cleanedText.startsWith("0")) {
//                cleanedText = cleanedText.replaceFirst("^0+", "");
//                if (cleanedText.isEmpty()) cleanedText = "0";
//            }
//
//            view.setFieldValue(cleanedText);
//        }
//    }


    @BindingAdapter("value")
    public static void setValue(ControlTextEditFieldRange view, String text) {
        System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 1111111111111111111111111");
        if (view.isUpdatingText) return;

        if (text == null || text.trim().isEmpty() || text.equals("")) {
            view.setFieldValue("");
            System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 22222222222222222");
            return;
        } else {
            // Clean the value
            String cleanedText = text.replaceAll("[^0-9]", "");
            System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 333333333333333333333333333333");
            // Handle "0" specifically - allow it but don't prevent clearing
            if (cleanedText.equals("0")) {
                view.setFieldValue("0");
                System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 4444444444444444444444444444");
            } else if (!cleanedText.isEmpty()) {
                // Remove leading zeros for other numbers
                System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 55555555555555555555555");
                if (cleanedText.startsWith("0") && cleanedText.length() > 1) {
                    cleanedText = cleanedText.replaceFirst("^0+", "");
                    System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 666666666666666666666666");
                }
                view.setFieldValue(cleanedText);
            } else {
                view.setFieldValue("");
                System.out.println("SETTTTTTTTTTTTTTINGGGGGGGGGGGGGGGGG 777777777777777777777777777777");
            }
        }
    }

//    @BindingAdapter("value")
//    public static void setValue(ControlTextEditFieldRange view, String text) {
//        if (view.isUpdatingText) return;
//
//        if (text == null || text.trim().isEmpty() || text.equals("")) {
//            view.setFieldValue("");
//            return;
//        } else {
//            // IMPROVED: Handle decimal values properly for range fields
//            String cleanedText = text.replaceAll("[^0-9.]", ""); // Allow decimal points
//
//            // Remove multiple decimal points
//            int firstDot = cleanedText.indexOf('.');
//            if (firstDot != -1) {
//                cleanedText = cleanedText.substring(0, firstDot + 1) +
//                        cleanedText.substring(firstDot + 1).replace(".", "");
//            }
//
//            // For range fields, convert to integer if it's a whole number
//            try {
//                double num = Double.parseDouble(cleanedText);
//                if (num == Math.floor(num)) {
//                    cleanedText = String.valueOf((int) num);
//                }
//            } catch (NumberFormatException e) {
//                // Keep original if parsing fails
//            }
//
//            // Prevent leading zeros (except single zero)
//            if (cleanedText.length() > 1 && cleanedText.startsWith("0")) {
//                cleanedText = cleanedText.replaceFirst("^0+", "");
//                if (cleanedText.isEmpty()) cleanedText = "";
//            }
//
//            view.setFieldValue(cleanedText);
//        }
//    }


     @BindingAdapter("value")
    public static void setValue(ControlTextEditFieldRange view, Integer integerValue) {
        if (integerValue != null) {
            view.setFieldValue(String.valueOf(integerValue));
        } else {
            view.setFieldValue("");
        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlTextEditFieldRange view, Float floatValue) {
        if (floatValue != null) {
            // Convert to integer for integer fields
            int intValue = Math.round(floatValue);
            view.setFieldValue(String.valueOf(intValue));
        } else {
            view.setFieldValue("");
        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlTextEditFieldRange view, Double doubleValue) {
        if (doubleValue != null) {
            // Convert to integer for integer fields
            int intValue = (int) Math.round(doubleValue);
            view.setFieldValue(String.valueOf(intValue));
        } else {
            view.setFieldValue("");
        }
    }

    public void setIntegerValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            setFieldValue("");
            return;
        }

        // Clean the value using regex
        String cleanedValue = value.replaceAll("[^0-9]", "");
        if (!cleanedValue.isEmpty()) {
            try {
                // Parse and format to ensure it's a proper integer
                int intValue = Integer.parseInt(cleanedValue);
                setFieldValue(String.valueOf(intValue));
            } catch (NumberFormatException e) {
                setFieldValue("");
            }
        } else {
            setFieldValue("");
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static String getValue(ControlTextEditFieldRange view) {
        return view.getFieldValue();
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Integer getIntegerValue(ControlTextEditFieldRange view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Integer.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }
    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlTextEditFieldRange view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public String getHint() {
        if (input.getHint() == null) {
            return null;
        }

        return input.getHint().toString();
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;

        if (this.singleLine) {
            input.setMaxLines(1);
            input.setVerticalScrollBarEnabled(false);
        } else {
            input.setMaxLines(getMaxLines());
            input.setVerticalScrollBarEnabled(true);
            if (textArea) {
                input.setLines(getMaxLines());
            }
        }
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
        if (input != null)
            input.setInputType(inputType);
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public EditText getInput() {
        return input;
    }

    public void setInput(EditText input) {
        this.input = input;
    }
}
