package com.workspace.shopping.code;

/**
 * Class use to create discountSlap
 */
public class DiscountSlab {
	private double rangMin;
	private double rangMax;
	private int discPerc;

	public DiscountSlab(long rangMin, long rangMax, int discPerc) {
		super();
		this.rangMin = rangMin;
		this.rangMax = rangMax;
		this.discPerc = discPerc;
	}

	public DiscountSlab() {
		
	}

	public double getRangMin() {
		return rangMin;
	}

	public void setRangMin(double rangMin) {
		this.rangMin = rangMin;
	}

	public double getRangMax() {
		return rangMax;
	}

	public void setRangMax(double rangMax) {
		this.rangMax = rangMax;
	}

	public int getDiscPerc() {
		return discPerc;
	}

	public void setDiscPerc(int discPerc) {
		this.discPerc = discPerc;
	}

	@Override
	public String toString() {
		return "DiscountSlab [rangMin=" + rangMin + ", rangMax=" + rangMax + ", discPerc=" + discPerc + "]";
	}

}
