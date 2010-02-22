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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.tree2.Tree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeState;
import org.apache.myfaces.custom.tree2.TreeWalker;


/**
 * {@link TreeModel} implementation based on node identifiers instead of indices.<br/>
 * <br/>
 * Tomahawk implementation means with 'nodeid' the position within the current tree level.<br/>
 * e.g. '0:2:1' is a valid node address.<br/>
 * <br/>
 * This implementation doesn't use indices but the node identifiers instead.<br/>
 * 
 * 
 */
public class SfTreeModel implements TreeModel, TreeWalker
{
	private static final Logger log = Logger.getLogger(SfTreeModel.class);

	private TreeNode rootNode = null;
	private TreeState treeState = null;

	//TreeWalker stuff
	private transient Tree tree;
	private transient boolean checkState = true;
	private transient Stack<List<TreeNode>> nodeListStack;
	private transient Stack<TreeNode> parentNodeStack = null;

	private transient Map<String, MetaTreeNode> nodeLookup = null;

	private class MetaTreeNode
	{
		private MetaTreeNode parentNode = null;
		private TreeNode node = null;
		private boolean isLastChild = false;

		private String[] getNodePath()
		{
			final List<String> _result = new ArrayList<String>();
			MetaTreeNode mnode = this;
			while (mnode != null)
			{
				_result.add(0, mnode.node.getIdentifier());
				mnode = mnode.parentNode;
			}
			final String[] result = _result.toArray(new String[] {});
			return result;
		}
	}

	public SfTreeModel(final TreeNode rootNode)
	{
		this.rootNode = rootNode;
		this.treeState = new SfTreeState();
	}



	public String[] getPathInformation(final String nodeId)
	{
		if (nodeId == null)
		{
			throw new IllegalArgumentException("Cannot determine parents for a null node.");
		}

		final MetaTreeNode mNode = getNodeLookupMap().get(nodeId);
		return mNode.getNodePath();
	}

	public boolean isLastChild(final String nodeId)
	{
		final MetaTreeNode mNode = getNodeLookupMap().get(nodeId);
		return mNode.isLastChild;
	}

	public TreeNode getNodeById(final String nodeId)
	{
		TreeNode result = null;
		if (nodeId != null)
		{
			final MetaTreeNode mNode = getNodeLookupMap().get(nodeId);
			result = mNode.node;
		}
		return result;
	}


	public TreeState getTreeState()
	{
		return this.treeState;
	}

	public void setTreeState(final TreeState treeState)
	{
		this.treeState = treeState;
	}


	public TreeWalker getTreeWalker()
	{
		return this;
	}

	/**
	 * The renderer sets the working tree.<br/>
	 * The {@link TreeWalker} uses this {@link Tree} to pass the current processing node (node which shall be rendered)
	 * to the renderer.<br/>
	 * 
	 * @see org.apache.myfaces.custom.tree2.TreeWalker#setTree(org.apache.myfaces.custom.tree2.Tree)
	 */
	public void setTree(final Tree tree)
	{
		this.tree = tree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.myfaces.custom.tree2.TreeWalker#getRootNodeId()
	 */
	public String getRootNodeId()
	{
		return this.rootNode.getIdentifier();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.myfaces.custom.tree2.TreeWalker#isCheckState()
	 */
	public boolean isCheckState()
	{
		return this.checkState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.myfaces.custom.tree2.TreeWalker#setCheckState(boolean)
	 */
	public void setCheckState(final boolean checkState)
	{
		this.checkState = checkState;
	}

	/**
	 * There's no useful documentation on that method.<br/>
	 * Functionality is implemented by researching tomahawks {@link org.apache.myfaces.custom.tree2.TreeWalkerBase} and
	 * observation of behavior.<br/>
	 * <br/>
	 * Task of next() is to iterate over each available node and provide each one to the renderer<br/>
	 * (implicitly via the {@link Tree#setNodeId(String)})<br/>
	 * It's helpful to simply think of a 'flat view' of the tree where each element gets iterated over.<br/>
	 * <br/>
	 * This method is called by the renderer.<br/>
	 * Iteration is done per call and not in one step.<br/>
	 * <br/>
	 * Rule for the 'flat view' is:<br/>
	 * 1) first node: is rootnode (1)<br/>
	 * 2) next node: first child (1.1)<br/>
	 * 3) next node: first childChild (1.1.1) of previous child (1.1) or when not available second child (1.2)<br/>
	 * 4) and so on<br/>
	 * <br/>
	 * 
	 * @return true when a node was available
	 * 
	 * @see org.apache.myfaces.custom.tree2.TreeWalker#next()
	 */
	//whereas original implementation starts with rootnode and ends with rootnode,
	//this impl. skips last step of iterate to rootnode again
	public boolean next()
	{
		boolean result = true;

		//a stack is available when iteration is in progress...
		if (nodeListStack != null)
		{
			//...stack is empty when no more nodes are available; stop iteration
			if (nodeListStack.isEmpty())
			{
				result = false;
			}
			else
			{
				//retrieve the current list of processing nodes
				final List<TreeNode> nodeList = nodeListStack.peek();

				//when no more nodes are available... 
				if (nodeList.isEmpty())
				{
					//...upper the processing level; pop last elements from stack
					this.nodeListStack.pop();
					this.parentNodeStack.pop();
					result = next();
				}
				else
				{
					//...otherwise remove current node from list
					final TreeNode node = nodeList.remove(0);
					final TreeNode parentNode = this.parentNodeStack.peek();

					//childnodes are stacked when forced to do or current node is expanded  
					final boolean addNextLevel = !checkState
							|| (checkState && tree.getDataModel().getTreeState().isNodeExpanded(node.getIdentifier()));
					if (addNextLevel)
					{
						final List<TreeNode> childNodes = node.getChildren();
						this.nodeListStack.push(new LinkedList(childNodes));
						this.parentNodeStack.push(node);
					}

					//set current node
					this.setProcesingNode(node, parentNode, nodeList.isEmpty());

					if (log.isDebugEnabled())
					{
						log.debug("Walking to node: " + node.getIdentifier());
					}
				}
			}
		}
		//otherwise start iteration
		else
		{
			//initially stacked processing nodelist is taken from the root 
			this.nodeListStack = new Stack<List<TreeNode>>();
			this.nodeListStack.push(new LinkedList<TreeNode>(rootNode.getChildren()));

			this.parentNodeStack = new Stack<TreeNode>();
			this.parentNodeStack.push(rootNode);

			this.setProcesingNode(rootNode, null, true);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.myfaces.custom.tree2.TreeWalker#reset()
	 */
	public void reset()
	{
		this.nodeListStack = null;
	}

	private void setProcesingNode(final TreeNode node, final TreeNode parentNode, final boolean isLastChild)
	{
		final Map<String, MetaTreeNode> map = getNodeLookupMap();
		MetaTreeNode mnode = map.get(node.getIdentifier());

		if (mnode == null)
		{
			final MetaTreeNode parent = parentNode != null ? map.get(parentNode.getIdentifier()) : null;
			mnode = new MetaTreeNode();
			mnode.node = node;
			mnode.isLastChild = isLastChild;
			mnode.parentNode = parent;
			map.put(node.getIdentifier(), mnode);
		}
		this.tree.setNodeId(node.getIdentifier());
	}

	private Map<String, MetaTreeNode> getNodeLookupMap()
	{
		if (this.nodeLookup == null)
		{
			this.nodeLookup = new HashMap<String, MetaTreeNode>();
		}
		return this.nodeLookup;
	}

}
