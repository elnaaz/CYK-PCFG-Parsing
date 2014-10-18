import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class BottomUp {

	static ArrayList<Rule> rules=new ArrayList<Rule>(); 
	
	static double [][][] best;
	static int [][][] best_rule;
	static int [][][] best_split_k;
	static String [][][] best_right;
	static String [][][] best_left;
	//static String [][][] best_rules;
	static String [] sentence;
	
	static String startnode="S";
	
	public static void main(String[] args) throws IOException{

		String rulefile="C:\\Users\\Elnaz\\Desktop\\cs544-hw3\\grammar.pcfg";		
		String inputfile="C:\\Users\\Elnaz\\Desktop\\cs544-hw3\\test.txt";		
		String outputfile="test-parsed-bottom-up.pcfg";		

		if (args.length > 0) {
		    try {
		    	rulefile = (args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument must be passed");
		        System.exit(1);
		    }
		}		
		try {

			readRules(rulefile);

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputfile));    		
			//InputStreamReader isr = new InputStreamReader(new FileInputStream(inputfile));
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			String s;
		    while ((s = br.readLine()) != null ){
		 		String input=s;
				double result= cky(input);
				String outline= print_cky_parse(startnode,input, result);
				if(outline.contains("%%%%%%%%")){
					outline="NONE # 0\n";
				}
				System.out.print(outline);
				bufferedWriter.write(outline);	            
	            bufferedWriter.flush();
	            reset();
		    }
		    bufferedWriter.flush();
		    bufferedWriter.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void reset() {
		best= null;
		best_split_k= null;
		best_rule= null;
		best_right= null;
		best_left= null;
	}
	private static String print_cky_parse(String start, String input, double result) {
		// Break Sentence into words
		String[] words = input.trim().split("\\s");
		int start_rule_id=-1;
		for (int t=0; t<rules.size();t++){
			if( rules.get(t).left_hand.equals(start) )
				start_rule_id=t;
		}	
		if(start_rule_id ==-1){
			//System.out.println("bug");
			return "  %%%%%%%%  1";
		}
		// Print the final string result and prob
		//System.out.print(subString(start_rule_id,0,words.length) +" # "+ result +"\n");
		return (subString(start_rule_id,0,words.length) +" # "+ result +"\n");
	}
	
	static String subString(int left_id, int i, int j) {
				
		//System.out.println("Rule_id: "+left_id+" "+rules.get(left_id)+" :( i: "+i+" ,j: "+j+" )");
		//System.out.println("( "+rules.get(left_id).left_hand+"( "+rules.get(left_id).tag+" )"+" ==> "+rules.get(left_id).right_hand_1.size()+" )");
		
		//System.out.println("Rule Info");
		//System.out.println("best split: "+best_split_k[left_id][i][j]);
		//System.out.println("best right: "+best_right[left_id][i][j]);
		//System.out.println("best left: "+best_left[left_id][i][j]);
		//System.out.println("best rule: "+best_rule[left_id][i][j]);
		//System.out.println("best prob: "+best[left_id][i][j]);
		
		if(best[left_id][i][j]==-1){
			//System.out.println("**********"+best[left_id][i][j]);
			//System.out.println("bug best print");
			return "  %%%%%%%%  2";
		}
		if(j==i+1){
			if(! rules.get(left_id).tag){
				//System.out.println("problem");
				return "  %%%%%%%%  3";
			}
			return "("+ rules.get(left_id).left_hand +" "+ (rules.get(left_id)).right_hand_1.get(best_rule[left_id][i][j])+")";
		}
		else{
			int left_index=-1;
			int right_index=-1;
			for(int h=0;h<rules.size();h++){
				if(rules.get(h).left_hand.equals(best_left[left_id][i][j])){
					//System.out.println(rules.get(h).left_hand+"\t==\t"+best_left[left_id][i][j]);
					left_index=h;
				}
				if(rules.get(h).left_hand.equals(best_right[left_id][i][j])){
					//System.out.println(rules.get(h).left_hand+"\t==\t"+best_right[left_id][i][j]);
					right_index=h;
				}
			}
			if(right_index==-1 || left_index==-1){
				//System.out.println("bug no child print");
				return "  %%%%%%%%  4";
			}
			return "("+ rules.get(left_id).left_hand +" "+ subString( left_index , i , 
					best_split_k[left_id][i][j] ) +" "+" "+ subString(  right_index, 
							best_split_k[left_id][i][j] , j) +")";
		}
	}


	static double cky(String inputLine) {
		
		// Break Sentence into words
		String[] words = inputLine.trim().split("\\s");
		sentence =words;
		
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
    	for (int a=0; a<words.length; a++){
	    	String word=words[a].trim();
	    	for (int i=0; i<rules.size();i++){
	    		Rule temp=rules.get(i);
	    		if(temp.tag){
	    			for(int h=0;h<temp.right_hand_1.size();h++){
	    				if( temp.right_hand_1.get(h).equals(word)){
	        				best[i][a][a+1]=temp.prob.get(h);
		    				best_rule[i][a][a+1]=h;
		    				best_right[i][a][a+1]=word;
		    				best_left[i][a][a+1]=word;
		    				best_split_k[i][a][a+1]=a;
	    				}
	    			}
	    		}
	    	}
	    }
    	//System.out.println("The final prob: " + search( 0,words.length));
		return search( 0,words.length);
	}	

	static double search(int index_0, int index_n) {
		//i=0;j=n
		for(int i=index_0; i<index_n;i++ ){			
			for (int t=0; t<rules.size();t++){ // loop over left hand side
				Rule current=rules.get(t);
		    	if(current.tag ){
		    		for(int m=0; m< current.right_hand_1.size();m++){
		    			if(current.right_hand_1.get(m).equals(sentence[i])){
		    					best[t][i][i+1]=current.prob.get(m);
			    	    		best_rule[t][i][i+1]=m;
								best_right[t][i][i+1]=sentence[i];
								best_left[t][i][i+1]=sentence[i];
								best_split_k[t][i][i+1]=i;				
		    			}
		    		}
		    	}
		    }
		}
		
		for(int span=2; span<index_n+1;span++ ){ // for span size 2,3,4,
			//System.out.println("Span size: "+span);
			for (int i=0; i<index_n-span+1;i++){
				int j=i+span;
				double opt_prob=0;
				for(int k=i+1 ; k< j; k++){
					//System.out.println("i: "+i+"  j: "+j+"  k: "+k);
					for (int t=0; t<rules.size();t++){
						Rule current=rules.get(t);
						if(! current.tag ){
							for(int g=0; g<current.right_hand_1.size();g++){
								int right_hand_1_index=-1;
								int right_hand_2_index=-1;
								for(int t2=0; t2<rules.size(); t2++){
									if(rules.get(t2).left_hand.equals(current.right_hand_1.get(g)))
										right_hand_1_index=t2;
									if(rules.get(t2).left_hand.equals(current.right_hand_2.get(g)))
										right_hand_2_index=t2;
								}
								if( right_hand_1_index == -1 || right_hand_2_index ==-1){
									//System.out.println("Bug Bug");
									System.exit(-1);
								}
								if( right_hand_1_index != -1 && right_hand_2_index !=-1){

									if(best[right_hand_1_index][i][k] <0){
										////System.out.println("not valid**1* "+right_hand_1_index+"| "+ i+ " "+k);
										//System.exit(-1);
									}
									
									if(best[right_hand_2_index][k][j] <0){
										////System.out.println("not valid**2* "+right_hand_2_index+"| "+ k+ " "+j);
										//System.exit(-1);
									}

									if(best[right_hand_1_index][i][k] > 0 && best[right_hand_2_index][k][j] >0){
										
										//System.out.println("Valid   :"+i+" | "+ k+ " "+j);
										double split_value=best[right_hand_1_index][i][k]*best[right_hand_2_index][k][j]*current.prob.get(g);
										if(split_value>best[t][i][j]){
											best[t][i][j]=split_value;//return_max(best[t][i][j], split_value);
											best_rule[t][i][j]=g;
											best_right[t][i][j]=current.right_hand_2.get(g);
											best_left[t][i][j]=current.right_hand_1.get(g);
											best_split_k[t][i][j]=k;
										}
									}
									
								}
							}
						}
					}
				}
			}
		}
		
		int index=0;
		for(int t2=0; t2<rules.size(); t2++){
			if(best[t2][index_0][index_n]>0){
				index=t2;
			}
		}		
		return best[index][index_0][index_n];		
	}	

	static void readRules(String rulefile) throws IOException{	
		FileInputStream fstream = new FileInputStream(rulefile);
		DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;	    
	    while ((strLine = br.readLine()) != null)   {	    			    
		     String[] result = strLine.trim().split("\\s");
		     for (int x=0; x<result.length; x++){
		         ////System.out.println(result[x]);
		     }
		     String lefthand="S";
		     if(result.length ==1 ){
		    	 lefthand= result[0];
		    	 startnode= result[0];
		     }
		     if(result.length > 1){	
		    	 lefthand= result[0];
//		    	 double aDouble = Math.abs((-1 )* Math.log(Double.parseDouble(result[result.length-1])));
		    	 double aDouble = (Double.parseDouble(result[result.length-1]));
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
	    //System.out.println("number of rules: "+rules.size());
	    for(int a=0;a<rules.size();a++){	    	
	    	if(rules.get(a).tag && rules.get(a).right_hand_2.size()>0 ){
	    		System.exit(1);
	    	}
	    	//System.out.println("=== "+rules.get(a).left_hand);
	    	//System.out.println(rules.get(a).tag);
	    	//System.out.println(rules.get(a).right_hand_1.toString());
	    	//System.out.println(rules.get(a).right_hand_2.toString());
	    	//System.out.println(rules.get(a).prob.toString());
	    }
	}
}
