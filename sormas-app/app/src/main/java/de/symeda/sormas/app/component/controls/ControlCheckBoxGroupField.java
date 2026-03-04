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
import android.graphics.Color;
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
//	private LinearLayout errorIndicatorsLayout;
	Set<String> selectedElements = new HashSet<>();

	// Add this field to store the pending value
	private Object pendingValue = null;
	private boolean optionsSet = false;
	private Context storedContext;
	protected String hint;
	protected boolean required;
	private boolean isUserInteraction = false;

	private Set<String> validOptionKeys = new HashSet<>();
	// Constructors
	public ControlCheckBoxGroupField(Context context) {
 
		super(context);
		this.storedContext = context;
		// In each constructor, add:
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
			logSelectedElementsChange("initialized in constructor333");
		}
//		this.selectedElements = new HashSet<>();
//		logSelectedElementsChange("reset in constructor");
 	}

	public ControlCheckBoxGroupField(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.storedContext = context;
		// In each constructor, add:
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
			logSelectedElementsChange("initialized in constructor22");
		}
//		this.selectedElements = new HashSet<>();
//		logSelectedElementsChange("reset in setValue(null)");
	}

	public ControlCheckBoxGroupField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.storedContext = context;
		// In each constructor, add:
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
			logSelectedElementsChange("initialized in constructor");
		}
