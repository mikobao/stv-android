package com.stv.supervod.utils;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Description: 计算list控件高度 Copyright (c) 永新视博 All Rights Reserved.
 * 
 * @version 1.0 2011-12-5 下午12:36:04 mustang created
 */
public class ListUtil {

	/**
	 * Description: 计算普通的listview高度
	 * 
	 * @Version1.0 2011-12-5 下午12:37:18 mustang created
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView, Integer titleheight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		View listItem = listAdapter.getView(0, null, listView);
		listItem.measure(0, 0);
		int itemHeight = listItem.getMeasuredHeight();
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			totalHeight += itemHeight;
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// 如果列表标题栏有高度，也要计算一下
		if (titleheight != null) {
			totalHeight += titleheight;
		}
		params.height = totalHeight;
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * Description: 计算ExpandListView控件高度
	 * 
	 * @Version1.0 2011-12-5 下午12:37:35 mustang created
	 * @param listView
	 */
	public static void setExpandListViewHeightBasedOnChildren(ExpandableListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		View listItem1 = listAdapter.getView(0, null, listView);
		for (int i = 0; i < listView.getExpandableListAdapter().getGroupCount(); i++) {
			//View listItem1 = listAdapter.getView(i, null, listView);
			listItem1.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem1.getMeasuredHeight(); // 统计所有子项的总高度
			for (int j = 0; j < listView.getExpandableListAdapter().getChildrenCount(i); j++) {
				//View listItem = listAdapter.getView(i, null, listView);
				listItem1.measure(0, 0); // 计算子项View 的宽高
				totalHeight += listItem1.getMeasuredHeight(); // 统计所有子项的总高度
			}
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	public static void setGridViewHeightBasedOnChildren(GridView gridView, int numColums, float verticalSpacing) {  
		//获取GridView对应的Adapter  
		ListAdapter gridViewListAdapter = gridView.getAdapter();   
		if (gridViewListAdapter == null) { 
			return;  
		}  
		  
		int totalHeight = 0;
		int temp = gridViewListAdapter.getCount() % numColums;
		int len = gridViewListAdapter.getCount() / numColums;
		len = temp == 0 ?  len : len + 1;
		for (int i = 0; i < len; i++) { 
			View gridViewItem = gridViewListAdapter.getView(i, null, gridView);  
			gridViewItem.measure(0, 0); //计算子项View 的宽高  
			totalHeight += gridViewItem.getMeasuredHeight(); //统计所有子项的总高度  
		}  
	
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (int)verticalSpacing * (len - 1);  
		gridView.setLayoutParams(params);  
	}  
}
