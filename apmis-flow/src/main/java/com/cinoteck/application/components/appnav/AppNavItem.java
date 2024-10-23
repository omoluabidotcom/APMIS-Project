package com.cinoteck.application.components.appnav;

import com.cinoteck.application.utils.authentication.AccessControl;
import com.cinoteck.application.utils.authentication.AccessControlFactory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.StateTree;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.VaadinService;
import java.util.Optional;

/**
 * A menu item for the {@link AppNav} component.
 * <p>
 * Can contain a label and/or an icon and links to a given {@code path}.
 */
@JsModule("@vaadin-component-factory/vcf-nav")
@Tag("vcf-nav-item")
@CssImport(value = "./themes/apmis-theme/components/vcf-nav.css", themeFor = "vcf-nav-item")
public class AppNavItem extends Component {
	private Button buttonComponent;

	/**
	 * Creates a menu item which does not link to any view but only shows the given
	 * label.
	 * 
	 * @param label the label for the item
	 */
	public AppNavItem(String label) {
		setLabel(label);
	}

	/**
	 * Creates a new menu item using the given label that links to the given path.
	 * 
	 * @param label the label for the item
	 * @param path  the path to link to
	 */
	public AppNavItem(String label, String path) {
		setPath(path);
		setLabel(label);
	}

	/**
	 * Creates a new menu item using the given label that links to the given view.
	 * 
	 * @param label the label for the item
	 * @param view  the view to link to
	 */
	public AppNavItem(String label, Class<? extends Component> view) {
		setPath(view);
		setLabel(label);
	}

	/**
	 * Creates a new menu item using the given label and icon that links to the
	 * given path.
	 * 
	 * @param label the label for the item
	 * @param path  the path to link to
	 * @param icon  the icon for the item
	 */
	public AppNavItem(String label, String path, Icon icon) {
		setPath(path);
		setLabel(label);
		setIcon(icon);
	}

	/**
	 * Creates a new menu item using the given label that links to the given view.
	 * 
	 * @param label the label for the item
	 * @param view  the view to link to
	 * @param icon  the icon for the item
	 */
	public AppNavItem(String label, Class<? extends Component> view, Icon icon) {
		setPath(view);
		setLabel(label);
		setIcon(icon);
	}

	/**
	 * Creates a new menu item using the given label and icon that links to the
	 * given path.
	 * 
	 * @param label     the label for the item
	 * @param path      the path to link to
	 * @param iconClass the CSS class to use for showing the icon
	 */

	// This APp Nav would work for having a subdomain under apmis - segun
	public AppNavItem(String label, String path, VaadinIcon iconClass, String style) {
		setPath(path);
		setLabel(label);
		setIcon(iconClass.create());
		setId(style);
	}

	/**
	 * Creates a new menu item using the given label and icon that links to the
	 * given url in a new tab .
	 * 
	 * @param label     the label for the item
	 * @param path      the url to be opened in a new tab to link to
	 * @param iconClass the CSS class to use for showing the icon ##Segun
	 */
	// This APp Nav would work for opening the url in another tab - segun
	public AppNavItem(String label, VaadinIcon iconClass, String path, String style) {

		setLabel(label);
		setIcon(iconClass.create());
		setId(style);
	
		// If the path is a URL, use JavaScript to open it in a new tab
		if (path.startsWith("http://") || path.startsWith("https://")) {

//    		final AccessControl accessControl = AccessControlFactory.getInstance().createAccessControl();
//    		accessControl.isUserSignedIn();
			getElement().addEventListener("click", e -> {
				getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", path));
				
				UI.getCurrent().getPage().reload();
			});
		} // else {
		
		// }
	}

	/**
	 * Creates a new menu item using the given label and icon that links to the
	 * given path.
	 * 
	 * @param label     the label for the item
	 * @param view      the view to link to
	 * @param iconClass the CSS class to use for showing the icon
	 */
	public AppNavItem(String label, Class<? extends Component> view, VaadinIcon iconClass, String style) {
		setPath(view);
		setLabel(label);

		setIcon(iconClass.create());
		setId(style);
	}

	/**
	 * Creates a new menu item using the given label and icon that links to the
	 * given path. and a buttin to initialize a clicklistener
	 * 
	 * @param label     the label for the item
	 * @param view      the view to link to
	 * @param iconClass the CSS class to use for showing the icon
	 */
	
	public AppNavItem(String label, VaadinIcon iconClass, String style, Button button,
			Class<? extends Component> view) {

		setLabel(label);
		setIcon(iconClass.create());
		setId(style);
		setButton(button);
		setPath(view);
	}

	public AppNavItem(String label, VaadinIcon iconClass, String style, Button button,
			Class<? extends Component> view, String classname) {

		setLabel(label);
		setIcon(iconClass.create());
		setId(style);
		setButton(button);
		setPath(view);
		setId(classname);
	}