//		this.selectedElements = new HashSet<>();
//		logSelectedElementsChange("reset in setValue(empty)");
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
		removeAllItems();

		// DO NOT clear selectedElements here!
		// We want to preserve selections if this is called after values are set

		validOptionKeys = new HashSet<>();
		validOptionKeys.clear();
		validOptionKeys.addAll(optionsValue.keySet());

		int index = 0;
		for (Map.Entry<String, String> entry : optionsValue.entrySet()) {
			String key = entry.getKey();    // This is the value we want to store
			String value = entry.getValue(); // This is what we show to user


			// Check if this key should be selected based on current selectedElements
			boolean shouldBeChecked = selectedElements != null && selectedElements.contains(key);
			addItemWithPreSelection(key, index, value, shouldBeChecked);
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

	private void addItemWithPreSelection(String key, int index, String displayText, boolean shouldBeChecked) {
		// ALWAYS initialize if null
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
			logSelectedElementsChange("initialized in addItemWithPreSelection");
		}

		final CheckBox checkBox = createCheckBoxWithLayout(key, index, displayText);
		if (dynamicCheckboxesContainer != null && checkBox != null) {
			checkBoxes.put(key, checkBox);

			// Set the checked state immediately
			checkBox.setChecked(shouldBeChecked);

			// Update selectedElements to match
			if (shouldBeChecked) {
				selectedElements.add(key);
				System.out.println("DEBUG addItemWithPreSelection - Selected: " + key);
			} else {
				selectedElements.remove(key);
			}
		}
	}

	private void initializeContainers() {
		if (dynamicCheckboxesContainer == null) {
			dynamicCheckboxesContainer = this.findViewById(R.id.dynamic_checkboxes_container);
		}
		if (groupLabel == null) {
			groupLabel = this.findViewById(R.id.label);
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
			if (suppressListeners) return;

			isUserInteraction = true;
			try {
				// Ensure selectedElements is initialized
				if (selectedElements == null) {
					selectedElements = new HashSet<>();
					logSelectedElementsChange("initialized in checkbox listener");
				}

				if (isChecked) {
					selectedElements.add(key);
					System.out.println("Checkbox '" + key + "' checked, selectedElements: " + selectedElements);
				} else {
					selectedElements.remove(key);
					System.out.println("Checkbox '" + key + "' unchecked, selectedElements: " + selectedElements);
				}
				notifyValueChanged();
			} finally {
				isUserInteraction = false;
			}
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
			if (suppressListeners) return;

			isUserInteraction = true;
			try {
				// Ensure selectedElements is initialized
				if (selectedElements == null) {
					selectedElements = new HashSet<>();
					logSelectedElementsChange("initialized in checkbox listener");
				}

				if (isChecked) {
					selectedElements.add(key);
					System.out.println("Checkbox '" + key + "' checked, selectedElements: " + selectedElements);
				} else {
					selectedElements.remove(key);
					System.out.println("Checkbox '" + key + "' unchecked, selectedElements: " + selectedElements);
				}
				notifyValueChanged();
			} finally {
				isUserInteraction = false;
			}
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

	public void setGroupLabel(String labelText) {
		initializeContainers();
		if (groupLabel != null) {
			groupLabel.setText(labelText);
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
	protected void setFieldValue(Object value) {
		System.out.println("DEBUG setFieldValue called with: " + value + " (Type: " + (value != null ? value.getClass().getName() : "null") + ")");

		// Store as pending value if no checkboxes exist yet
		if (checkBoxes.isEmpty()) {
			pendingValue = value;
			return;
		}
//
//		if (!selectedElements.isEmpty() && valuesAreEqual(value, getFieldValue())) {
//			System.out.println("DEBUG setFieldValue: Value already set, skipping reapplication");
//			return;
//		}

		applyValueToCheckboxes(value);
	}

	private void applyValueToCheckboxes(Object value) {
		suppressListeners = true;
		try {
			List<String> valuesToSet = new ArrayList<>();

			// Parse the value regardless of its format
			if (value instanceof List) {
				List<?> valueList = (List<?>) value;
				for (Object element : valueList) {
					if (element != null) {
						valuesToSet.add(element.toString().trim());
					}
				}
			} else if (value instanceof String) {
				String strValue = ((String) value).trim();
				if (!strValue.isEmpty()) {
					// Handle string format like "[value1, value2, value3]"
					if (strValue.startsWith("[") && strValue.endsWith("]")) {
						strValue = strValue.substring(1, strValue.length() - 1);
					}
					String[] parts = strValue.split("\\s*,\\s*");
					for (String part : parts) {
						if (!part.trim().isEmpty()) {
							valuesToSet.add(part.trim());
						}
					}
				}
			}

			System.out.println("DEBUG applyValueToCheckboxes - Values to set: " + valuesToSet);
			System.out.println("DEBUG applyValueToCheckboxes - Current selectedElements before: " + selectedElements);

			// NEVER clear selectedElements! Just update checkboxes
			// First, uncheck any checkboxes that are NOT in valuesToSet
			for (Map.Entry<Object, CheckBox> entry : checkBoxes.entrySet()) {
				String key = entry.getKey().toString();
				CheckBox checkBox = entry.getValue();

				if (valuesToSet.contains(key)) {
					// This should be checked
					if (!checkBox.isChecked()) {
						checkBox.setChecked(true);
					}
					// Ensure it's in selectedElements
					selectedElements.add(key);
					System.out.println("DEBUG applyValueToCheckboxes - Ensured checked: " + key);
				} else {
					// This should NOT be checked
					if (checkBox.isChecked()) {
						checkBox.setChecked(false);
					}
					// Remove from selectedElements
					selectedElements.remove(key);
					System.out.println("DEBUG applyValueToCheckboxes - Ensured unchecked: " + key);
				}
			}

			// Also handle case where we might need to add keys that aren't in checkboxes yet
			// (though this shouldn't happen if options are set first)
			for (String key : valuesToSet) {
				if (!selectedElements.contains(key)) {
					selectedElements.add(key);
				}
			}

			System.out.println("DEBUG applyValueToCheckboxes - Current selectedElements after: " + selectedElements);
		} finally {
			suppressListeners = false;
		}

		notifyValueChanged();

		if (!selectedElements.isEmpty()) {
			disableErrorState();
		}
	}

	@Override
	protected Object getFieldValue() {
		List<String> selectedList = new ArrayList<>(selectedElements);

		// Filter out invalid values
		if (!validOptionKeys.isEmpty()) {
			selectedList = selectedList.stream()
					.filter(validOptionKeys::contains)
					.collect(Collectors.toList());
		}

		System.out.println("DEBUG getFieldValue returning: " + selectedList);
		return selectedList;
	}

	@Override
	public boolean setErrorIfEmpty() {

		System.out.println("DEBUG ControlCheckBoxGroupField.setErrorIfEmpty() - required: " + isRequired() +
				", enabled: " + isEnabled() + ", selectedElements size: " + getSelectedElementsSize() +
				", field id: " + getId());

		if (!isEnabled()) {
			return false;
		}

		if (!isRequired()) {
			return false;
		}

		// For checkbox groups, check if no checkboxes are selected
		List<String> selectedList = new ArrayList<>(selectedElements);
		if (selectedList.isEmpty()) {
			enableErrorState(R.string.validation_error_required);
			return true;
		}

		return false;
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

	@BindingAdapter("value")
	public static void setValue(ControlCheckBoxGroupField view, Set<?> value) {
		if (value != null) {
			Set<?> list = new HashSet<>();
			view.setFieldValue(list);
		}
	}

	@Override
	protected void changeVisualState(VisualState state) {
		// Handle label color changes
		if (groupLabel != null) {
			int labelColor = getResources().getColor(state.getLabelColor());
			groupLabel.setTextColor(labelColor);
		}

		if (state == VisualState.ERROR) {
			// Make error more visible
			setBackgroundColor(getResources().getColor(android.R.color.holo_red_light) & 0x33FFFFFF);

			// Optionally scroll to this field
			requestFocus();
		} else {
			setBackgroundColor(Color.TRANSPARENT);
		}
	}

	public int getSelectedElementsSize() {
		return selectedElements != null ? selectedElements.size() : 0;
	}

	public Set<String> getSelectedElements() {
		return selectedElements != null ? new HashSet<>(selectedElements) : new HashSet<>();
	}

	private void logSelectedElementsChange(String operation) {
		System.out.println("DEBUG selectedElements " + operation + " - size: " + getSelectedElementsSize() + ", elements: " + getSelectedElements());
	}

	public void setOptionsAndValue(Map<String, String> optionsValue, Object fieldValue) {
		System.out.println("DEBUG setOptionsAndValue called with options: " +
				(optionsValue != null ? optionsValue.size() : 0) +
				", value: " + fieldValue);

		// Initialize selectedElements if null
		if (selectedElements == null) {
			selectedElements = new HashSet<>();
			logSelectedElementsChange("initialized in setOptionsAndValue");
		}

		// Parse the fieldValue
		List<String> valuesToSet = new ArrayList<>();
		if (fieldValue instanceof List) {
			List<?> valueList = (List<?>) fieldValue;
			for (Object element : valueList) {
				if (element != null) {
					valuesToSet.add(element.toString().trim());
				}
			}
		} else if (fieldValue instanceof Set) {
			Set<?> valueSet = (Set<?>) fieldValue;
			for (Object element : valueSet) {
				if (element != null) {
					valuesToSet.add(element.toString().trim());
				}
			}
		} else if (fieldValue != null) {
			// Handle single string value
			String strValue = fieldValue.toString().trim();
			if (!strValue.isEmpty()) {
				if (strValue.startsWith("[") && strValue.endsWith("]")) {
					strValue = strValue.substring(1, strValue.length() - 1);
				}
				String[] parts = strValue.split("\\s*,\\s*");
				for (String part : parts) {
					if (!part.trim().isEmpty()) {
						valuesToSet.add(part.trim());
					}
				}
			}
		}

		System.out.println("DEBUG setOptionsAndValue - Parsed values: " + valuesToSet);

		// Store valid option keys
		validOptionKeys = new HashSet<>();
		if (optionsValue != null) {
			validOptionKeys.addAll(optionsValue.keySet());
		}

		// Initialize containers
		initializeContainers();

		// Clear existing items
		removeAllItems();

		// Clear current selections (but we'll rebuild from valuesToSet)
		selectedElements.clear();

		// Create checkboxes with proper selection
		if (optionsValue != null) {
			int index = 0;
			for (Map.Entry<String, String> entry : optionsValue.entrySet()) {
				String key = entry.getKey();
				String displayText = entry.getValue();

				// Check if this should be selected
				boolean shouldBeChecked = valuesToSet.contains(key);

				// Create checkbox with pre-selection
				addItemWithPreSelection(key, index, displayText, shouldBeChecked);

				// Update selectedElements
				if (shouldBeChecked) {
					selectedElements.add(key);
				}

				index++;
			}
		}

		optionsSet = true;
		pendingValue = null; // Clear any pending value

		System.out.println("DEBUG setOptionsAndValue - Final selectedElements: " + selectedElements);
	}

}