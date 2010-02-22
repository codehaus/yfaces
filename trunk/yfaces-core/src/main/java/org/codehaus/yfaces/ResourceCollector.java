package org.codehaus.yfaces;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

public class ResourceCollector {

	private static final Logger LOG = Logger.getLogger(ResourceCollector.class);

	// matches when a string represents a visible directory
	// (no dot after a slash; ends with a slash)
	private static final Pattern UNHIDDEN_DIRECTORY_PATTERN = Pattern.compile("[^\\.]*/?");

	private Set<URL> fileResources = null;
	private Set<String> filePaths = null;
	private Set<String> dirPaths = null;
	private String namespace = null;

	/**
	 * Constructor.
	 * 
	 * @param namespace
	 *          namespace
	 */
	public ResourceCollector(final String namesapce) {
		this.namespace = namesapce;
		this.fileResources = new TreeSet<URL>(new Comparator<URL>() {
			public int compare(final URL o1, final URL o2) {
				return o1.getFile().compareTo(o2.getFile());
			}
		});
		this.dirPaths = new TreeSet<String>();
		this.filePaths = new TreeSet<String>();

		// remove trailing slash if any
		if (this.namespace.endsWith("/")) {
			this.namespace = this.namespace.substring(0, this.namespace.length() - 1);
		}
	}

	/**
	 * Returns a namespace for this collection of resources
	 * 
	 * @return namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Returns all plain File resources which were found.
	 * 
	 * @return Collection of resources
	 */
	public Collection<URL> getFileResources() {
		return this.fileResources;
	}

	/**
	 * Returns all directory resources which were found.
	 * 
	 * @return Collection of resources
	 */
	public Collection<String> getDirLocations() {
		return this.dirPaths;
	}

	public Collection<String> getFileLocations() {
		return this.filePaths;
	}

	/**
	 * Adds each resource which is a sub-resource of passed root. Deep (recursive) search is possible.
	 * 
	 * @param base
	 *          base path starts relative to webapplication root
	 * @param recursive
	 *          true to enable deep search
	 */
	public void addResources(final String base, final boolean recursive) {

		// take sub-resources which are found for passed 'base'
		final ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		final Set<String> resources = ctx.getResourcePaths(base);

		// is null when base is not valid
		if (resources != null) {

			// for each resource...
			for (final String resource : resources) {

				// ... check whether it is a 'directory'
				if (isValidDirectory(resource)) {
					this.dirPaths.add(resource);
					// ... and when recursive is enabled, dig a level deeper
					if (recursive) {
						addResources(resource, recursive);
					}
				} else {
					// ... or check for a valid xhtml file
					if (isValidFile(resource)) {
						try {
							final URL url = FacesContext.getCurrentInstance().getExternalContext().getResource(
									resource);
							fileResources.add(url);
							filePaths.add(resource);
						} catch (final MalformedURLException e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
		} else {
			LOG.error(base + " is not a valid resource path");
		}
	}

	/**
	 * Tries to detect, whether passed resource is a directory which is not hidden.
	 * 
	 * @param resource
	 *          resource to check
	 * @return true when valid (visible directory)
	 */
	private boolean isValidDirectory(final String resource) {
		return UNHIDDEN_DIRECTORY_PATTERN.matcher(resource).matches();
	}

	/**
	 * Detects whether resource is a file which and whether it is filtered or not.
	 * 
	 * @param resource
	 *          resource to check
	 * @return true when valid
	 */
	private boolean isValidFile(final String resource) {
		return (resource.endsWith(".xhtml") && !resource.endsWith("index.xhtml"));
	}

}
