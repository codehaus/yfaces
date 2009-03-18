package de.hybris.yfaces.doclet;


import de.hybris.yfaces.YComponentInfo;
import de.hybris.yfaces.doclet.YCmpXsdElement;
import de.hybris.yfaces.doclet.YCmpXsdElement.YCmpXsdAttribute;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;


public class YCmpXsdDoclet
{
	private static final Logger log = Logger.getLogger(YCmpXsdDoclet.class);

	private static final String XSD_TEMPLATE_NAME = "yfaces-components.xsd.vm";

	//the root directory of all operations
	private static final String ROOT_DIR = "D:/hybrisplatform_trunk/ext-commerce/storefoundation";

	//Searchpath for component classes
	private static final String CLASSES_DIR = "/web/src/";

	//Searchpath for component files (xhtml) 
	private static final String[] COMPONENT_DIRS =
	{ "/web/webroot/components", "/web/webroot/components/cms" };


	//target directory and name of XSD file
	private static final String XSD_TARGET_DIR = "/web/webroot/WEB-INF";
	private static final String XSD_TARGET_NAME = XSD_TEMPLATE_NAME.substring(0, XSD_TEMPLATE_NAME.length() - 3);


	public static YCmpXsdDoclet doclet = new YCmpXsdDoclet();

	/**
	 * Start.
	 * 
	 * @param argc
	 */
	public static void main(final String[] argc)
	{
		for (final String path : COMPONENT_DIRS)
		{
			doclet.loadComponents(ROOT_DIR + path);
		}

		final String[] params = new String[]
		{ "-sourcepath", ROOT_DIR + CLASSES_DIR, "-subpackages", "ystorefoundationpackage.yfaces.component" };

		com.sun.tools.javadoc.Main.execute("programmName", YCmpXsdDoclet.class.getName(), params);

		doclet.generateXSD(ROOT_DIR + XSD_TARGET_DIR);
	}

	public static boolean start(final RootDoc root)
	{
		final ClassDoc[] classes = root.classes();
		for (int i = 0; i < classes.length; ++i)
		{
			doclet.processClass(classes[i]);
		}
		log.info("Generated XSD (" + ROOT_DIR + XSD_TARGET_DIR + "/" + XSD_TARGET_NAME + ")");
		return true;
	}




	private Map<String, YCmpXsdElement> componentMap = null;

	public YCmpXsdDoclet()
	{
		this.componentMap = new HashMap<String, YCmpXsdElement>();
	}

	public void processClass(final ClassDoc clazz)
	{
		final String name = clazz.qualifiedName();

		//lookup for a YComponent under current class 
		final YCmpXsdElement xcmp = componentMap.get(name);

		//when component is available...
		if (xcmp != null)
		{
			xcmp.setComment(clazz.commentText());
			for (MethodDoc md : clazz.methods())
			{
				if (md.name().startsWith("set"))
				{
					String property = md.name().substring(3).toLowerCase();

					YCmpXsdAttribute xsdprop = xcmp.getAttribute(property);
					if (xsdprop != null)
					{
						String descr = md.commentText();
						xsdprop.setDescription(descr);
						xsdprop.setType(md.signature().substring(1, md.signature().length() - 1));

						//						log.info("Found YComponent: " + xcmp.getComponentInfo().getId() + "." + xsdprop.getName() + " -> " + xsdprop.getDescription());
					}
				}

			}
		}
	}

	public void generateXSD(final String target)
	{
		try
		{
			//Reader in = new FileReader(path + "\\" + XSD_TEMPLATE );
			final Reader in = new InputStreamReader(YCmpXsdDoclet.class.getResourceAsStream(XSD_TEMPLATE_NAME));
			final Writer out = new FileWriter(target + "\\" + XSD_TARGET_NAME);

			final VelocityContext ctx = new VelocityContext();
			ctx.put("elements", this.componentMap.values());

			Velocity.evaluate(ctx, out, "out", in);
			out.flush();
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private static Pattern pattern = Pattern.compile(".*?/(.*)Tag\\.xhtml");

	/**
	 * Load all components which can be found at given path.
	 * 
	 * @param path
	 * @return
	 */
	private void loadComponents(final String path)
	{
		final List<File> files = new ArrayList<File>();
		final File file = new File(path);

		if (file.isDirectory())
		{
			files.addAll(Arrays.asList(file.listFiles()));
		}
		else
		{
			files.add(file);
		}

		try
		{
			for (final File _file : files)
			{
				final URL url = _file.toURL();
				final Matcher m = pattern.matcher(url.toExternalForm());
				if (m.matches())
				{
					final YComponentInfo cmp = new YComponentInfo(url);
					this.componentMap.put(cmp.getSpecificationClassName(), new YCmpXsdElement(cmp));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
