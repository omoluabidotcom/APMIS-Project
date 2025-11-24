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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.util.DataUtils;

public class ControlCheckBoxGroupField extends ControlPropertyEditField<Object> {
	private Map<Object, CheckBox> checkBoxes = new HashMap<>();
	private InverseBindingListener inverseBindingListener;
	private Class<? extends Enum> enumClass = null;

	// New layout components for vertical structure
	private LinearLayout dynamicCheckboxesContainer;
	private TextView groupLabel;
	private LinearLayout errorIndicatorsLayout;
	Set<String> selectedElements = new HashSet<>();

	// Add this field to store the pending value
	private Object pendingValue = null;
	private boolean optionsSet = false;
private Context storedContext;
	private static Map<String, String> optionvaluex = new HashMap<>();


	// Constructors
	public ControlCheckBoxGroupField(Context context) {
 
		super(context);
		this.storedContext = context;
		this.selectedElements = new HashSet<>();
 	}

	public ControlCheckBoxGroupField(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.storedContext = context;
		this.selectedElements = new HashSet<>();
	}

	public ControlCheckBoxGroupField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.storedContext = context;
		this.selectedElements = new HashSet<>();
	}

	public <T extends Enum> void setEnumClass(Class<T> c) {
		if (!DataHelper.equal(c, enumClass)) {
			suppressListeners = true;

			// Ensure container is initialized
			initializeContainers();

			// Clear existing items first
			removeAllItems();

			// Get enum items using the existing utility
			List<Item> items = DataUtils.getEnumItems(c, false);

			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				// Use the enum value as key, display text as value (same pattern as setOptions)
 
				System.out.println( item.getValue().toString() + "1455555item.getValue().toString()  " + item.getKey());
				addItem(item.getValue().toString(), i, item.getKey());
			}

			enumClass = c;
			suppressListeners = false;
		}
	}


	public void setOptions(Map<String, String> optionsValue) {
		initializeContainers();
		// Clear existing items first
		removeAllItems();

		int index = 0;
		for (Map.Entry<String, String> entry : optionsValue.entrySet()) {
			String key = entry.getKey();    // This is the value we want to store
			String value = entry.getValue(); // This is what we show to user
			addItem(key, index, value);
			index++;
		}
		optionsSet = true;

		// Apply any pending value now that checkboxes exist
		if (pendingValue != null) {
			System.out.println("Applying pending value after options set: " + pendingValue);
			applyValueToCheckboxes(pendingValue);
			pendingValue = null;
		}
	}

	public void setOptionsAndValue(Map<String, String> optionsValue, Object fieldValue) {
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
		}
		initializeContainers();
		removeAllItems();
		int index = 0;
		for (Map.Entry<String, String> entry : optionsValue.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			addItemAndSetValue(key, index, value, fieldValue);
			index++;
		}
		optionsSet = true;
		if (pendingValue != null) {
			System.out.println("Applying pending value after options set: " + pendingValue);
			applyValueToCheckboxes(pendingValue);
			pendingValue = null;
		}
	}

	private void initializeContainers() {
		if (dynamicCheckboxesContainer == null) {
			dynamicCheckboxesContainer = this.findViewById(R.id.dynamic_checkboxes_container);
		}
		if (groupLabel == null) {
			groupLabel = this.findViewById(R.id.label);
		}
		if (errorIndicatorsLayout == null) {
			View labelFrame = this.findViewById(R.id.label_frame);
			if (labelFrame instanceof LinearLayout) {
				LinearLayout parentLayout = (LinearLayout) labelFrame;
				for (int i = 0; i < parentLayout.getChildCount(); i++) {
					View child = parentLayout.getChildAt(i);
					if (child instanceof LinearLayout) {
						LinearLayout childLayout = (LinearLayout) child;
						if (childLayout.findViewById(R.id.required_indicator) != null) {
							errorIndicatorsLayout = childLayout;
							break;
						}
					}
				}
			}
		}

		if (checkBoxes == null) {
			checkBoxes = new HashMap<>();
		}
	}

	private void addItem(String key, int index, String displayText) {
		final CheckBox checkBox = createCheckBoxWithLayout(key, index, displayText);
		if (dynamicCheckboxesContainer != null && checkBox != null) {
			checkBoxes.put(key, checkBox);
		}
	}

	private void addItemAndSetValue(String key, int index, String displayText, Object value) {
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
		}

		final CheckBox checkBox = createCheckBoxWithLayout(key, index, displayText);
		if (dynamicCheckboxesContainer != null && checkBox != null) {
			try {
				checkBoxes.put(key, checkBox);
			} finally {
				if (value instanceof List) {
					List<?> valueList = (List<?>) value;
					for (Object element : valueList) {
						if (element instanceof String) {
							String str = ((String) element).trim();
							if (str.startsWith("[") && str.endsWith("]")) {
								str = str.substring(1, str.length() - 1);
							}

							String[] parts = str.split("\\s*,\\s*");

							for (String part : parts) {
								if (part.equals(key)) {
									checkBox.setChecked(true);
									selectedElements.add(key);
								}
							}
						}
					}
				}
			}
		}
	}

	private CheckBox createCheckBoxWithLayout(String key, int index, String displayText) {
		// Inflate individual checkbox item layout
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View checkboxItemView = inflater.inflate(R.layout.item_checkboxes, null);

		CheckBox checkBox = checkboxItemView.findViewById(R.id.checkbox);
		TextView caption = checkboxItemView.findViewById(R.id.checkbox_caption);

		if (checkBox == null || caption == null) {
			// Fallback: create inline layout if item_checkbox.xml is not available
			return createInlineCheckboxLayout(key, index, displayText);
		}

		// Configure checkbox
		int viewId = View.generateViewId();
		checkBox.setId(viewId);
		checkBox.setTag(key);
		checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

		// Configure caption
		caption.setText(displayText);
		caption.setOnClickListener(v -> {
			if (checkBox.isEnabled()) {
				checkBox.toggle();
			}
		});

		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (selectedElements == null) {
				selectedElements = new HashSet<>(); // Safety check
			}

			if (isChecked) {
				selectedElements.add(key);
			} else {
				selectedElements.remove(key);
			}
			System.out.println("Checkbox '" + key + "' is now: " + isChecked);
			notifyValueChanged();
		});

		if (dynamicCheckboxesContainer != null) {
			dynamicCheckboxesContainer.addView(checkboxItemView);
		}

		return checkBox;
	}

	private CheckBox createInlineCheckboxLayout(String key, int index, String displayText) {
		LinearLayout itemLayout = new LinearLayout(getContext());
		itemLayout.setOrientation(LinearLayout.HORIZONTAL);
		itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		// Create checkbox
		CheckBox checkBox = new CheckBox(new ContextThemeWrapper(getContext(), R.style.ControlCheckboxStyle));
		int viewId = View.generateViewId();
		checkBox.setId(viewId);
		checkBox.setTag(key);
		checkBox.setText(displayText);
		checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (selectedElements == null) {
				selectedElements = new HashSet<>();
			}

			if (isChecked) {
				selectedElements.add(key);
			} else {
				selectedElements.remove(key);
			}
			System.out.println("Checkbox '" + key + "' is now: " + isChecked);
			notifyValueChanged();
		});
		itemLayout.addView(checkBox);

		if (dynamicCheckboxesContainer != null) {
			dynamicCheckboxesContainer.addView(itemLayout);
		}
		return checkBox;
	}

	private void notifyValueChanged() {
		if (!suppressListeners) {
			onValueChanged();
			if (inverseBindingListener != null) {
				inverseBindingListener.onChange();
			}
		}
	}

	private void uncheckAll() {
		for (CheckBox checkBox : checkBoxes.values()) {
			checkBox.setChecked(false);
		}
	}

	private void removeAllItems() {
		checkBoxes.clear();
		if (dynamicCheckboxesContainer != null) {
			dynamicCheckboxesContainer.removeAllViews();
		}
	}

	public void removeItem(Object itemId) {
		CheckBox checkBox = checkBoxes.get(itemId);
		if (checkBox != null && dynamicCheckboxesContainer != null) {
			// Find and remove the parent view that contains this checkbox
			View parentView = (View) checkBox.getParent();
			if (parentView != null) {
				dynamicCheckboxesContainer.removeView(parentView);
			}
			checkBoxes.remove(itemId);
		}
	}

	public void setGroupLabel(String labelText) {
		initializeContainers();
		if (groupLabel != null) {
			groupLabel.setText(labelText);
		}
	}

	public void showError(String errorMessage) {
		initializeContainers();
		if (errorIndicatorsLayout != null) {
			TextView errorIndicator = errorIndicatorsLayout.findViewById(R.id.error_indicator);
			if (errorIndicator != null) {
				errorIndicator.setText(errorMessage);
				errorIndicator.setVisibility(View.VISIBLE);
				errorIndicatorsLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	public void hideErrors() {
		if (errorIndicatorsLayout != null) {
			errorIndicatorsLayout.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		// Nothing to initialize
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater != null) {
			// Use the new vertical layout instead of the old one
			inflater.inflate(R.layout.control_checkboxgroup_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initializeContainers();
	}

	@Override
	protected void setFieldValue( Object value) {
		pendingValue = value;
		checkBoxes.put(value, new CheckBox(storedContext));
		if (checkBoxes.isEmpty()) {
			pendingValue = value;
			return;
		}
		applyValueToCheckboxes(value);
	}

	private void applyValueToCheckboxes(Object value) {
		suppressListeners = true;
		try {
			selectedElements.clear();
			uncheckAll();

			if (value instanceof List) {
				List<?> valueList = (List<?>) value;
				for (Object element : valueList) {
					if (element instanceof List) {
						// Handle nested lists
						for (Object inner : (List<?>) element) {
							handleElement(inner);
						}
					} else if (element instanceof String) {
						// Clean up string values like "[NA, No_assistance, Community_mobilization]"
						String str = ((String) element).trim();
						// Remove brackets if present
						if (str.startsWith("[") && str.endsWith("]")) {
							str = str.substring(1, str.length() - 1);
						}
						// Split by comma
						String[] parts = str.split("\\s*,\\s*"); // trims whitespace around commas

						for (String part : parts) {
							System.out.println(part + "  extracted value");
							handleElement(part);
						}
					} else {
						handleElement(element);
					}
				}
			} else if (value != null) {
				handleElement(value);
			}
		} finally {
			suppressListeners = false;
		}
	}

	private void handleElement(Object element) {
		if (element == null) {
			return;
		}

		String key = element.toString().trim();

		CheckBox checkBox = checkBoxes.get(key);
		if (checkBox != null) {
			checkBox.setChecked(true);
			selectedElements.add(key);
			System.out.println("Successfully checked checkbox for key: " + key);
		} else {
			System.out.println("WARNING: No checkbox found for key: '" + key + "'");
			System.out.println("Available keys: " + checkBoxes.keySet());
		}
	}


	@Override
	protected Object getFieldValue() {
		List<String> selectedList = new ArrayList<>(selectedElements);
		System.out.println("getFieldValue() returning: " + selectedList);
		return selectedList;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (CheckBox checkBox : checkBoxes.values()) {
			checkBox.setEnabled(enabled);
		}
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		// Not needed
	}

	@Override
	public void setHint(String hint) {
		// Not needed
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Set<?> getValue(ControlCheckBoxGroupField view) {
		Object value = view.getFieldValue();
		if (value instanceof List) {
			// Safe cast
			return (Set<?>) value;
		}
		return new HashSet<>();
	}


	@BindingAdapter("valueAttrChanged")
	public static void setListener(final ControlCheckBoxGroupField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	@BindingAdapter(value = {
			"value",
			"enumClass" })
	public static void setValue(ControlCheckBoxGroupField view, Object value, Class enumClass) {
		System.out.println("BindingAdapter - enumClass: " + enumClass);
		if (enumClass != null) {
			view.setEnumClass((Class<? extends Enum>) enumClass);
		}
		System.out.println("BindingAdapter - value: " + value);

		List<String> flattened = new ArrayList<>();

		if (value instanceof List) {
			for (Object element : (List<?>) value) {
				if (element instanceof List) {
					for (Object inner : (List<?>) element) {
						flattened.add(inner.toString());
					}
				} else if (element != null) {
					flattened.add(element.toString());
				}
			}
		} else if (value != null) {
			flattened.add(value.toString());
		}

		view.setFieldValue(flattened);
	}



	@BindingAdapter(value = {"value"})
	public static void setValue(ControlCheckBoxGroupField view, Object value) {
		System.out.println("BindingAdapter - valuebbb: " + value);
		view.setFieldValue( value);
	}

	@BindingAdapter("value")
	public static void setValue(ControlCheckBoxGroupField view, Set<?> value) {
		if (value != null) {
			Set<?> list = new HashSet<>();
			view.setFieldValue(list);
		}
	}

	@Override
	protected void changeVisualState(VisualState state) {
		// TODO: Implement error state changes
		switch (state) {
			case ERROR:
				// Show error indicators
				if (errorIndicatorsLayout != null) {
					errorIndicatorsLayout.setVisibility(View.VISIBLE);
				}
				break;
			case NORMAL:
				hideErrors();
				break;
			default:
				break;
		}
	}
}