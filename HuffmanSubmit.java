import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
// Import any package as required
public class HuffmanSubmit implements Huffman {
	//Object class for the node in the binary tree
	public static class BTNode implements Comparable<BTNode>{
		int frequency;//time of teh character appears from the input file
		char name;//name of the character
		BTNode left;//left child
		BTNode right;//right child
		//Constructor of binary tree node with the frequency of the character that this node holds, the name
		//of the character, left child and right child of this node.
		public BTNode(int item, char charname, BTNode left, BTNode right){
			this.frequency = item;
			this.name = charname;
			this.left = left;
			this.right = right;
		}
		//Override the compareTo method so I can directly use priority queue with BTNode
		@Override
		public int compareTo(BTNode o) {
			// TODO Auto-generated method stub
			return frequency - o.frequency;
		}
	}
	//count the number of appearance of each character from the input file
	public static void counter(String inputFile, String outputFile,HashMap<Character,Integer> map) {
		BinaryIn reader = new BinaryIn(inputFile);
		while(!reader.isEmpty()) {
			char st= reader.readChar();
			if(map.containsKey(st)) {
				int s=map.get(st);
				map.put(st, s+1);
			}else{
				map.put(st, 1);
			}
		}
	}
	//create a huffman tree based on the map that stores the character and it's frequency
	static BTNode createtree( HashMap<Character, Integer> map) {
		PriorityQueue<BTNode> haffmanTree = new PriorityQueue<BTNode>();
		for (char k : map.keySet()) {
			BTNode node= new BTNode(map.get(k), k, null, null);
			haffmanTree.offer(node);//add each node contains character and it's frequency to the priority frequency
		}
		while(haffmanTree.size()>1) {
			BTNode smallest = haffmanTree.poll();
			BTNode secondsmallest = haffmanTree.poll();
			//put smallest one on the left and secondsmallest on the right 
			BTNode newnode = new BTNode(smallest.frequency + secondsmallest.frequency, '\0' , smallest, secondsmallest );
			haffmanTree.offer(newnode);
		}
		return haffmanTree.poll();//return the root of the huffman tree
	}
	public static boolean isLeaf(BTNode o) {//method to checj if the node is the leaf, aka there is not left and right child
		if(o.left==null&&o.right==null) {
			return true;
		}
		return false;
	}
	//assign 0 or 1 to the huffman tree
	private static void assignhaffmanbits(BTNode root, HashMap<Character, String> chartobi, String bi){
		if(!isLeaf(root)){//call the method recursively to assign 0 on left side and 1 on right side 
			assignhaffmanbits(root.left, chartobi, bi+"0");
			assignhaffmanbits(root.right, chartobi,bi+"1");
		}else {
			chartobi.put(root.name, bi);
		}
	} 
	//print the character in 8 digits binary form with it's frequency
	private static void printFrequency(HashMap<Character,Integer>map, String freqFile){
		BinaryOut out=new BinaryOut(freqFile);
		for(char c:map.keySet()) {
			out.write(charToBinary((int)c)+":"+map.get(c)+"\n");
		}
		out.close();
	}
	//convert a character to 8 digit binary representation
	private static String charToBinary(int number) {
		StringBuilder builder = new StringBuilder();
		while (number > 0) {
			builder.append(number % 2);
			number/=2;
		}
		String binary= builder.reverse().toString();
		//adding zero in the front if the length of character's binary representation is smaller than 8
		while(binary.length()<8) {
			binary=0+binary;
		}
		return binary;		
	}
	//method to print the corresponding huffman code of each character according to the original input
	public static void printEncoded(String inputFile, String outputFile ,  HashMap<Character,String> map, BTNode root) throws Exception {
		BinaryOut out = new BinaryOut(outputFile);
		assignhaffmanbits( root, map, "");//assign 0s and 1s to the tree
		BinaryIn reader = new BinaryIn(inputFile);
		while(!reader.isEmpty()) {
			char c= reader.readChar();
			for (int i = 0; i<map.get(c).length(); i++) {
				if(map.get(c).charAt(i)=='0') {//write each bit as a boolean into the output file
					out.write(true);
				}else if(map.get(c).charAt(i)=='1') {
					out.write(false);
				}
			}
		}
		out.flush();
	}
	//method to compress the input file to an encoded file
	public void encode(String inputFile, String outputFile, String freqFile) {
		HashMap<Character,Integer> map=new HashMap<Character, Integer>();//map to store the frequency of the character
		HashMap<Character, String> chartobi = new HashMap<Character, String>();//map to store the huffman value of teh character
		counter(inputFile,outputFile, map);
		BTNode root = createtree(map);
		try {
			//print the frequency file and output file
			printFrequency(map, freqFile);
			printEncoded(inputFile, outputFile,  chartobi, root);
		} catch (Exception e) {
			e.printStackTrace();
		}					
	}
	//method to decode the encoded file to an decoded output file
	public void decode(String inputFile, String outputFile, String freqFile) {
		HashMap<Character, Integer>map=new HashMap<Character, Integer>();//store the frequency of the character
		BinaryIn in =new BinaryIn(inputFile);
		BinaryOut out=new BinaryOut(outputFile);
		String[] s;//string array to store the character as the first index and frequency as the second index
		BufferedReader reader;//buffered reader to reader line by line from the input file
		try {
			reader=new BufferedReader(new FileReader(freqFile));
			String current;//string to store current line in from the input file as a whole
			while((current=reader.readLine())!=null) {
				s=current.split(":");//split the current line by ":" so the first index is the name of character in binary
									//representation and the second index is the frequency
				char c = (char) Integer.parseInt(s[0],2);//convert the binary representation to an actual literal character and store it			
				int d = Integer.parseInt(s[1]);
				map.put(c, d);//put the literal character and its frequency in the map 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		BTNode root = createtree(map);//create a huffman tree with character names and frequency
		BTNode position = root; //initialize the temporary pointer in the binary tree as the root
		while(!in.isEmpty()){
			boolean zero = in.readBoolean();//read each bit as a boolean of either 1 or 0
			if(isLeaf(position)){//write the character to the output file whenever the pointer gets to the leaf
				out.write(position.name);
				out.flush();
				position=root;//reset the pointer to root whenever it reaches one leaf	           
			}
			if(zero){//move to right if the bit is 0
				position=position.left;
			}
			else if(!zero){//move to left if the bit is 1
				position=position.right;
			}
		}
	}
	public static void main(String[] args) {
		Huffman huffman = new HuffmanSubmit();
		huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");

		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same.
	}
}



