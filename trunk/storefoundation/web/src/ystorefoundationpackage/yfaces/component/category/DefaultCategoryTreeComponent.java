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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeState;
import org.codehaus.yfaces.component.AbstractYComponent;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>CategoryTreeComponent</code> interface.
 */
public class DefaultCategoryTreeComponent extends AbstractYComponent implements CategoryTreeComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultCategoryTreeComponent.class);

	private static final String SESSION_KEY = DefaultCategoryTreeComponent.class.getName();

	private CategoryModel category = null;
	private transient TreeModel categoryTree = null;

	/**
	 * Help class for <code>CategoryTreeComponent</code>
	 */
	public static class CategoryTreeNodeBase extends TreeNodeBase
	{
		Object value = null;
		private List<TreeNode> children = null;

		public CategoryTreeNodeBase(final Object value)
		{
			super();
			this.value = value;
		}

		public Object getValue()
		{
			return this.value;
		}

		@Override
		public List<TreeNode> getChildren()
		{
			if (this.children == null)
			{
				final CategoryModel catg = (CategoryModel) value;
				//				final List<CategoryModel> catgList = Webfoundation.getInstance().getServices().getCategoryService()
				//						.findAllByParentCategory(catg, false);

				List<CategoryModel> catgList = catg.getCategories();
				catgList = YStorefoundation.getRequestContext().getProductManagement().getFilteredCategories(catgList);

				if (catgList.isEmpty())
				{
					this.children = Collections.EMPTY_LIST;
				}
				else
				{
					this.children = new ArrayList();
					for (final CategoryModel cat : catgList)
					{
						final TreeNode childNode = new CategoryTreeNodeBase(cat);
						childNode.setType(super.getType());
						childNode.setDescription(cat.getName());
						childNode.setIdentifier(cat.getCode());
						this.children.add(childNode);
					}
					this.children = Collections.unmodifiableList(this.children);
				}
			}
			return this.children;
		}

		@Override
		public String toString()
		{
			return super.toString() + "(" + getIdentifier() + ")";
		}

	}


	public CategoryModel getCategory()
	{
		return this.category;
	}

	public void setCategory(final CategoryModel category)
	{
		this.category = category;
	}


	@Override
	public void validate()
	{
		if (this.category == null)
		{
			this.category = YStorefoundation.getRequestContext().getSessionContext().getCategory();
		}
	}

	@Override
	public void refresh()
	{
		final SfSessionContext log = YStorefoundation.getRequestContext().getSessionContext();
		if (log.isLanguageChanged())
		{
			final TreeState oldState = this.getCategoryTree().getTreeState();
			final TreeModel newTree = this.createCategoryTree();
			newTree.setTreeState(oldState);
			this.setCategoryTree(newTree);
		}
	}

	public TreeModel getCategoryTree()
	{
		if (this.categoryTree == null)
		{
			final SfSessionContext sfSession = YStorefoundation.getRequestContext().getSessionContext();
			final CatalogModel cat = YStorefoundation.getRequestContext().getSessionContext().getCatalog();

			final Map m = sfSession.getAttributes();
			final CatalogModel prevCat = (CatalogModel) m.get(SESSION_KEY + "cat");
			this.categoryTree = (TreeModel) m.get(SESSION_KEY + "tree");
			if (!cat.equals(prevCat) || categoryTree == null)
			{
				this.setCategoryTree(this.createCategoryTree());
			}
			if (!FacesContext.getCurrentInstance().getRenderKit().getResponseStateManager().isPostback(
					FacesContext.getCurrentInstance()))
			{
				this.openCategoryTree(categoryTree, getCategory());
			}
		}
		return this.categoryTree;
	}

	protected void setCategoryTree(final TreeModel tree)
	{
		this.categoryTree = tree;
		final SfSessionContext sfSession = YStorefoundation.getRequestContext().getSessionContext();
		final Map m = sfSession.getAttributes();
		m.put(SESSION_KEY + "tree", tree);
		m.put(SESSION_KEY + "cat", YStorefoundation.getRequestContext().getSessionContext().getCatalog());
	}

	private TreeModel createCategoryTree()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final CatalogModel cat = reqCtx.getSessionContext().getCatalog();
		final CatalogVersionModel cvm = reqCtx.getPlatformServices().getCatalogService().getSessionCatalogVersion(cat.getId());
		final List<CategoryModel> catgList = reqCtx.getProductManagement().getFilteredCategories(cvm.getRootCategories());


		final TreeNode rootNode = new TreeNodeBase("rootNode", "description", "root", false);

		for (final CategoryModel catg : catgList)
		{
			final TreeNode child = new CategoryTreeNodeBase(catg);
			child.setType("categoryNode");
			child.setIdentifier(catg.getCode());
			child.setDescription(catg.getName());
			rootNode.getChildren().add(child);
		}

		final TreeModel result = new SfTreeModel(rootNode);
		result.getTreeState().setTransient(true);

		return result;
	}

	private void openCategoryTree(final TreeModel treeModel, final CategoryModel catg)
	{
		final Set<String> expandNodes = new LinkedHashSet<String>();
		expandNodes.add("root");
		final List<List<CategoryModel>> catgPathes = catg != null ? YStorefoundation.getRequestContext().getProductManagement()
				.getCategoryPath(catg) : Collections.EMPTY_LIST;


		for (final List<CategoryModel> catgList : catgPathes)
		{
			for (final CategoryModel _catg : catgList)
			{
				expandNodes.add(_catg.getCode());
			}
		}
		treeModel.getTreeState().expandPath(expandNodes.toArray(new String[] {}));
	}

}
