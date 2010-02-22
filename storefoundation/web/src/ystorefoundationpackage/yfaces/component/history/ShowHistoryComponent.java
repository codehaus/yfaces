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
package ystorefoundationpackage.yfaces.component.history;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.yfaces.component.YComponent;

import java.util.List;




/**
 * This component displays the recent products that the user views.
 */
public interface ShowHistoryComponent extends YComponent
{

	//model
	List<ItemModel> getHistoryEntries();

	void setHistoryEntries(List<ItemModel> historyEntries);

	void addHistoryEntry(ItemModel item);

	void addFilterClass(Class clazz);

	int getHistoryEntrySize();

	void setHistoryEntrySize(int historyEntrySize);

	boolean isEnabledProduct();

	void setEnabledProduct(boolean enabledProduct);

	boolean isEnabledCategory();

	void setEnabledCategory(boolean enabledCategory);

	void resetHistory();

}
