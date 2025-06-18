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
import android.text.TextWatcher;
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

import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.campaign.edit.CountryDetails;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class ControlPhoneField extends ControlPropertyEditField<String> {

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

    private Map<String, CountryDetails> mapvalue = new HashMap<>();

    // Constructors

    public ControlPhoneField(Context context) {
        super(context);
        addMapValue();
    }

    public ControlPhoneField(Context context, AttributeSet attrs) {
        super(context, attrs);
        addMapValue();
    }

    public ControlPhoneField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addMapValue();
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
                            if (nextView instanceof ControlPhoneField) {
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
                inputType = a.getInt(R.styleable.ControlTextEditField_inputType, InputType.TYPE_CLASS_TEXT);
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

        initInput(false, false, false, null, null, false, false);
    }

    protected void initInput(boolean isIntegerFlag, boolean isRequired, boolean isRange, Integer minValue, Integer maxValue, Boolean isExpression, Boolean warnOnError) {

        input = (EditText) this.findViewById(R.id.text_input);
        //if (getImeOptions() == EditorInfo.IME_NULL) {
        //	setImeOptions(EditorInfo.IME_ACTION_DONE);
        //}
        //	input.setImeOptions(getImeOptions());
        //input.setImeActionLabel(null, getImeOptions());
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
/*
		if (getMinLength() >= 0) {
			input.setFilters(
					new InputFilter[] {
							new InputFilter.LengthFilter(getMinLength()) });
		}
*/

        CharSequence valx = input.getText();
        if (valx == null && required) {
            //  setSoftRequired(true);

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

                /*if (isRange && isExpression && isRequired){
                    System.out.println((onChangeData.length() == 0) +" =XXXXXXXXXXX ENTERSSSSS XXXXXXXX =" +(beforeData.length() > 0));
                    if(beforeData.length() > 0 && onChangeData.length() == 0) {
                        enableErrorState("Number not in provided range!");
                        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx");

                    }*/
                //  }
            }

            @Override
            public void afterTextChanged(Editable editablex) {

                System.out.println("yeboooooooooooooooooooooooooooooooooo "+editablex.toString());

                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
                String valuesHolder = "";
                String validatingHolder = "";
                int max = 0;
                int min = 0;
//                Integer validatingHolderInt = 0;

                for (Map.Entry<String, CountryDetails> mapEachValues: mapvalue.entrySet()) {
                    if(editablex.toString().startsWith(mapEachValues.getValue().getDialCode())) {
                        max = mapEachValues.getValue().getMaxLength();
                        min = mapEachValues.getValue().getMinLength();
                        valuesHolder = mapEachValues.getValue().getDialCode();
                        validatingHolder = editablex.toString().replace(mapEachValues.getValue().getDialCode(), "");
                        System.out.println("valuesHoldervaluesHoldervaluesHolder " + valuesHolder + " replacersss " + editablex.toString().replace(mapEachValues.getValue().getDialCode(), ""));
                        break;
                    }
                }

                if(validatingHolder.length() > max || validatingHolder.length() < min) {
//                    input.setError("Mobile Number cannot be less than " + min + " or greater than " + max);
                    setErrorIfEmptyRange();
                    enableErrorState("Mobile Number cannot be less than " + min + " or greater than " + max);
//                    NotificationHelper.showNotification((NotificationContext) input.getContext(), WARNING,
//                            "Mobile Number cannot be less than " + min + " or greater than " + max);
                } else {
                    System.out.println("tytytytytytytytytytytytyt");
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
            ((ControlPhoneField) nextView).input.requestFocus();
            ((ControlPhoneField) nextView).setCursorToRight();
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

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlPhoneField view, String text) {
        view.setFieldValue(text);
    }

/*
    @BindingAdapter("value")
    public static void setValue(ControlPhoneField view, String text, Boolean hasErrorNow) {
        view.setFieldValue(text);

        if (hasErrorNow) {
            changeVisualState(VisualState.ERROR);
        } else {
            changeVisualState(VisualState.NORMAL);
        }
    }
*/

    @BindingAdapter("value")
    public static void setValue(ControlPhoneField view, Integer integerValue) {
        if (integerValue != null) {
            view.setFieldValue(String.valueOf(integerValue));
        } else {
            view.setFieldValue(null);
        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlPhoneField view, Float floatValue) {
        if (floatValue != null) {
            view.setFieldValue(String.valueOf(floatValue));
        } else {
            view.setFieldValue(null);
        }
    }

    @BindingAdapter("value")
    public static void setValue(ControlPhoneField view, Double doubleValue) {
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
    public static String getValue(ControlPhoneField view) {
        return view.getFieldValue();
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Integer getIntegerValue(ControlPhoneField view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Integer.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Float getFloatValue(ControlPhoneField view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Float.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Double getDoubleValue(ControlPhoneField view) {
        if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
            return Double.valueOf(view.getFieldValue());
        } else {
            return null;
        }
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlPhoneField view, InverseBindingListener listener) {
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

    public void addMapValue() {

        mapvalue.put("Afghanistan", new CountryDetails("+93", 9, 9));
        mapvalue.put("Albania", new CountryDetails("+355", 8, 9));
        mapvalue.put("Algeria", new CountryDetails("+213", 9, 9));
        mapvalue.put("Andorra", new CountryDetails("+376", 6, 6));
        mapvalue.put("Angola", new CountryDetails("+244", 9, 9));
        mapvalue.put("Argentina", new CountryDetails("+54", 10, 10));
        mapvalue.put("Armenia", new CountryDetails("+374", 8, 8));
        mapvalue.put("Australia", new CountryDetails("+61", 9, 9));
        mapvalue.put("Austria", new CountryDetails("+43", 10, 13));
        mapvalue.put("Azerbaijan", new CountryDetails("+994", 9, 9));
        mapvalue.put("Bahrain", new CountryDetails("+973", 8, 8));
        mapvalue.put("Bangladesh", new CountryDetails("+880", 10, 10));
        mapvalue.put("Belarus", new CountryDetails("+375", 9, 9));
        mapvalue.put("Belgium", new CountryDetails("+32", 8, 9));
        mapvalue.put("Bolivia", new CountryDetails("+591", 8, 8));
        mapvalue.put("Brazil", new CountryDetails("+55", 10, 11));
        mapvalue.put("Canada", new CountryDetails("+1", 10, 10));
        mapvalue.put("China", new CountryDetails("+86", 11, 11));
        mapvalue.put("Colombia", new CountryDetails("+57", 10, 10));
        mapvalue.put("Denmark", new CountryDetails("+45", 8, 8));
        mapvalue.put("Egypt", new CountryDetails("+20", 10, 10));
        mapvalue.put("France", new CountryDetails("+33", 9, 9));
        mapvalue.put("Germany", new CountryDetails("+49", 10, 11));
        mapvalue.put("India", new CountryDetails("+91", 10, 10));
        mapvalue.put("Indonesia", new CountryDetails("+62", 9, 11));
        mapvalue.put("Iran", new CountryDetails("+98", 10, 10));
        mapvalue.put("Iraq", new CountryDetails("+964", 10, 10));
        mapvalue.put("Italy", new CountryDetails("+39", 9, 10));
        mapvalue.put("Japan", new CountryDetails("+81", 10, 10));
        mapvalue.put("Kenya", new CountryDetails("+254", 9, 9));
        mapvalue.put("Mexico", new CountryDetails("+52", 10, 10));
        mapvalue.put("Netherlands", new CountryDetails("+31", 9, 9));
        mapvalue.put("Nigeria", new CountryDetails("+234", 7, 10));
        mapvalue.put("Pakistan", new CountryDetails("+92", 10, 10));
        mapvalue.put("Philippines", new CountryDetails("+63", 10, 10));
        mapvalue.put("Poland", new CountryDetails("+48", 9, 9));
        mapvalue.put("Portugal", new CountryDetails("+351", 9, 9));
        mapvalue.put("Russia", new CountryDetails("+7", 10, 10));
        mapvalue.put("Saudi Arabia", new CountryDetails("+966", 9, 9));
        mapvalue.put("South Africa", new CountryDetails("+27", 9, 9));
        mapvalue.put("South Korea", new CountryDetails("+82", 9, 10));
        mapvalue.put("Spain", new CountryDetails("+34", 9, 9));
        mapvalue.put("Sweden", new CountryDetails("+46", 7, 9));
        mapvalue.put("Switzerland", new CountryDetails("+41", 9, 9));
        mapvalue.put("Thailand", new CountryDetails("+66", 9, 9));
        mapvalue.put("Turkey", new CountryDetails("+90", 10, 10));
        mapvalue.put("Ukraine", new CountryDetails("+380", 9, 9));
        mapvalue.put("United Arab Emirates", new CountryDetails("+971", 9, 9));
        mapvalue.put("United Kingdom", new CountryDetails("+44", 9, 10));
        mapvalue.put("United States", new CountryDetails("+1", 10, 10));
        mapvalue.put("Vietnam", new CountryDetails("+84", 9, 10));
    }
}
