# Huffman_Compress-Decompress_Project
Name: Shuran Zhang
Class ID: 122
NetID: szhang73
Email: szhang73@u.rochester.edu

Submitted File:
    HuffmanSubmit.java: included all methods and code for this project.

I have a main method in my HuffmanSubmit.java, which contains exactly the same thing with the sample file that I download from the instruction PDF. My main method runs and tests the encoding from ur.jpg to ur.enc and the decoding from ur.enc to ur_dec.jpg. My main method looks like:
public static void main(String[] args) {
		Huffman huffman = new HuffmanSubmit();
		huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");

		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same.
	}
which is exactly the same with the online tar file that Prof.Biswas gave us. I'm not sure if we are supposed to include a main method so I just keep the same thing. I didn't include the testing main method for the alice30.txt files. Please let me know if this is ok or not. Thanks!




 
