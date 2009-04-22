package de.hybris.yfaces.doclet;


import de.hybris.yfaces.YComponentInfo;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a {@link YComponentInfo} as a single XSD element.
 * 
 * @author Denny.Strietzbaum
 */
public class YCmpXsdElement
{
	/**
	 * Represents a single property of a {@link YComponentInfo} as a XSD attribute.
	 */
	public static class YCmpXsdAttribute
	{
		private String propertyName = null;
		private String description = null;
		private String type = null;

		public YCmpXsdAttribute(final String property)
		{
			this.propertyName = property;
		}

		public String getName()
		{
			return propertyName;
		}

		public String getDescription()
		{
			return description;
		}

		public void setDescription(final String text)
		{
			this.description = text;
		}

		public String getType()
		{
			return this.type;
		}

		public void setType(final String type)
		{
			this.type = type;
		}

	}


	private YComponentInfo cmp = null;
	private String comment = null;
	private Map<String, YCmpXsdAttribute> properties = null;


	public YCmpXsdElement(final YComponentInfo cmp)
	{
		this.cmp = cmp;
		this.properties = new HashMap<String, YCmpXsdAttribute>();
		for (final String property : cmp.getConfiguredAttributes())
		{
			this.properties.put(property.toLowerCase(), new YCmpXsdAttribute(property));
		}
	}

	/**
	 * Returns the wrapped {@link YComponentInfo}
	 * 
	 * @return {@link YComponentInfo}
	 */
	public YComponentInfo getComponentInfo()
	{
		return this.cmp;
	}

	public String getName()
	{
		return this.cmp.getTagName();
	}

	/**
	 * Returns the name of the renderer file
	 * 
	 * @return
	 */
	public String getRendererName()
	{
		String result = "???";
		final URL url = this.cmp.getURL();
		if (url != null)
		{
			result = (new File(url.toExternalForm())).getName();
		}
		return result;

	}

	/**
	 * Returns all XSD Attributes
	 * 
	 * @return Collection of {@link YCmpXsdAttribute}
	 */
	public Collection<YCmpXsdAttribute> getAttributes()
	{
		return this.properties.values();
	}

	/**
	 * Returns a XSD Attribute by its name.
	 * 
	 * @param name
	 *           name of attribute
	 * @return {@link YCmpXsdAttribute}
	 */
	public YCmpXsdAttribute getAttribute(final String name)
	{
		return this.properties.get(name);
	}

	/**
	 * Returns the comment of this element. This is the javadoc from the specification class.
	 * 
	 * @return comment String
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * Sets the comment for this element.
	 * 
	 * @param comment
	 *           comment to set.
	 */
	protected void setComment(final String comment)
	{
		this.comment = comment;
	}

}
