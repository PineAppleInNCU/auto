package com.guanyi.rehab;

public class ActionProducts {
	
	private int _id;
	private String _aciotnname;
	private String ActionData;
	
	public ActionProducts() {		
	}
	
	public ActionProducts(String _actionname) {
		this._aciotnname = _actionname;
	}
	
	public ActionProducts(String _actionname,String ActionData) {
		this._aciotnname = _actionname;
		this.ActionData = ActionData;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_aciotnname() {
		return _aciotnname;
	}

	public void set_aciotnname(String _aciotnname) {
		this._aciotnname = _aciotnname;
	}

	public String getActionData() {
		return ActionData;
	}

	public void setActionData(String actionData) {
		ActionData = actionData;
	}	
}