	/**
	 * Adds menu item(s) inside this item, creating a hierarchy.
	 * 
	 * @param appNavItems the menu item(s) to add
	 * @return this item for chaining
	 */
	public AppNavItem addItem(AppNavItem... appNavItems) {
		for (AppNavItem appNavItem : appNavItems) {
			appNavItem.getElement().setAttribute("slot", "children");
			getElement().appendChild(appNavItem.getElement());

		}

		return this;
	}

	/**
	 * Removes the given menu item from this item.
	 * <p>
	 * If the given menu item is not a child of this menu item, does nothing.
	 * 
	 * @param appNavItem the menu item to remove
	 * @return this item for chaining
	 */
	public AppNavItem removeItem(AppNavItem appNavItem) {
		Optional<Component> parent = appNavItem.getParent();
		if (parent.isPresent() && parent.get() == this) {
			getElement().removeChild(appNavItem.getElement());
		}

		return this;
	}

	/**
	 * Removes all menu items from this item.
	 * 
	 * @return this item for chaining
	 */
	public AppNavItem removeAllItems() {
		getElement().removeAllChildren();
		return this;
	}

	/**
	 * Gets the label for the item.
	 * 
	 * @return the label or null if no label has been set
	 */
	public String getLabel() {
		return getExistingLabelElement().map(e -> e.getText()).orElse(null);
	}

	/**
	 * Set a textual label for the item.
	 * <p>
	 * The label is also available for screen rader users.
	 * 
	 * @param label the label to set
	 * @return this instance for chaining
	 */
	public AppNavItem setLabel(String label) {
		getLabelElement().setText(label);
		return this;
	}

	public AppNavItem setButton(Button button) {
		this.buttonComponent = button;
		return this;
	}

	private Optional<Element> getExistingLabelElement() {
		return getElement().getChildren().filter(child -> !child.hasAttribute("slot")).findFirst();
	}

	private Element getLabelElement() {
		return getExistingLabelElement().orElseGet(() -> {
			Element element = Element.createText("");
			getElement().appendChild(element);
			return element;
		});
	}

	/**
	 * Sets the path this item links to.
	 * 
	 * @param path the path to link to
	 * @return this instance for chaining
	 */
	public AppNavItem setPath(String path) {
		getElement().setAttribute("path", path);
		return this;
	}

	/**
	 * Sets the view this item links to.
	 * 
	 * @param view the view to link to
	 * @return this instance for chaining
	 */
	public AppNavItem setPath(Class<? extends Component> view) {
		String url = RouteConfiguration.forRegistry(getRouter().getRegistry()).getUrl(view);
		setPath(url);
		return this;
	}

	private Router getRouter() {
		Router router = null;
		if (getElement().getNode().isAttached()) {
			StateTree tree = (StateTree) getElement().getNode().getOwner();
			router = tree.getUI().getInternals().getRouter();
		}
		if (router == null) {
			router = VaadinService.getCurrent().getRouter();
		}
		if (router == null) {
			throw new IllegalStateException("Implicit router instance is not available. "
					+ "Use overloaded method with explicit router parameter.");
		}
		return router;
	}

	public String getPath() {
		return getElement().getAttribute("path");
	}

	private int getIconElementIndex() {
		for (int i = 0; i < getElement().getChildCount(); i++) {
			if ("prefix".equals(getElement().getChild(i).getAttribute("slot"))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Sets the icon for the item.
	 * <p>
	 * Can also be used to set a custom component to be shown in front of the label.
	 * 
	 * @param icon the icon to show
	 * @return this instance for chaining
	 */
	public AppNavItem setIcon(Component icon) {
		icon.getElement().setAttribute("slot", "prefix");
		int iconElementIndex = getIconElementIndex();
		if (iconElementIndex != -1) {
			getElement().setChild(iconElementIndex, icon.getElement());
		} else {
			getElement().appendChild(icon.getElement());
		}
		return this;
	}

	/**
	 * Sets the icon using a CSS class for the item.
	 * <p>
	 * Can also be used to set a custom component to be shown in front of the label.
	 * 
	 * @param iconClass the CSS class to use for showing the icon
	 * @return this instance for chaining
	 */
	public AppNavItem setIconClass(String iconClass) {
		Span icon = new Span();
		icon.setClassName(iconClass);
		setIcon(icon);
		return this;
	}

	/**
	 * Sets the expanded status of the item.
	 *
	 * @param value true to expand the item, false to collapse it
	 */
	public AppNavItem setExpanded(boolean value) {
		if (value) {
			getElement().setAttribute("expanded", "");
//            getElement().getStyle().set("@active", "color", "red");
		} else {
			getElement().removeAttribute("expanded");
		}
		return this;
	}

}
