package de.hybris.yfaces.component;

public interface YComponentProcessor {

	YComponent createComponent();

	void initializeComponent(YComponent cmp);

	void setProperty(YComponent cmp, String property, Object value);

}
