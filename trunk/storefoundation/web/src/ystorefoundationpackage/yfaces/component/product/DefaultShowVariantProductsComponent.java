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
package ystorefoundationpackage.yfaces.component.product;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.yfaces.component.AbstractYComponent;

import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>ShowVariantProductsComponent</code> interface.
 */
public class DefaultShowVariantProductsComponent extends AbstractYComponent implements ShowVariantProductsComponent
{

	private static final long serialVersionUID = 57992864848121918L;

	private ProductModel product = null;
	private List<SimpleVariantProduct> variantProducts = null;
	private List<FormattedAttribute> attributeDescriptors = null;
	private boolean printPage = false;

	@Override
	public void validate()
	{
		if (this.product == null)
		{
			this.product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		}
		if (this.product != null)
		{
			if (this.variantProducts == null)
			{
				this.variantProducts = createSimpleVariantProducts();
			}
			if (this.attributeDescriptors == null)
			{
				this.attributeDescriptors = YStorefoundation.getRequestContext().getProductManagement().getVariantAttributeInfoList(
						product);
			}
		}
	}

	/**
	 * Helper class for the variant product.
	 */
	public static class SimpleVariantProduct implements Serializable
	{

		private static final long serialVersionUID = -6661308272839091623L;

		private ProductModel productModel = null;
		private Map<String, FormattedAttribute> variantMap = null;

		//default constructor, with parameter
		public SimpleVariantProduct(final ProductModel product)
		{
			this.productModel = product;
			final List<FormattedAttribute> variants = YStorefoundation.getRequestContext().getProductManagement()
					.getVariantAttributeInfoList(product);
			this.variantMap = new HashMap<String, FormattedAttribute>();
			for (final FormattedAttribute vinfo : variants)
			{
				this.variantMap.put(vinfo.getId(), vinfo);
			}
		}

		public ProductModel getProduct()
		{
			return this.productModel;
		}

		public void setProduct(final ProductModel product)
		{
			this.productModel = product;
		}

		public Map<String, FormattedAttribute> getVariantAttributes()
		{
			return this.variantMap;
		}

	}

	//	/**
	//	 * Helper class for variant products. It contains all attributes.
	//	 */
	//	public static class SimpleVariantDescriptor implements Serializable
	//	{
	//
	//		private static final long serialVersionUID = -6661308272839091624L;
	//
	//		private String qualifier = null;
	//		private String name = null;
	//
	//		//default constructor, with parameter
	//		public SimpleVariantDescriptor(final String qualifier, final String name)
	//		{
	//			this.qualifier = qualifier;
	//			this.name = name;
	//		}
	//
	//		public String getQualifier()
	//		{
	//			return this.qualifier;
	//		}
	//
	//		public String getName()
	//		{
	//			return this.name;
	//		}
	//	}

	public ProductModel getCurrentProduct()
	{
		return this.product;
	}

	public List<SimpleVariantProduct> getVariantProducts()
	{
		return this.variantProducts;
	}

	public void setCurrentProduct(final ProductModel product)
	{
		this.product = product;
	}

	public List<FormattedAttribute> getVariantAttributeDescriptors()
	{
		return this.attributeDescriptors;
	}

	public boolean isPrintPage()
	{
		return this.printPage;
	}

	public void setPrintPage(final boolean printPage)
	{
		this.printPage = printPage;
	}

	//	private List<SimpleVariantDescriptor> createVariantAttributeDescriptors(final ProductModel product)
	//	{
	//		ProductModel p;
	//		if (product.getVariantType() == null)
	//		{
	//			p = ((VariantProductModel) product).getBaseProduct();
	//		}
	//		else
	//		{
	//			p = product;
	//		}
	//
	//		final List<SimpleVariantDescriptor> result = new LinkedList<SimpleVariantDescriptor>();
	//		for (final Map.Entry<String, String> entry : Webfoundation.getInstance().getServices().getProductService()
	//				.getVariantAttributeNames(p).entrySet())
	//		{
	//			result.add(new SimpleVariantDescriptor(entry.getKey(), entry.getValue()));
	//		}
	//		return result;
	//	}

	private List<SimpleVariantProduct> createSimpleVariantProducts()
	{
		final List<SimpleVariantProduct> result = new ArrayList<SimpleVariantProduct>();
		final ProductModel baseProduct = ((VariantProductModel) this.product).getBaseProduct();
		final Iterator<ProductModel> itProducts = new ArrayList<ProductModel>(baseProduct.getVariants()).iterator();
		while (itProducts.hasNext())
		{
			final SimpleVariantProduct svp = new SimpleVariantProduct(itProducts.next());
			result.add(svp);
		}
		return result;
	}

}
