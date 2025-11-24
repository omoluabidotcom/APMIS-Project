package com.cinoteck.application.views.reports;

 
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
 
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

@Route(layout = ReportView.class) 
public class CompletionAnalysisTabsheet extends VerticalLayout implements RouterLayout {

    private static final long serialVersionUID = 1L;

    private final Map<Tab, Supplier<Component>> tabSupplierMap = new LinkedHashMap<>();
    
    private final Map<Tab, Component> loadedComponents = new LinkedHashMap<>();

    private Tabs createTabs() {

        tabSupplierMap.put(
            new Tab(I18nProperties.getCaption(Captions.dataCompleteness)),
            () -> new CompletionAnalysisView()
        );

        tabSupplierMap.put(
            new Tab(I18nProperties.getCaption(Captions.adminDataCompleteness)),
            () -> new AdminCompletionAnalysisView()
        );

        tabSupplierMap.put(
            new Tab("FLW Operation Report"),
            () -> new FlwErrorAnalysisView()
        );

        Tabs tabs = new Tabs(tabSupplierMap.keySet().toArray(new Tab[0]));

        tabSupplierMap.keySet().forEach(tab -> {
            tab.getElement().getStyle().set("color", "green");
        });

        return tabs;
    }

    private Component getOrCreateComponent(Tab tab) {
        if (loadedComponents.containsKey(tab)) {
            return loadedComponents.get(tab);
        }

        Supplier<Component> supplier = tabSupplierMap.get(tab);
        if (supplier != null) {
            Component component = supplier.get();
            loadedComponents.put(tab, component);           
            return component;
        }

        return null;
    }

    public CompletionAnalysisTabsheet() {
        setSizeFull();

        HorizontalLayout reportTabsheetLayout = new HorizontalLayout();
        reportTabsheetLayout.setClassName("campDatFill");

        Tabs tabs = createTabs();
        tabs.setSizeFull();
        tabs.getStyle().set("background", "rgba(255, 255, 255, 0)");

        Div contentContainer = new Div();
        contentContainer.setSizeFull();

        tabs.addSelectedChangeListener(e -> {
            contentContainer.removeAll();
            Component component = getOrCreateComponent(e.getSelectedTab());
            if (component != null) {
                contentContainer.add(component);
            }
        });

        Tab initialTab = tabs.getSelectedTab();
        if (initialTab != null) {
            Component initialComponent = getOrCreateComponent(initialTab);
            if (initialComponent != null) {
                contentContainer.add(initialComponent);
            }
        }

        reportTabsheetLayout.add(tabs);
        add(reportTabsheetLayout, contentContainer);
    }
}
 
