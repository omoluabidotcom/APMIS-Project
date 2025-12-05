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

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.math.BigDecimal;

import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class ControlDecimalEditField extends ControlPropertyEditField<String> {

    // Views
    protected EditText input;

    // Attributes
    private boolean singleLine;
    private int maxLines;
    private int maxLength;
    private int minLength;
    private boolean textArea;
    private int inputType;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private OnClickListener onClickListener;

    // Constructors

    public ControlDecimalEditField(Context context) {
        super(context);
    }

    public ControlDecimalEditField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlDecimalEditField(Context context, AttributeSet attrs, int defStyle) {
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
                            if (nextView instanceof ControlDecimalEditField) {
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
        input.setText(value);
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
                inputType = a.getInt(R.styleable.ControlTextEditField_inputType, InputType.TYPE_CLASS_NUMBER);
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

        initInput(true, false, false, null, null, false, false);
    }

    protected void initInput(boolean isIntegerFlag, boolean isRequired, boolean isDecimal, Integer minValue, Integer maxValue, Boolean isExpression, Boolean warnOnError) {

        input = (EditText) this.findViewById(R.id.text_input);
        input.setTextAlignment(getTextAlignment());
        if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getGravity());
        }
        if (isIntegerFlag) {
            input.setInputType(InputType.TYPE_CLASS_NUMBER |
                    InputType.TYPE_NUMBER_FLAG_DECIMAL |
                    InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else {
 
            input.setInputType(inputType);
        }
        setSingleLine(singleLine);
        if (getMaxLength() >= 0) {
            input.setFilters(
                    new InputFilter[]{
                            new InputFilter.LengthFilter(240)}
            );
        }

        required = isRequired;
        CharSequence valx = input.getText();
        if (valx == null && required) {
//              setSoftRequired(true);

            //   input.setError("!");
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

                System.out.println("===================================================== "+editablex.toString());
                System.out.println("===================================================== "+input.getId());

                String text = editablex.toString();

                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();

                if (isDecimal && minValue != null && maxValue != null) {
 
                	if (minValue != null && maxValue != null && input.getText() != null) {
                        String inputText = input.getText().toString();
                        if (!inputText.equals("") && !inputText.isEmpty() && !inputText.equals(".") && !inputText.endsWith(".")) {
                            try{
                                double valxx = Double.parseDouble(inputText);

                                if (valxx >= Double.parseDouble(minValue.toString())  && valxx <= Double.parseDouble(maxValue.toString())) {
                                    // Clear error when valid
                                    input.setError(null);
                                    disableErrorState();
                                    hasError = false;
                                } else if (warnOnError) {
                                    NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING, "Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
                                    hasError = true;
                                } else {
                                    input.setError("Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
//                                    setErrorIfEmptyRange();
                                    enableErrorState("Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
                                    hasError = true;
                                }

                            }catch(NumberFormatException e ){
                                if (!inputText.endsWith(".")) {
                                    if(warnOnError){
                                        NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING, "Please enter a valid decimal");
                                        hasError = true;  // ← Add this
                                    } else {
                                        input.setError("Please enter a valid decimal");
//                                        setErrorIfEmptyRange();
                                        enableErrorState("Please enter a valid decimal");
                                        hasError = true;
                                    }
                                }
                            }
                        }
                    }

//                    if (minValue != null && maxValue != null && input.getText() != null) {
//                        if (!input.getText().toString().equals("") || !input.getText().toString().isEmpty()) {
//                            try{
//                                int valxx = Integer.parseInt(input.getText().toString());
//                                if (valxx >= minValue && valxx <= maxValue) {
//                                    // Valid range
//                                } else if (warnOnError) {
//                                    NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING, "Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
//                                } else {
//                                    input.setError("Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
//                                    setErrorIfEmptyRange();
//                                    enableErrorState("Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
//                                }
//
//                            }catch(NumberFormatException e ){
//                                if(warnOnError){
//                                    NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING, "Number not in provided range! i.e min: " + minValue + " and max: " + maxValue);
//                                } else {
////                                    input.setError("Number not in provided range! i.e min: " + minValue + " and max: 333333333333333" + maxValue);
////                                    setErrorIfEmptyRange();
////                                    enableErrorState("Number not in provided range! i.e min: " + minValue + " and max: 4444444444444444444" + maxValue);
//                                }
//                            }
//
//                        }
//                    }
                } else if (isDecimal && isExpression && isRequired) {

                    if (text.equals("-") || text.startsWith("-")) {
                        input.removeTextChangedListener(this);
                        input.setText(text.replace("-", ""));
                        input.setSelection(input.getText().length());
                        input.addTextChangedListener(this);
                        return;
                    }

                      try {
 
                        if(beforeData.length() > 0 || onChangeData.length() > 0 ) {
                            int beforeDatavalxx = Integer.parseInt(beforeData.toString());
                            int onChangeDatavalxx = Integer.parseInt(onChangeData.toString()); 
                            if (beforeData.length() > 0 && onChangeData.length() == 0) {
                                enableErrorState("Number not in provided range!");
                            }
                        }
                    }catch (NumberFormatException e){
                        if(beforeData.length() > 0 && onChangeData.length() == 0){
  
                            input.setError("Please enter a valid decimal");
                            enableErrorState("Invalid decimal");
                         }else if (beforeData.length() > 0 &&  onChangeData.length() > 0) {
 
                            try {
                                Integer.parseInt(text);
                                // ✅ If parsing works, clear error
                                input.setError(null);
                                disableErrorState();
                            } catch (NumberFormatException eX) {
//                                input.setError("Please enter a valid number 222222222222222");
//                                enableErrorState("Invalid number");
                            }
                        }
                    }
 
                }else if(isDecimal && isExpression && !isRequired){

                    if (text.equals("-") || text.startsWith("-")) {
                        input.removeTextChangedListener(this);
                        input.setText(text.replace("-", ""));
                        input.setSelection(input.getText().length());
                        input.addTextChangedListener(this);
                        return;
                    }

                      try {
 
                        if(beforeData.length() > 0 || onChangeData.length() > 0 ) {
                            int beforeDatavalxx = Integer.parseInt(beforeData.toString());
                            int onChangeDatavalxx = Integer.parseInt(onChangeData.toString());
 
                        }
                    }catch (NumberFormatException e){
                        if (!text.isEmpty()) {
                            try {
                                Integer.parseInt(text);
                                // ✅ If parsing works, clear error
                                input.setError(null);
                                disableErrorState();
                            } catch (NumberFormatException ecc) {
//                                input.setError("Please enter a valid number 3333333333333333333333");
//                                enableErrorState("Invalid number");
                            }
                        }
                    }
                }
            }
        });

        addValueChangedListener(new ValueChangeListener() {


            @Override
            public void onChange(ControlPropertyField field) {
                System.out.println(isLiveValidationDisabled() + " decimal changes isLiveValidationDisabled()----------");
                if (!isLiveValidationDisabled()) {
                    ((ControlDecimalEditField) field).setErrorIfEmptyRange();
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
            ((ControlDecimalEditField) nextView).input.requestFocus();
            ((ControlDecimalEditField) nextView).setCursorToRight();
        }
    }

    @Override
    protected void changeVisualState(VisualState state) {
        System.out.println("VisualStateVisualStateVisualState " + state.toString());
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

    // Data binding, getters & setters
    @BindingAdapter("value")
    public static void setValue(ControlDecimalEditField view, String text) {
        // If text is null or blank, keep it empty
        if (text == null || text.trim().isEmpty() || text == "") {
            view.setFieldValue("");
            return;
        }else{
            try {
                double num = Double.parseDouble(text);
                if (num == Math.floor(num)) {
                    // Whole number, no decimals
                    text = String.valueOf((int) num);
                } else {
                    // Show with 2 decimal places
                    text = String.format("%.2f", num);
                }
            } catch (NumberFormatException e) {
                // If not a number, leave as-is
            }
            view.setFieldValue(text);
            return;

        }
    }


    @BindingAdapter("value")
    public static void setValue(ControlDecimalEditField view, String text, String errorMessage) {
        // If text is null or blank, keep it empty
        if (text == null || text.trim().isEmpty() || text == "") {
            view.setFieldValue("");
            return;
        }else{
            try {
                double num = Double.parseDouble(text);
                if (num == Math.floor(num)) {
                    // Whole number, no decimals
                    text = String.valueOf((int) num);
                } else {
                    // Show with 2 decimal places
                    text = String.format("%.2f", num);
                }
            } catch (NumberFormatException e) {
                // If not a number, leave as-is
            }
            view.setFieldValue(text);
            return;

        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlDecimalEditField view, Integer integerValue) {
        if (integerValue != null) {
            view.setFieldValue(String.valueOf(integerValue));
        } else {
            view.setFieldValue("");
        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlDecimalEditField view, Float floatValue) {
        if (floatValue != null) {
            view.setFieldValue(String.valueOf(floatValue));
        } else {
            view.setFieldValue(null);
        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlDecimalEditField view, Double doubleValue) {
        if (doubleValue != null) {
            view.setFieldValue(String.valueOf(doubleValue));
        } else {
            view.setFieldValue(null);
        }
    }



    public void setDoubleValue(Double doubleValue) {
        setValue(this, doubleValue);
    }

    public void setFloatValue(Float floatValue) {
        setValue(this, floatValue);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static String getValue(ControlDecimalEditField view) {
        return view.getFieldValue();
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Integer getIntegerValue(ControlDecimalEditField view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Integer.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Float getFloatValue(ControlDecimalEditField view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Float.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Double getDoubleValue(ControlDecimalEditField view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Double.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlDecimalEditField view, InverseBindingListener listener) {
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
