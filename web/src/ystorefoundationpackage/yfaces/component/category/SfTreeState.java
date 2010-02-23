/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2009 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package ystorefoundationpackage.yfaces.component.category;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.tree2.TreeState;


/**
 * 
 * 
 */
public class SfTreeState implements TreeState
{
	private static final Logger log = Logger.getLogger(SfTreeState.class);

	private final Set<String> selectedNodes = new HashSet();

	private boolean transientToggle = false;
	private HashSet<String> expandedNodesSet = null;

	public SfTreeState()
	{
		this.expandedNodesSet = new HashSet<String>();
	}

	public boolean isTransient()
	{
		return this.transientToggle;
	}

	public void setTransient(final boolean trans)
	{
		this.transientToggle = trans;
	}

	public boolean isSelected(final String nodeId)
	{
		return this.selectedNodes.contains(nodeId);
	}

	public void setSelected(final String nodeId)
	{
		this.selectedNodes.add(nodeId);
	}


	public void collapsePath(final String[] nodePath)
	{
		for (int i = 0; i < nodePath.length; i++)
		{
			final String nodeId = nodePath[i];
			this.expandedNodesSet.remove(nodeId);
		}
	}

	public void expandPath(final String[] nodePath)
	{
		for (int i = 0; i < nodePath.length; i++)
		{
			final String nodeId = nodePath[i];
			this.expandedNodesSet.add(nodeId);
		}
	}

	public boolean isNodeExpanded(final String nodeId)
	{
		return this.expandedNodesSet.contains(nodeId);
	}


	public void toggleExpanded(final String nodeId)
	{
		if (isNodeExpanded(nodeId))
		{
			final boolean result = this.expandedNodesSet.remove(nodeId);
			if (log.isDebugEnabled())
			{
				log.debug("Collapse node " + nodeId + " (" + result + ")");
			}
		}
		else
		{
			final boolean result = this.expandedNodesSet.add(nodeId);
			if (log.isDebugEnabled())
			{
				log.debug("Expand node " + nodeId + " (" + result + ")");
			}
		}
	}

	//	//
	//	// DeSerialization
	//	//
	//	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
	//	{
	//		in.defaultReadObject();
	//	}
	//	
	//	//
	//	// Serialization
	//	//
	//	private void writeObject(ObjectOutputStream aOutputStream) throws IOException
	//	{
	//		aOutputStream.defaultWriteObject();
	//	}		




}
