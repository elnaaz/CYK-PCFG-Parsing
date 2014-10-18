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


public class Main {


	static ArrayList<Double> rule_probablity=new ArrayList<Double>(); 
	static ArrayList<ArrayList<String>> rule_righthand=new ArrayList<ArrayList<String>>(); 
	static ArrayList<String> rule_lefthand=new ArrayList<String>(); 
	static ArrayList<Boolean> rule_is_tag=new ArrayList<Boolean>(); 
	
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

//		System.out.println(sentence.toString());
		for (int t=0; t<rule_lefthand.size();t++){
//			System.out.println();
			for (int i=0; i<sentence.length+1;i++){
	//			System.out.println();
				for (int j=0; j<sentence.length+1;j++)
		//			System.out.print("  | "+best[t][i][j]);
					;
	    	}
		}	
	}

	private static void print_cky_parse(String start, String input) {

		// Break Sentence into words
		String[] words = input.trim().split("\\s");
		
		int start_rule_id=-1;

		for (int i=0; i<rule_righthand.size();i++){
			if(rule_righthand.get(i).size()==1 && 
	    				(rule_lefthand.get(i)).equals(start)){
							start_rule_id=i;
							break;
	    		}
	    }
		start_rule_id=0;
				
		System.out.println("***");
		System.out.println(subString(start_rule_id,0,words.length));
		
	}
	
	static String subString(int rule_id, int i, int j) {
				
		System.out.println("rule_id: "+rule_id+"\ti: "+i+"\tj: "+j);
		System.out.println("( "+rule_lefthand.get(rule_id)+" , "+i+" , "+j+" )");
		if(j==i+1){
				return "( "+ rule_lefthand.get(rule_id) +" "+ rule_righthand.get(rule_id).get(0)+" )";
		}
		else{
			System.out.println();
			System.out.println("best split: "+best_split_k[rule_id][i][j]);
			System.out.println("best right: "+best_right[rule_id][i][j]);
			System.out.println("best left: "+best_left[rule_id][i][j]);
			System.out.println("best rule: "+best_rule[rule_id][i][j]);
			System.out.println("best prob: "+best[rule_id][i][j]);
			return "( "+ rule_lefthand.get(rule_id) +" ("+ subString( best_rule[rule_id][i][j] , i , best_split_k[rule_id][i][j] ) +" )"+" ("+ subString( best_rule[rule_id][i][j] , best_split_k[rule_id][i][j] , j) +" )";
		}
	}

	static double cky(String start , String inputLine ) {
		
		// Break Sentence into words
		String[] words = inputLine.trim().split("\\s");
		sentence =words;
		System.out.println(sentence);		
	    for (int x=0; x<words.length; x++){
	         System.out.print(words[x]);
	     }
	    System.out.println();
	    
	    // Make the dynamic table for the words
		best= new double [rule_lefthand.size()][words.length+1][words.length+1];
		best_split_k= new int [rule_lefthand.size()][words.length+1][words.length+1];
		best_rule= new int [rule_lefthand.size()][words.length+1][words.length+1];
		best_right= new String [rule_lefthand.size()][words.length+1][words.length+1];
		best_left= new String [rule_lefthand.size()][words.length+1][words.length+1];		
		//best_rules= new String [rule_lefthand.size()][words.length+1][words.length+1];
		
		for (int t=0; t<rule_lefthand.size();t++){
			for (int i=0; i<words.length+1;i++){
				for (int j=0; j<words.length+1;j++){
					best[t][i][j]=-1;
					best_split_k[t][i][j]=-1;
					best_rule[t][i][j]=-1;
					best_right[t][i][j]="";
					best_left[t][i][j]="";
					//best_rules[t][i][j]="";
				}
	    	}
		}
		 // Fill up for the word substitution
    	for (int a=0; a<words.length; a++){
	    	String word=words[a].trim();
	    	for (int i=0; i<rule_righthand.size();i++){
	    		if(rule_is_tag.get(i)) 
	    				if(rule_righthand.get(i).get(0).equals(word)){
	    					best[i][a][a+1]=rule_probablity.get(i);
	    					best_rule[i][a][a+1]=i;
	    					best_right[i][a][a+1]="";
	    					best_left[i][a][a+1]="";
	    					best_split_k[i][a][a+1]=-7;
	    		}else{
	    			best[i][a][a+1]=0;
					best_rule[i][a][a+1]=i;
					best_right[i][a][a+1]="";
					best_left[i][a][a+1]="";
					best_split_k[i][a][a+1]=-7;
	    		}
	    	}
	    }
    	System.out.println(search("S", 0,words.length));
		return search(start, 0,words.length);
	}	

	
	static double search(String rule, int i, int j) {
		
		System.out.println("Search for Rule: "+ rule + " between\t\ti: "+i+"\tj: "+j);
		System.out.println("( "+rule+" , "+i+" , "+j+" )");
		
		if(j==i+1){
			for (int t=0; t<rule_lefthand.size();t++){
		    	if(rule_lefthand.get(t).equals(rule) && rule_righthand.get(t).size()==1 
		    			&& rule_righthand.get(t).get(0).equals(sentence[i])){
		    		//System.out.println("word found: "+sentence[i] + " : "+ best[t][i][j]);
		    		
		    		System.out.println("( "+t+" , "+i+" , "+j+" )");
					best_rule[t][i][j]=t;
					//best_rules[t][i][j]="( "+rule_lefthand.get(t)+" "+best_rules[t][i][k]+" "+best_rules[t][i][k]+")";
					best_right[t][i][j]="";
					best_left[t][i][j]="";
					best_split_k[t][i][j]=i;
					
					return best[t][i][j];
		    	}
			}
		}
		
		int count_usale_rules=0;
		ArrayList<Integer> usable_rules=new ArrayList<Integer>();
		
		double temp_best=-2;
		int temp_best_rule=-1;
		String temp_best_right="";
		String temp_best_left="";
		int temp_best_Split_k=-3;
		
		for (int t=0; t<rule_lefthand.size();t++){
				if(rule_lefthand.get(t).equals(rule) && rule_righthand.get(t).size()==2 
						&& best[t][i][j] != -1){
					//to_be_returned=best[t][i][j];
					count_usale_rules++;
					usable_rules.add(t);
					if(best[t][i][j]>temp_best){
						temp_best= best[t][i][j];
						temp_best_rule= best_rule[t][i][j];
						temp_best_right=best_right[t][i][j];
						temp_best_left=best_left[t][i][j];
						temp_best_Split_k=best_split_k[t][i][j];
					}
		    	}	
		}
		if(usable_rules.size()>0){
			for (int t=0; t<rule_lefthand.size();t++){
				if(rule_lefthand.get(t).equals(rule) && rule_righthand.get(t).size()==2 
						&& best[t][i][j] != -1){
						best[t][i][j]=temp_best;
						best_rule[t][i][j]=temp_best_rule;
						best_right[t][i][j]=temp_best_right;
						best_left[t][i][j]=temp_best_left;
						best_split_k[t][i][j]=temp_best_Split_k;
		    	}	
			}

		}
		
		double opt_prob=0;
		
		for(int k=i+1;k<j;k++){
			for (int t=0; t<rule_lefthand.size();t++){
		    	if(rule_lefthand.get(t).equals(rule) && rule_righthand.get(t).size()==2){		    		

		    		System.out.println(rule+" * * * k: "+ k + "* * * t: "+ t + "\t\ti: "+i+"\tj: "+j);
		    		System.out.println("Rule: "+ rule_lefthand.get(t) + "\t\t "+rule_righthand.get(t).toString());
		    		
		    		double split_value=search( rule_righthand.get(t).get(0), i,k)*search( rule_righthand.get(t).get(1), k,j)*rule_probablity.get(t);
		    		
		    		if(split_value>opt_prob){
						best[t][i][j]=split_value;
						best_rule[t][i][j]=t;
						//best_rules[t][i][j]="( "+rule_lefthand.get(t)+" "+best_rules[t][i][k]+" "+best_rules[t][i][k]+")";
    					best_right[t][i][j]=rule_righthand.get(t).get(0);
    					best_left[t][i][j]=rule_righthand.get(t).get(1);
    					best_split_k[t][i][j]=k;    		
		    		}
		    		opt_prob=return_max(opt_prob, search( rule_righthand.get(t).get(0), i,k)*search( rule_righthand.get(t).get(1), k,j)*rule_probablity.get(t) );
		    		best[t][i][j]=opt_prob;
		    		//best_rules[t][i][j]="( "+rule_lefthand.get(t)+" "+best_rules[t][i][k]+" "+best_rules[t][i][k]+")";
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
		         System.out.println(result[x]);
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
		    	 rule_righthand.add( righthand);
		    	 rule_lefthand.add( lefthand);
		    	 rule_probablity.add(aDouble);
		     }
	    }
	    for (int i=0; i<rule_righthand.size();i++){
    		if(rule_righthand.get(i).size()==1){
    			rule_is_tag.add((true));
    		}else{
    			rule_is_tag.add(false);
    		}
    	}
	    System.out.println(rule_lefthand.toString());
	    System.out.println(rule_righthand.toString());
		System.out.println(rule_probablity.toString());
		System.out.println(rule_is_tag.toString());
	}
}
