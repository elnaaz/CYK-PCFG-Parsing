package bottomup;

import java.util.ArrayList;

public class Rule {

	String left_hand;

	ArrayList<String> right_hand_1=new ArrayList<String>();
	ArrayList<String> right_hand_2=new ArrayList<String>();

	ArrayList<Double> prob=new ArrayList<Double>();

	boolean tag=false;

	public Rule(String left_hand, String right_hand, double prob) {
		super();
		this.left_hand = left_hand;
		this.right_hand_1.add( right_hand);
		this.prob.add(prob);
		this.tag = true;
	}

	public Rule(String left_hand, String right_hand_1, String right_hand_2, double d) {
		super();
		this.left_hand = left_hand;
		this.right_hand_1.add( right_hand_1);
		this.right_hand_2.add( right_hand_2);
		this.prob.add(d);
		this.tag=false;
	}

	public void updateBinaryRule(String r1, String r2, double d){
		boolean found=false;
		for(int i=0; i<right_hand_1.size();i++){
			if(right_hand_1.get(i).equals(r1) && right_hand_2.get(i).equals(r2)) {
				found=true;
			}
		}
		if(! found){
			this.right_hand_1.add( r1);
			this.right_hand_2.add( r2);
			this.prob.add(d);
			this.tag=false;
		}
	}

	public void updateUnaryRule(String r1, double d){
		boolean found=false;
		for(int i=0; i<right_hand_1.size();i++){
			if(right_hand_1.get(i).equals(r1)) {
				found=true;
			}
		}
		if(! found){
			this.right_hand_1.add( r1);
			this.prob.add(d);
			this.tag=true;
		}
	}

}