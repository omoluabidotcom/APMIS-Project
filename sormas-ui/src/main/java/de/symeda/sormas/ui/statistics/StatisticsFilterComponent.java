package de.symeda.sormas.ui.statistics;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseAttributeGroup;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsFilterComponent extends VerticalLayout {

	private static final String SPECIFY_YOUR_SELECTION = "Specify your selection";

	private StatisticsCaseAttribute selectedAttribute;
	private StatisticsCaseSubAttribute selectedSubAttribute;
	private StatisticsFilterElement filterElement;

	public StatisticsFilterComponent() {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		addComponent(createFilterAttributeElement());
	}

	private HorizontalLayout createFilterAttributeElement() {
		HorizontalLayout filterAttributeLayout = new HorizontalLayout();
		filterAttributeLayout.setSpacing(true);
		filterAttributeLayout.setWidth(100, Unit.PERCENTAGE);

		MenuBar filterAttributeDropdown = new MenuBar();
		filterAttributeDropdown.setCaption("Attribute");
		MenuItem filterAttributeItem = filterAttributeDropdown.addItem("Select an attribute", null);
		MenuBar filterSubAttributeDropdown = new MenuBar();
		filterSubAttributeDropdown.setCaption("Attribute specification");
		MenuItem filterSubAttributeItem = filterSubAttributeDropdown.addItem(SPECIFY_YOUR_SELECTION, null);

		// Add attribute groups
		for (StatisticsCaseAttributeGroup attributeGroup : StatisticsCaseAttributeGroup.values()) {
			MenuItem attributeGroupItem = filterAttributeItem.addItem(attributeGroup.toString(), null);
			attributeGroupItem.setEnabled(false);

			// Add attributes belonging to the current group
			for (StatisticsCaseAttribute attribute : attributeGroup.getAttributes()) {
				Command attributeCommand = selectedItem -> {
					selectedAttribute = attribute;
					selectedSubAttribute = null;
					filterAttributeItem.setText(attribute.toString());
					
					// Add style to keep chosen item selected and remove it from all other items
					for (MenuItem menuItem : filterAttributeItem.getChildren()) {
						menuItem.setStyleName("");
					}
					selectedItem.setStyleName("selected-filter");
					
					// Reset the sub attribute dropdown
					filterSubAttributeItem.removeChildren();
					filterSubAttributeItem.setText(SPECIFY_YOUR_SELECTION);

					if (attribute.getSubAttributes().length > 0) {
						for (StatisticsCaseSubAttribute subAttribute : attribute.getSubAttributes()) {
							if (subAttribute.isUsedForFilters()) {
								Command subAttributeCommand = selectedSubItem -> {
									selectedSubAttribute = subAttribute;
									filterSubAttributeItem.setText(subAttribute.toString());
									
									// Add style to keep chosen item selected and remove it from all other items
									for (MenuItem menuItem : filterSubAttributeItem.getChildren()) {
										menuItem.setStyleName("");
									}
									selectedSubItem.setStyleName("selected-filter");
									
									updateFilterElement();
								};

								filterSubAttributeItem.addItem(subAttribute.toString(), subAttributeCommand);
							}
						}

						// Only add the sub attribute dropdown if there are any sub attributes that are relevant for the filters section
						if (filterSubAttributeItem.getChildren() != null && filterSubAttributeItem.getChildren().size() > 0) {
							filterAttributeLayout.addComponent(filterSubAttributeDropdown);
							filterAttributeLayout.setExpandRatio(filterSubAttributeDropdown, 1);
						} else {
							filterAttributeLayout.removeComponent(filterSubAttributeDropdown);
						}
					} else {
						filterAttributeLayout.removeComponent(filterSubAttributeDropdown);
					}
					updateFilterElement();
				};

				filterAttributeItem.addItem(attribute.toString(), attributeCommand);
			}
		}

		filterAttributeLayout.addComponent(filterAttributeDropdown);
		filterAttributeLayout.setExpandRatio(filterAttributeDropdown, 0);
		return filterAttributeLayout;
	}

	private void updateFilterElement() {
		
		if (filterElement != null) {
			removeComponent(filterElement);
			filterElement = null;
		}
		
		if (selectedSubAttribute == StatisticsCaseSubAttribute.DATE_RANGE) {
			filterElement = new StatisticsFilterDateRangeElement();
		} else if (selectedAttribute == StatisticsCaseAttribute.REGION_DISTRICT) {
			filterElement = new StatisticsFilterRegionDistrictElement();
		} else if (selectedAttribute.getSubAttributes().length == 0 
				|| selectedSubAttribute != null) {
			filterElement = new StatisticsFilterValuesElement(
					selectedAttribute.toString() + (selectedSubAttribute != null ? " (" + selectedSubAttribute.toString() + ")" : ""), 
					selectedAttribute, selectedSubAttribute);
		}

		if (filterElement != null) {
			addComponent(filterElement);
		}
	}

	public StatisticsCaseAttribute getSelectedAttribute() {
		return selectedAttribute;
	}

	public StatisticsCaseSubAttribute getSelectedSubAttribute() {
		return selectedSubAttribute;
	}

	public StatisticsFilterElement getFilterElement() {
		return filterElement;
	}
}