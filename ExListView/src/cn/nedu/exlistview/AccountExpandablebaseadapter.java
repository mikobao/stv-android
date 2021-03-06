package cn.nedu.exlistview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class AccountExpandablebaseadapter extends BaseExpandableListAdapter {

	private Context context;
	// 准备一级列表中显示的数据
	List<String> accountFather = new ArrayList<String>();
	// 准备二级列表中显示的数据
	List<List<Map<String, String>>> accountChild = new ArrayList<List<Map<String, String>>>();

	public AccountExpandablebaseadapter(Context context,
			List<String> accountFathers,
			List<List<Map<String, String>>> accountChilds) {

		this.accountFather = accountFathers;
		this.accountChild = accountChilds;
		this.context = context;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.member_listview, null);
		}
		//一级列表中显示的标题
		TextView txtFather = (TextView) view.findViewById(R.id.txtFather);
		txtFather.setText(getGroup(groupPosition).toString());
		System.out.println("=======groupPosition=========="+groupPosition);
		return view;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public Object getGroup(int groupPosition) {
		return accountFather.get(groupPosition).toString();
	}

	public int getGroupCount() {
		return accountFather.size();

	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		System.out.println("=======childPosition=========="+childPosition);
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.member_childitem, null);
		}
		//类别
		final TextView txtAccountType = (TextView) view
				.findViewById(R.id.txtAccountType);
		txtAccountType.setText(accountChild.get(groupPosition).get(childPosition)
				.get(ExListView.FATHERACCOUNT).toString());
		//子类别
		final TextView txtChildType = (TextView) view.findViewById(R.id.txtChildType);
		txtChildType.setText(accountChild.get(groupPosition).get(childPosition)
				.get(ExListView.CHILDACCOUNT).toString());
		//余额
		TextView txtBalance = (TextView) view.findViewById(R.id.txtBalance);
		txtBalance.setText(accountChild.get(groupPosition).get(childPosition)
				.get(ExListView.BALANCEACCOUNT).toString());

		ImageView imgDraw = (ImageView) view.findViewById(R.id.imgDraw);
		imgDraw.setBackgroundResource(R.drawable.icon_drag);

		return view;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return accountChild.get(groupPosition).get(childPosition)
				.get(ExListView.FATHERACCOUNT).toString();
	}

	public int getChildrenCount(int groupPosition) {
		return accountChild.get(groupPosition).size();
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}