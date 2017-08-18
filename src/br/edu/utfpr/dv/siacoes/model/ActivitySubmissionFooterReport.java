package br.edu.utfpr.dv.siacoes.model;

public class ActivitySubmissionFooterReport {
	
	private String group;
	private double total;
	private int minimum;
	private int maximum;
	private String situation;
	private int sequence;
	
	public ActivitySubmissionFooterReport(){
		this.setGroup("");
		this.setTotal(0);
		this.setMinimum(0);
		this.setMaximum(0);
		this.setSituation("");
		this.setSequence(0);
	}
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getMinimum() {
		return minimum;
	}
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	public int getMaximum() {
		return maximum;
	}
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	public String getSituation() {
		return situation;
	}
	public void setSituation(String situation) {
		this.situation = situation;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
