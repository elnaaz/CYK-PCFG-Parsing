import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class New {

	static ArrayList<Rule> rules=new ArrayList<Rule>(); 
	
	static double [][][] best;
	static int [][][] best_rule;
	static int [][][] best_split_k;
	static String [][][] best_right;
	static String [][][] best_left;
	//static String [][][] best_rules;
	static String [] sentence;
	
	public static void main(String[] args) throws IOException{

		String input= "the boy saw a girl";
		
		readRules("C:\\Users\\Elnaz\\Desktop\\cs544-hw3\\toy.pcfg");
		cky("S",input);
		print_cky_parse("S",input);

	}

	private static void print_cky_parse(String start, String input) {

		// Break Sentence into words
		String[] words = input.trim().split("\\s");
		
		int start_rule_id=-1;

		start_rule_id=0;
				
		System.out.println("***");
		System.out.println(subString(start_rule_id,0,words.length));
		
	}
	
	static String subString(int left_id, int i, int j) {
				
		System.out.println("rule_id: "+left_id+"\ti: "+i+"\tj: "+j);
		System.out.println("( "+rules.get(left_id)+" , "+i+" , "+j+" )");
		
		if(j==i+1){
//			return "( "+ rules.get(left_id).left_hand +" "+ rules.get(left_id).right_hand_1.get(best_rule[left_id][i][j])+" )";
			return " "+ rules.get(left_id).left_hand +" "+ rules.get(left_id).right_hand_1.get(best_rule[left_id][i][j])+" ";
		}
		else{
			System.out.println();
			System.out.println("best split: "+best_split_k[left_id][i][j]);
			System.out.println("best right: "+best_right[left_id][i][j]);
			System.out.println("best left: "+best_left[left_id][i][j]);
			System.out.println("best rule: "+best_rule[left_id][i][j]);
			System.out.println("best prob: "+best[left_id][i][j]);
			
			int left_index=0;
			int right_index=0;
			for(int h=0;h<rules.size();h++){
				if(rules.get(h).left_hand.equals(best_left[left_id][i][j])){
					left_index=h;
				}
				if(rules.get(h).left_hand.equals(best_right[left_id][i][j])){
					right_index=h;
				}
			}
			
			return "( "+ rules.get(left_id).left_hand +" ("+ subString( left_index , i , 
					best_split_k[left_id][i][j] ) +" )"+" ("+ subString(  right_index, 
							best_split_k[left_id][i][j] , j) +" ) )";
		}
	}

	static double cky(String start , String inputLine ) {
		
		// Break Sentence into words
		String[] words = inputLine.trim().split("\\s");
		sentence =words;
		
		for (int x=0; x<words.length; x++){
	         System.out.print(" - "+words[x] + "|" +sentence[x]);
	     }
	    System.out.println();
	    
	    // Make the dynamic table for the words
		best= new double [rules.size()][words.length+1][words.length+1];
		best_split_k= new int [rules.size()][words.length+1][words.length+1];
		best_rule= new int [rules.size()][words.length+1][words.length+1];
		best_right= new String [rules.size()][words.length+1][words.length+1];
		best_left= new String [rules.size()][words.length+1][words.length+1];		
		
		for (int t=0; t<rules.size();t++){
			for (int i=0; i<words.length+1;i++){
				for (int j=0; j<words.length+1;j++){
					best[t][i][j]=-1;
					best_split_k[t][i][j]=-2;
					best_rule[t][i][j]=-3;
					best_right[t][i][j]="";
					best_left[t][i][j]="";
				}
	    	}
		}
		 // Fill up for the word substitution/*
    	/*for (int a=0; a<words.length; a++){
	    	String word=words[a].trim();
	    	for (int i=0; i<rules.size();i++){
	    		Rule temp=rules.get(i);
	    		if(temp.tag){
	    				int index=temp.right_hand_1.indexOf(word);
	    				if( index != -1){
	    					best[i][a][a+1]=temp.prob.get(index);
	    					best_rule[i][a][a+1]=i;
	    					best_right[i][a][a+1]="";
	    					best_left[i][a][a+1]="";
	    					best_split_k[i][a][a+1]=-7;
	    				}
	    		}
	    	}
	    }*/
    	System.out.println("The final prob: " + search("S", 0,words.length));
		return search(start, 0,words.length);
	}	

	
	static double search(String rule, int i, int j) {
		
		System.out.println("Search for: "+ rule + " -->\ti: "+i+"\tj: "+j+"\t( "+rule+" , "+i+" , "+j+" )");
		//System.out.println();
		
		if(j==i+1){
			for (int t=0; t<rules.size();t++){
				Rule current=rules.get(t);
		    	if(current.left_hand.equals(rule) && current.tag ){
		    		for(int m=0; m< current.right_hand_1.size();m++){
		    			if(current.right_hand_1.get(m).equals(sentence[i])){
		    				best[t][i][j]=current.prob.get(m);
		    	    		best_rule[t][i][j]=m;
							best_right[t][i][j]=sentence[i];
							best_left[t][i][j]=sentence[i];
							best_split_k[t][i][j]=i;							
							return best[t][i][j];
		    			}
		    		}
		    	}
			}
		}
				
		double opt_prob=0;
		for(int k=i+1;k<j;k++){
			for (int t=0; t<rules.size();t++){
				Rule current=rules.get(t);
		    	if(current.left_hand.equals(rule) && !current.tag){		    		
		    		for(int g=0;g<current.right_hand_1.size();g++){
		    			System.out.println("&"+current.tag);
		    			System.out.println("&"+current.left_hand);
		    			System.out.println("&"+current.right_hand_1.toString());
		    			System.out.println("&"+current.right_hand_2.toString());
		    			double split_value=search( current.right_hand_1.get(g), i,k)*search( current.right_hand_2.get(g), k,j)* current.prob.get(g);		    		
			    		if(split_value>opt_prob){
							best[t][i][j]=split_value;
							best_rule[t][i][j]=g;
	    					best_right[t][i][j]=current.right_hand_2.get(g);
	    					best_left[t][i][j]=current.right_hand_1.get(g);
	    					best_split_k[t][i][j]=k;    		
			    		}
			    		opt_prob=return_max(opt_prob, split_value );
			    		best[t][i][j]=opt_prob;
		    		}
		    	}
			}
		}
		return opt_prob;
	}	
	
	static double return_max(double a, double b, double c){
		
		if(a>b){
			if(a>c){
				return a;
			}else{
				return c;
			}
		}else{
			if(b>c){
				return b;
			}else{
				return c;
			}
		}
	}
	static double return_max(double a, double b){
		if(a>b){
				return a;
		
		}else{
				return b;		
		}
	}

	static void readRules(String rulefile) throws IOException{
	
		FileInputStream fstream = new FileInputStream(rulefile);
		DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;	    
	    while ((strLine = br.readLine()) != null)   {
	    			    
		     String[] result = strLine.trim().split("\\s");
		     for (int x=0; x<result.length; x++){
		         //System.out.println(result[x]);
		     }
		     String lefthand="S";
		     if(result.length > 0){
		    	 lefthand= result[0];
		     }
		     if(result.length > 1){		    	
		    	 double aDouble = Double.parseDouble(result[result.length-1]);
		    	 ArrayList <String> righthand=new ArrayList<String>();
		    	 for (int x=2; x<result.length-2; x++){
		    		 righthand.add(result[x]);		    		 
			     }
		    	 
		    	 boolean found=false;
		    	 Rule temp=null;
		    	 for(int f=0;f <rules.size(); f++){
		    		 temp=rules.get(f);
		    		 if(temp.left_hand.equals(lefthand)){
		    			 found=true;
		    		 }
		    	 }
		    	 if(!found){
			    	 if(righthand.size()==1){
			    		 Rule one=new Rule(lefthand, righthand.get(0),aDouble);
			    		 rules.add(one);
			    	 }else{
			    		 Rule two=new Rule(lefthand, righthand.get(0), righthand.get(1),aDouble);
			    		 rules.add(two);
			    	 }
		    	 }else{
		    		 if(righthand.size()==1){
		    			 temp.updateUnaryRule(righthand.get(0),aDouble);
		    		 }
		    		 else{
		    			 temp.updateBinaryRule(righthand.get(0), righthand.get(1),aDouble);
		    		 }
		    	 }
		     }
	    }
    	System.out.println("number of rules: "+rules.size());

	    for(int a=0;a<rules.size();a++){
	    	
	    	if(rules.get(a).tag && rules.get(a).right_hand_2.size()>0 ){
	    		System.exit(1);
	    	}
	    	System.out.println("=== "+rules.get(a).left_hand);
	    	System.out.println(rules.get(a).tag);
	    	System.out.println(rules.get(a).right_hand_1.toString());
	    	System.out.println(rules.get(a).right_hand_2.toString());
	    	System.out.println(rules.get(a).prob.toString());
	    }
	}
}
