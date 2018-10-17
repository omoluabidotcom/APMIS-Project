package de.symeda.sormas.app.component.controls;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

public class ControlDateField extends ControlPropertyEditField<Date> {

    // Views

    private EditText input;

    // Listeners

    private InverseBindingListener inverseBindingListener;

    // Other fields

    private FragmentManager fragmentManager;
    private SimpleDateFormat dateFormat;

    // Constructors

    public ControlDateField(Context context) {
        super(context);
    }

    public ControlDateField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlDateField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    // Instance methods

    /**
     * Shows a date fragment linked with the value of this field.
     * You need to set the fragment manager with initializeDateField before calling this method.
     */
    private void showDateFragment() {
        if (fragmentManager == null) {
            Log.e(getClass().getName(), "Tried to show date fragment before setting fragment manager");
            return;
        }

        ControlDatePickerFragment fragment = new ControlDatePickerFragment();
        fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                input.setText(DateHelper.formatLocalDate(DateHelper.getDateZero(yy, mm, dd), dateFormat));
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

    private void setUpOnFocusChangeListener() {
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(hasFocus);

                if (hasFocus) {
                    changeVisualState(VisualState.FOCUSED);
                    showDateFragment();
                } else {
                    if (hasError) {
                        changeVisualState(VisualState.ERROR);
                    } else {
                        changeVisualState(VisualState.NORMAL);
                    }
                }
            }
        });
    }

    private void setUpOnClickListener() {
        input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(v.hasFocus());

                if (v.hasFocus()) {
                    showDateFragment();
                }
            }
        });
    }

    public void initializeDateField(final FragmentManager fm) {
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

        return DateHelper.parseDate(input.getText().toString(), dateFormat);
    }

    @Override
    protected void setFieldValue(Date value) {
        if (value == null) {
            input.setText(null);
        } else {
            input.setText(DateHelper.formatLocalDate(value, dateFormat));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    @Override
    public void setHint(String value) {
        input.setHint(value);
    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        dateFormat = DateHelper.getLocalShortDateFormat();
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (isSlim()) {
                inflater.inflate(R.layout.control_date_picker_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_date_picker_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (EditText) this.findViewById(R.id.input);
        input.setInputType(InputType.TYPE_NULL);
        input.setTextAlignment(getTextAlignment());
        if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getGravity());
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });

        setUpOnFocusChangeListener();
        setUpOnClickListener();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getHint() == null) {
            setHint(I18nProperties.getFieldCaption(getFieldCaptionPropertyId()));
        }
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlDateField) nextView).input.requestFocus();
    }

    @Override
    protected void changeVisualState(VisualState state) {
        if (getUserEditRight() != null && !ConfigProvider.getUser().hasUserRight(getUserEditRight())) {
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
    public static void setValue(ControlDateField view, Date date) {
        view.setFieldValue(date);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Date getValue(ControlDateField view) {
        return view.getFieldValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlDateField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter("dateFormat")
    public static void setDateFormat(ControlDateField field, SimpleDateFormat dateFormat) {
        field.dateFormat = dateFormat;
    }

}