import de.membrainminusnn.*; // Include MemBrain remote connect lib

/**
 * @author Alexey Munishkin
 *   
 *   Simple java test code to check functionality of MemBrain's java
 *   API for connecting and reading data from BlackJack_RELU Neural Net(s)
 */
public class MemBrain_BlackJack_Main {

	/*
	 * void main(String[] args);
	 * 
	 *   Main executable method that loads XOR.mdl file into MemBrain,
	 *   then reads information about network structure, does a
	 *   simple training, and reads output for validation.
	 */
	public static void main(String[] args) {
		
		// Read and print current wrapper version
		String version = MBDllWrapper.GetWrapperVersionInfo();
		System.out.println(version);
		
		// Set path to Neural Net files
		String path = "C:\\Users\\munis\\Documents\\_code\\EmbeddedBlackjack\\"; 
		
		
		System.out.println();
		//-----------------------------------------------------------
		// Load Neural Net *.mdl file
		String file_nn = (path + "MemBrain-DeepRL\\MemBrain_ANNs\\BlackJack_RELU+delay2out_layer1.mbn");
		MBDllWrapper.MBLoadNet(file_nn);
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("SUCCESS: net <" + file_nn + "> loaded");
		} else {
			System.out.println("ERROR: loading net <" + file_nn + ">");
		}
		
		// Read information about total number Neurons
		int total_nn = MBDllWrapper.MBGetNetCount();
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("Total Net number = " + total_nn);
		} else {
			System.out.println("ERROR: cannot read neurons");
		}
		
		// Read information about input Neurons
		int input_nn = MBDllWrapper.MBGetInputCount();
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("Input Neural Net number = " + input_nn);
		} else {
			System.out.println("ERROR: cannot read input neurons");
		}
		
		// Read information about output Neurons
		int output_nn = MBDllWrapper.MBGetOutputCount();
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("Output Neural Net number = " + output_nn);
		} else {
			System.out.println("ERROR: cannot read output neurons");
		}
		
		
		System.out.println();
		//-----------------------------------------------------------
		// Load training data *.csv from Blackjack-Sim
		String file_lesson = (path + "Blackjack-Simulator\\MB_data_in\\MB_BlackJack.csv"); 
		MBDllWrapper.MBSetLessonInputCount(input_nn);	// set input count
		MBDllWrapper.MBSetLessonOutputCount(output_nn);	// set output count
		MBDllWrapper.MBImportLessonRaw(file_lesson); 
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("SUCCESS: <" + file_lesson + "> loaded");
		} else {
			System.out.println("ERROR: loading <" + file_lesson + ">");
		}
		
		// Simple print tests to check if correct lesson
		System.out.println("Patterns  = " + MBDllWrapper.MBGetLessonSize());
		System.out.println("Input num = " + MBDllWrapper.MBGetLessonInputCount());
		System.out.println("Output num= " + MBDllWrapper.MBGetLessonOutputCount());
		
		
		System.out.println();
		//-----------------------------------------------------------
		// Load teacher trainer *.mbt
		String file_teacher = (path + "MemBrain-DeepRL\\MemBrain_ANNs\\Teachers.mbt");
		MBDllWrapper.MBLoadTeacherFile(file_teacher);
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("SUCCESS: <" + file_teacher + "> loaded");
		} else {
			System.out.println("ERROR: loading <" + file_teacher + ">");
		}
		
		// Training phase on lesson loaded 
		MBDllWrapper.MBRandomizeNet();
		if ( MBDllWrapper.GetLastError()==0 ) {
			System.out.println("SUCCESS: net weights randomized");
			MBDllWrapper.MBSelectTeacher("RPROP BlackJack");
			if ( MBDllWrapper.GetLastError()==0 ) {
				System.out.println("SUCCESS: teacher loaded");
				
				double NET_TOL = 1e-5;
				double net_perr = MBDllWrapper.MBGetLastNetError();
				double net_cerr = net_perr + 10*NET_TOL;
				// Start training phase
				int teacher_val = MBDllWrapper.MBTeachStep();
				int teacher_cnt = 1;
				int MIN_RUNS = 100;
				while ( (teacher_val == MBDllWrapper.MB_TR_OK) && ((net_cerr-net_perr) > NET_TOL) || (teacher_cnt < MIN_RUNS) ) {
					teacher_val = MBDllWrapper.MBTeachStep();
					net_perr = net_cerr;
					net_cerr = MBDllWrapper.MBGetLastNetError();
					System.out.println("Cnt= " + teacher_cnt + " Net err= " + net_cerr);
					teacher_cnt = teacher_cnt+1;
				}
				System.out.println("Net err= " + net_cerr + " Teacher val= " + teacher_val);
				MBDllWrapper.MBStopTeaching(); // End training
				
			} else {
				System.out.println("ERROR: cannot load teacher");
			}
		} else {
			System.out.println("ERROR: could not randomize net");
		}

	}
	/* End main() */

}

