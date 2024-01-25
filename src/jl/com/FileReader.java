package jl.com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * /**
 * This is a program that reads
 * multiple files (csv, txt, sql) 
 * and has functions such as
 * checking and removing blank
 * lines, count number of lines, 
 * find and replace word and
 * generate modified version of file
 *
 * @author jr.pastorin
 *
 */
public class FileReader extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	// list of the absolute path of selected files within the folder
	private static ArrayList<File> folderFiles = new ArrayList<File>();
	
	// get the number of user selected files or folder (txt, sql, csv)
	private static File filesSelected[] = new File[folderFiles.size()];
	
	// mapping of find and replace word
	private static LinkedHashMap<String,String> wordMapping = new LinkedHashMap<String,String>();
	
	// Jlabel to show the files user selects
    private static JLabel labelFileSelected;
    
    // List of string combining fnd and replace word for display purposes
    private static JList<String> labelMapList;
    
    // List of String values that user select in Jlist
    private static List<String> selectedInJList;
    
    // JTextField that prompts the find word to user 
    private static JTextField findWordText;
    
    // JTextField that prompts the replace word to user 
    private static JTextField replaceWordText;
    
    /**
     * Run the file reader tool
     * @throws IOException
     */
	public void runFileReader() throws IOException {
		showAndCreateGUI();
	}
	
	/**
	 * Show and create GUI
	 */
	private void showAndCreateGUI() {
		 // frame to contains GUI elements
        JFrame f = new JFrame("File Reader Tool");
 
        // set the size of the frame
        f.setSize(400, 600);
 
        // set the frame's visibility
        f.setVisible(true);
 
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // button to open save dialog
        JButton btnRun = new JButton("Run File Reader");
        btnRun.setBounds(200, 20, 165, 40);
        
        // button to open open dialog
        JButton btnChooseFile = new JButton("Choose File/Folder");
        btnChooseFile.setBounds(10, 20, 165, 40);
        
        // find word label
        JLabel findWordLabel = new JLabel("Find : ");
        findWordLabel.setBounds(10, 100, 80, 25);
        
        // find word field
        findWordText = new JTextField(25);
        findWordText.setBounds(100, 100, 165, 25);
        
        // replace word label
        JLabel replaceWordLabel = new JLabel("Replace : ");
        replaceWordLabel.setBounds(10, 130, 80, 25);
        
        // find word field
        replaceWordText = new JTextField(25);
        replaceWordText.setBounds(100, 130, 165, 25);
        
        // button to add find and replace word
        JButton btnAdd = new JButton("add");
        btnAdd.setBounds(100, 160, 165, 30);
        
        // button to clear values
        JButton btnClear = new JButton("clear");
        btnClear.setBounds(20, 430, 165, 30);
        
        // button to remove item in JList
        JButton btnRemove = new JButton("remove");
        btnRemove.setBounds(200, 430, 165, 30);
        
        //Map list 
        labelMapList = new JList<String>();
        labelMapList.setBounds(40,220,300,200);
        
        // set the label to its initial value
        labelFileSelected = new JLabel("no file selected");
        labelFileSelected.setBounds(10, 60, 500, 30);
        // make an object of the class FileReader
        FileReader f1 = new FileReader();
 
        // add action listener to the button to capture user
        // response on buttons
        btnRun.addActionListener(f1);
        btnChooseFile.addActionListener(f1);
        btnAdd.addActionListener(f1);
        btnClear.addActionListener(f1);
        btnRemove.addActionListener(f1);
        
        // Gets the selected values in the JList
        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
            	selectedInJList = labelMapList.getSelectedValuesList();
            }
        };
      
        labelMapList.addListSelectionListener(listSelectionListener);
        
        // make a panel to add the buttons and labels
        JPanel p = new JPanel(null);
        p.setBorder(new EmptyBorder(5,5,5,5));
        
        // add buttons to the frame
        p.add(btnChooseFile);
        p.add(btnRun);
       
       
        //add text fields to the frame
        p.add(findWordLabel);
        p.add(findWordText);
        p.add(replaceWordLabel);
        p.add(replaceWordText);
        p.add(btnAdd);
        
        p.add(labelFileSelected);
        p.add(labelMapList);
        p.add(btnClear);
        p.add(btnRemove);
        
        // add panel to the frame
        f.add(p);
 
        f.setVisible(true);
	}
	
	/**
	 * Generates the log file and modified version of the file
	 * 
	 * @throws IOException
	 */
	private void generateOutputFile() throws IOException {
		boolean success = false;
		
		File outputPath = null;
		
		if(folderFiles.size() > 0) {
			for (File selectedFile : folderFiles) {
				String inputFileName = selectedFile.getName();
				outputPath = getOutputDirectory(selectedFile);
				success = createOutputFile(selectedFile, outputPath, inputFileName);
			}
		
			if(success) {
				System.out.println("Output files generated successfully.");
				showMessageDialog(null, "Output files generated successfully.");
				
				Desktop desktop = Desktop.getDesktop();
		       
		        try {
		            //dirToOpen
		            //desktop.open(getOutputDirectory(outputPath));
		        } catch (IllegalArgumentException iae) {
		            System.out.println("File Not Found");
		        }
			}
		} else {
			showMessageDialog(null, "No files selected");
		}
	}
	
	/**
	 * Generate the modified version of the file and log file
	 * for each instance of the file selected
	 * 
	 * @param inputFilePath 	source file path
	 * @param outputFilePath	output file path 
	 * @param inputFileName		file name with extension
	 * @return					true if the output file is generated successfully, otherwise false
	 * @throws IOException 
	 */
	private boolean createOutputFile(File inputFilePath, File outputFilePath, 
			String inputFileName) throws IOException {
		
		String fileNameNoExt = getFileNameNoExtension(inputFileName);
		String fileExtension = getFileExtension(inputFileName);

		String outputFileName = outputFilePath.getPath().concat("\\").concat(fileNameNoExt);
		File outputFileNameAbsoulutePath = new File(outputFileName.concat("_out.").concat(fileExtension));
		File logOutputFilePath = new File(outputFileName.concat(".log"));
		
		try {
			// create directory for the output folder
			// if non-existent
			createDirectory(outputFilePath);
		
			Scanner scan = new Scanner(inputFilePath); // Scanner object to read each line in the file
			String fileContent = ""; // stores the value of modified version of file
			long lineCount = 0; // stores the value of total line count of file
			long blankLines = 0; // stores the value of total blank lines in file
			
			while(scan.hasNextLine()) {
				 // get total line count
				 lineCount++;
				 
				 String line = scan.nextLine();
				 if(line.isBlank()) {
					// get total blank line
					blankLines++;
				 } else {
					 // helps skip blank lines 
					 // lines which are not blank lines are concatenated 
					 // and separated by new line
					 fileContent = fileContent.concat(line + "\n");
				 }
				 
			}
			scan.close();
			
			// writes and generate the modified version of file
			FileWriter writer = new FileWriter(outputFileNameAbsoulutePath);
			
			// replace the specific find word
			//String modifiedContent = fileContent.replace(foundWord,replacementWord);
			String modifiedContent = replaceMultiple(fileContent, wordMapping);
			writer.write(modifiedContent);
			writer.close();
			
			// writes and generate the log output file
			FileWriter logWriter = new FileWriter(logOutputFilePath);
			logWriter.write("Line Count: " + lineCount + "\n");
			logWriter.write("Blank Lines: " + blankLines + "\n");
			logWriter.write("Found Word/s: \n");
			
			// find the specific word and write it to log file
			boolean isWordFound = false; 
			ArrayList<String> findWordList = new ArrayList<String>();
			findWordList = getAllFindWords(wordMapping);
			
			if(findWordList != null) {
				for (String word : findWordList){
					isWordFound = isContainKeyWord(fileContent, word);
					if(isWordFound) {
						logWriter.write(word + "\n");
					}
					
				}
			}

			logWriter.close();
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		
	}
	
	/**
	 * Check if the given target source contains the key word (exactly match)
	 * @param source    the target String
	 * @param keyToFind	key to find in the target
	 * @return true if key exist in the target, otherwise false
	 */
	private boolean isContainKeyWord(String source, String keyToFind){
        String pattern = "\\b"+keyToFind+"\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
   }
	
	/**
	 * Get the key value pair of the LinkedHashMap 
	 * and convert to Array of String
	 * 
	 * @param map 	the map containing the key value pair of find and replace word
	 * @return array of String containing the concatenated output of key and value pair 
	 */
	private String[] getListKeyValuePair(LinkedHashMap<String, String> map) {
		ArrayList<String> listEntry = new ArrayList<String>();
		
		for (Map.Entry<String, String> m : map.entrySet()) {
			listEntry.add(m.getKey() + "  ->  " + m.getValue());
        }
		
		String[] arrayEntry = new String[listEntry.size()];
		arrayEntry = listEntry.toArray(arrayEntry);
        
		return arrayEntry;
	}
	
	/**
	 * Get all the keys of the Map
	 * 
	 * @param map 	with the value pair of find and replace
	 * @return ArrayList of the find words (key)
	 */
	private ArrayList<String> getAllFindWords(Map<String, String> map) {
		if(map.isEmpty()) {
			return null;
		}
		
		ArrayList<String> findWords = new ArrayList<String>();
		
		for (Map.Entry<String, String> m : map.entrySet()) {
			findWords.add(m.getKey());
        }
		
		return findWords;
	}
	
	/**
	 * Get the filename of the file without the extension
	 * 
	 * @param filePath 	string value of file path
	 * @return filename without extension
	 */
	private String getFileNameNoExtension(String filePath) {
		int index = filePath.lastIndexOf(".");
		String fileNameNoExt = filePath.substring(0, index);
		return fileNameNoExt;
	}
	
	/**
	 * Get the extension of the file
	 * 
	 * @param filePath 	the path of the file
	 * @return 			file extension 
	 */
	private String getFileExtension(String filePath) {
		int index = filePath.lastIndexOf(".");
		String fileExtension = filePath.substring(index + 1);
		return fileExtension;
	}
	
	/**
	 * Get the output directory where output files will be located
	 * An output folder will be created after the parent file of the text file
	 * 
	 * @param sourceFile the input file path
	 * @return output directory with type File
	 */
	private File getOutputDirectory(File sourceFile) {
		String parent = sourceFile.getParentFile().toString();
		File outputFilePath = new File(parent.concat("\\output"));
		return outputFilePath;
	}
	
	/**
	 * Create directory if not existing 
	 * @param file the path of the file to be created
	 */
	private void createDirectory(File file) {
		if (! file.exists()){
			file.mkdir();
	    }
	}
	
	/**
	 * Create mapping between the word user needs to find and the word to replace
	 * 
	 * @param strFind		string that user need to find in the file
	 * @param strReplace	string that user will use to replace the found word
	 * @return mapping of Find and Replace word
	 */
	private LinkedHashMap<String, String> createMap(String strFind, String strReplace) {
		LinkedHashMap<String,String> mapFindAndReplace = new LinkedHashMap<String, String>();
	    mapFindAndReplace.put(strFind, strReplace);
		return mapFindAndReplace;
	}
	
	/**
	 * Adds a LinkedHashMap in the ArrayList<LinkedHashMap<String , String>>
	 * 
	 * @param map 	LinkedHashMap to add in the array list
	 * @return updated ArrayList
	 */
	private ArrayList<LinkedHashMap<String , String>> addMapToList(LinkedHashMap<String, String> map) {
		ArrayList<LinkedHashMap<String , String>> mapList = new ArrayList<LinkedHashMap<String,String>>();
		mapList.add(map);
	    return mapList;
	}
	
	/**
	 * Performs simultaneous search/replace of multiple strings. Case Sensitive!
	 */
	public String replaceMultiple(String target, Map<String, String> replacements) {
	  return replaceMultiple(target, replacements, true);
	}

	/**
	 * Performs simultaneous search/replace of multiple strings.
	 * 
	 * @param target        string to perform replacements on.
	 * @param replacements  map where key represents value to search for, and value represents replacem
	 * @param caseSensitive whether or not the search is case-sensitive.
	 * @return replaced string
	 */
	public String replaceMultiple(String target, Map<String, String> replacements, boolean caseSensitive) {
	  if(target == null || "".equals(target) || replacements == null || replacements.size() == 0)
	    return target;

	  //if we are doing case-insensitive replacements, we need to make the map 
	  //case-insensitive--make a new map with all-lower-case keys
	  if(!caseSensitive) {
	    Map<String, String> altReplacements = new LinkedHashMap<String, String>(replacements.size());
	    for(String key : replacements.keySet())
	      altReplacements.put(key.toLowerCase(), replacements.get(key));

	    replacements = altReplacements;
	  }
	  
	  // for each 'find' word in the target, replace it with the 'replace' word
	  //String replacedString = "";
	  for(String key : replacements.keySet()) {
		  target = target.replaceAll("\\b" + key + "\\b", replacements.get(key)); 
	  }
	  
	  return target;

	}
	
	private void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
	            //listFilesForFolder(fileEntry);
	        } else {
	            folderFiles.add(new File(fileEntry.getAbsolutePath()));
	        }
	    }
	}
	
	
	/**
	 * An action Listener for the buttons
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// if the user presses the save button show the save dialog
        String com = e.getActionCommand();
 
        if (com.equals("Run File Reader")) {
        	try {
        		generateOutputFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }  
        else if (com.equals("add")) {
        	
        	String findWord = findWordText.getText();
        	String replaceWord = replaceWordText.getText();
        	
        	LinkedHashMap<String,String> myMap1 = new LinkedHashMap<String, String>();
        	if(findWord != "") {
        		myMap1 = createMap(findWord,replaceWord);
        	}
        	
        	ArrayList<LinkedHashMap<String , String>> mapList = addMapToList(myMap1);
        	
        	for (int i = 0; i < mapList.size(); i++) {
	 	    	wordMapping.putAll(mapList.get(i)); 
    	    }
        	
        	//set the values of wordMapping to Jlist
        	labelMapList.setListData(getListKeyValuePair(wordMapping));
        	
        	//clear fields
        	findWordText.setText("");
        	replaceWordText.setText("");
        	
        }
        
        else if (com.equals("clear")) {
        	
        	//clear fields, value of mapping and selected directory
        	findWordText.setText("");
        	replaceWordText.setText("");
        	
        	wordMapping.clear();
        	labelMapList.setListData(getListKeyValuePair(wordMapping));
        	
        	filesSelected = null;
        	labelFileSelected.setText("no file selected");
        }
        
        else if (com.equals("remove")) {
        	
        	Object key;
        	if(selectedInJList != null) {
        		//remove selected item in Jlist
            	for (int i = 0; i < selectedInJList.size(); i++) {
                    key = selectedInJList.get(i).split(" ")[0];
                    wordMapping.remove(key);
                }
        	}
        	
        	// set the udpated List Data
        	labelMapList.setListData(getListKeyValuePair(wordMapping));
        	
        }
        
        // if the user presses the open dialog show the open dialog
        else if (com.equals("Choose File/Folder")) {
            // create an object of JFileChooser class
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            
            // set the selection mode to directories and files
            j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            
            // allow multiple file selection
            j.setMultiSelectionEnabled(true);
            
            //filter files to accept only csv, sql, txt
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "csv", "sql");
            j.setFileFilter(filter);
            
            // invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);
 
            if (r == JFileChooser.APPROVE_OPTION) {
            	
                // get the Selected files           	
            	int numberOfFiles = j.getSelectedFiles().length;
            	//filesSelected = new File[folderFiles.size()];
            	filesSelected = j.getSelectedFiles();
            	
                // set text to blank
                labelFileSelected.setText("");
                
                if(filesSelected == null) {
                	labelFileSelected.setText("no file selected");
                }
            	
                int t = 0;
                // set the label to the path of the selected files
                
                
                while (t++ < filesSelected.length) {
                	File file = new File(filesSelected[t - 1].getAbsolutePath());
                	if(! file.isDirectory()) {
                		//filesSelected should be array list instead of array 
                		labelFileSelected.setText(labelFileSelected.getText() + " " + filesSelected[t - 1].getName() + ", ");
                		folderFiles.add(file);
                	} else {
                		//get the folder path
                		File folderPath = new File(filesSelected[t - 1].getAbsolutePath());
                		labelFileSelected.setText(labelFileSelected.getText() + " " + filesSelected[t - 1].getName() + ", ");
                		
                		listFilesForFolder(folderPath);
                	}	
                	
                }
                
                labelFileSelected.setText(labelFileSelected.getText().substring(0,labelFileSelected.getText().length() - 2));
                
                System.out.println("Selected files:");
                for(File file : folderFiles ) {
                	System.out.println(file);
                }
                    
            }
            // if the user cancelled the operation
            else
                labelFileSelected.setText("the user cancelled the operation");
        } else {
        	System.out.println("Error: no available command");
        }
    }
	


}

