
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * DATFile - opens and reads a .dat file with ECG data
 * Translation of dumpBspmSamples by Dr Horacek of Halifax, Nova Scotia
 * Translated by Dakota Williams
 */
public class DATFile extends ECGFile {
	private final int MAX_NL = 282;

	public String getExtension() {
		return "dat";
	}

	public int[][] getLayout() {
		return new int[][]{
		{-1, -1}, // // first two are junk
		{-1, -1}, // //
		{-1, -1}, // 1-3 unhelpful (limb leads)
		{-1, -1}, //
		{-1, -1}, //
		{4, 0},		{5, 0}, 	{6, 0}, 	{7, 0}, 								   	//4-7
		{1, 1}, 	{2, 1}, 	{3, 1}, 	{4, 1}, 	{5, 1}, 	{6, 1}, 	{7, 1},	//8-14
		{1, 2}, 	{2, 2}, 	{3, 2}, 	{4, 2},		{5, 2},		{6, 2}, 	{7, 2},	//15-21
		{1, 3},		{2, 3},		{3, 3},		{4, 3},		{5, 3},		{6, 3},		{7, 3},	//22-28
		{1, 4},		{2, 4},		{3, 4},		{4, 4},		{5, 4},		{6, 4},		{7, 4},	//29-35
		{1, 5},		{2, 5},		{3, 5},		{4, 5},		{5, 5},		{6, 5},		{7, 5},	//36-42
		{1, 6},		{2, 6},		{3, 6},		{4, 6},		{5, 6},		{6, 6},		{7, 6},	//43-49
		{1, 7},		{2, 7},		{3, 7},		{4, 7},		{5, 7},		{6, 7},		{7, 7},	//50-56
		{1, 8},		{2, 8},		{3, 8},		{4, 8},		{5, 8},		{6, 8},		{7, 8},	//57-63
		{1, 9},		{2, 9},		{3, 9},		{4, 9},		{5, 9},		{6, 9},		{7, 9},	//64-70
		{4, 10},	{5, 10}, 	{6, 10}, 	{7, 10}, 								   	//71-74
		{4, 11},	{5, 11}, 	{6, 11}, 	{7, 11}, 									//75-78
		{1, 12},	{2, 12},	{3, 12},	{4, 12},	{5, 12},	{6, 12},	{7, 12},//79-85
		{1, 13},	{2, 13},	{3, 13},	{4, 13},	{5, 13},	{6, 13},	{7, 13},//86-92
		{0, 14},	{1, 14},	{2, 14},	{3, 14},	{4, 14},	{5, 14},	{6, 14},//93-
					{7, 14},															//  -100
		{0, 15},	{1, 15},	{2, 15},	{3, 15},	{4, 15},	{5, 15},	{6, 15},//101-
					{7, 15},															//	 -108
		{0, 16},	{1, 16},	{2, 16},	{3, 16},	{4, 16},	{5, 16},	{6, 16},//109-
					{7, 16},															//	 -116
		{1, 17},	{2, 17},	{3, 17},	{4, 17},	{5, 17},	{6, 17},	{7, 17},//117-123
		};

	}

	public double sint; // msec between samples in 1 lead
	public long[] ifbuf = new long[158000]; // modes 4-9: 72192, 78848, 92160, 118784, 0, 157696
	public short ifbufh; //pointer
	public byte[] hdrbuf = new byte[32]; //latest data record header
	
	public RandomAccessFile fh;
	public int recstride;
	public int drecsizeb;
	public int varian;
	public int grid2lead;	//pointer
	public int ir3sz;		//size of 3rd header
	public int icdsz;		//size of channel desc in 2nd header
	public int ir2sz;		//size of 2nd header
	public int idrsz;		//size of data recs
	public int jsamp;		//#samples/second/lead
	public int nbits;		//#bits per sample
	public int nspcpr;		//#samples/channel/data record
	public int nch;		//#channels
	public int frecsz;

	public int ifendn;
	public int ifholo;

	public int plot_newmag;

	public int[] mcalib = new int[MAX_NL];	//counts per millivolt
	public byte[] header_badleads = new byte[MAX_NL];

	public int ifbps;
	public int ifbpr;
	public int ifsbs;
	public int ifcrsk;
	public int ifbips;
	public int ifnhdr;

	public double secs;
	public double secskip;
	public int numtuples;

	/**
	 * read - opens a file and reads it
	 * @param fileName the file to open
	 * @param points (mutable) a place for data to be read into
	 * @return 0 on success, failure otherwise 
	 */
	public int read(String fileName, 
					ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points) {
		int numLeads = 154;
		 
		try {
			fh = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException e) {
			System.err.println("Open error on " + fileName + "\n" + e.getMessage());
			return -1;
		}

		int numMSecs;
		try {
			numMSecs = (int)((double)fh.length()/(double)1024/1.115);
			//System.out.println(fh.length());
		} catch (IOException e) {
			System.out.println("Length error\n" + e.getMessage());
			return -3;
		}
		if(numMSecs < 0) {
			numMSecs = 100;
		}

		if(readBspmHeaders() != 0) {
			return -2;
		}

		if(numLeads > nch) {
			numLeads = nch;
		}

		int tuplesPerRecord = nspcpr;

		int[] samps = new int[frecsz-32];
		int tupleNum = 0;

		int i;
		for(int recordNum = ifnhdr+1; ; recordNum++) {
			if(readBspmRecord(recordNum, samps) < 0) {
				break;
			}

			for(i = 0; i < tuplesPerRecord; i++) {
				//printing stuff here
				//System.out.printf("%10d %10.3f ms:", tupleNum, (double)tupleNum*sint);
				points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
					(double)tupleNum*sint, new ArrayList<Double>()));

				for(int j = 0; j < numLeads; j++) {
					if (j < 2) {
						//System.out.printf(" %8x", samps[8+i*nch+j]);
						points.get(tupleNum).getValue().add(samps[8+i*nch+j]*0.125);
					} else {
						//System.out.printf(" %8d", samps[8+i*nch+j]>>8);
						points.get(tupleNum).getValue().add((samps[8+i*nch+j]>>8)*0.125);
					}
				}

				//System.out.println("");

				tupleNum++;
				if(tupleNum*sint > numMSecs) {
					break;
				}
			}

			if (i < tuplesPerRecord) {
				break;
			}
		}

		try { 
			fh.close();
		} catch (IOException e) {
			System.err.println("Error on close");
		}

	//	System.out.println(Arrays.toString(header_badleads));

		return 0;
	}

	public double getSampleInterval() {
		return sint;
	}

	private ByteBuffer longArrToBytes(long[] arr) {
		ByteBuffer bb = ByteBuffer.allocate(arr.length * 8);
		for(int i = 0; i < arr.length; i++) {
			bb.putLong(arr[i]);
		}
		return bb;
	}

	private int readBspmHeaders() {
		//need to write this back later - also USE java.nio.Buffer SUBCLASSES
		//not really, not used anywhere else, not even sure why it's in the structure
		//ByteBuffer ibuf = longArrToBytes(ifbuf);
		ByteBuffer ibuf = ByteBuffer.allocate(158000 * 8);
		byte[] wbuf;
		int nbread;
		try {
			fh.seek(0);
		} catch (IOException e) {
			System.err.println("Seek error (readBspmHeaders)\n" + e.getMessage());
			return -1;
		}

		try {
			nbread = (int)fh.read(ibuf.array(), 0, 32760);
			ibuf.rewind();
		} catch (IOException e) {
			System.err.println("Read error (readBspmHeaders)\n" + e.getMessage());
			return -1;
		}

		if((ibuf.get(0) != (byte)'H') || (ibuf.get(1) != (byte)'1')) { // TODO: fix this
			System.err.println("first record not H1 (opnsrc)");
			return -1;
		}

		short shortp = ibuf.getShort();
		ifendn = ((shortp & 255) == (short)'H') ? 0 : 1;
		try {
			ifholo = "IBM2".equals(new String(ibuf.array(), 550, 4, "US-ASCII").trim()) ? 1 : 0;

			ir3sz = Integer.parseInt(new String(ibuf.array(), 568, 6, "US-ASCII").trim());
			icdsz = Integer.parseInt(new String(ibuf.array(), 566, 2, "US-ASCII").trim());

			ir2sz = Integer.parseInt(new String(ibuf.array(), 560, 6, "US-ASCII").trim());
			idrsz = Integer.parseInt(new String(ibuf.array(), 554, 6, "US-ASCII").trim());
			int is2s = Integer.parseInt(new String(ibuf.array(), 412, 6, "US-ASCII").trim());
		
			switch(is2s) {
				case 488:
					jsamp = 2048;
					break;
				case 244:
					jsamp = 4096;
					break;
				case 122:
					jsamp = 8192;
					break;
				case 61:
					jsamp = 16384;
					break;
				default:
					jsamp = 10000000/is2s;
					break;
			}

			nbits = Integer.parseInt(new String(ibuf.array(), 410, 2, "US-ASCII").trim());
			nspcpr = Integer.parseInt(new String(ibuf.array(), 406, 4, "US-ASCII").trim());
			nch = Integer.parseInt(new String(ibuf.array(), 402, 4, "US-ASCII").trim());
			sint = 1000.0/(double)jsamp;
		} catch (UnsupportedEncodingException e) {
			System.err.println("Wow, it looks like java doesn't support \"US-ASCII\" encoding");
			return -1;
		}
		/* locate start of header 2 */

		int i = 1;
		for(; i < nbread; i++) {
			if((ibuf.get(i+1) == (byte)'2') && (ibuf.get(i) == (byte)'H')) {
				break;
			}
		}

		int h2off = 0;
		if(i >= nbread-1) {
			for(h2off+=nbread-1; h2off<1000000; h2off+=nbread-1) {
				try {
					fh.seek(h2off);
				} catch (IOException e) {
					System.err.println("Seek error 2 (readBspmHeaders)\n" + e.getMessage());
					return -1;
				}

				try {
					nbread = (int)fh.read(ibuf.array(), 0, 32760);
					ibuf.rewind();
				} catch (IOException e) {
					System.err.println("Read error 2 (readBspmHeaders)\n" + e.getMessage());
					return -1;
				}

				for(i = 1; i<nbread-1; i++) {
					if((ibuf.get(i+1) == '2') && (ibuf.get(i) == 'H')) { // TODO: fix me
						break;
					}
				}
				if(i < nbread-1) {
					break;
				}
			}
		}
		i += h2off;

		try {
			fh.seek(frecsz=i);
		} catch(IOException e) {
			System.err.println("Seek error 3 (readBspmHeaders)");
			return -1;
		}

		try {
			nbread = (int)fh.read(ibuf.array(), 0, 32+icdsz*nch);
			ibuf.rewind();
		} catch (IOException e) {
			System.err.println("Read error 3 (readBspmHeaders)");
			return -1;
		}

		if((ibuf.get(0) != (byte)'H') || (ibuf.get(1) != (byte)'2')) { //TODO: fix me
			System.err.println("second record not H2 (readBspmHeaders)");
			return -1;
		}

		int firstu = 9999;
		int icdoff = 32 - icdsz;
		for(int j = 0; j < nch; j++) {
			icdoff += icdsz;
			double resol;
			try {
				resol = Double.parseDouble(
					new String(ibuf.array(), icdoff+12, 8, "US-ASCII").trim());
			} catch (UnsupportedEncodingException e) {
				System.err.println(
				"Wow, it looks like java doesn't support \"US-ASCII\" encoding\n"+e.getMessage());
				return -1;
			}
			long i1mv = (long)(1.0/(resol*1000.0) + .5);
			mcalib[j] = (int)i1mv;
			header_badleads[j] = ibuf.get(icdoff+31);

			if((header_badleads[j] == 'U') && (j < firstu)) {
				firstu = j;
			}
		}

		ifbps = 4;
		ifbpr = nspcpr * nch * ifbps + 32;
		ifbips = 24;
		ifnhdr = 2;
		if(ir3sz > 0) {
			ifnhdr = 3;
		}

		long fs3;
		try {
			fs3 = fh.length();
		} catch (IOException e) {
			System.err.println("Length error\n" + e.getMessage());
			return -1;
		}

		numtuples = (int)(fs3 / frecsz - ifnhdr) * nspcpr;
		secs = (double)(numtuples) / (double)(jsamp);
		
		return 0;
	}

	private int readBspmRecord(int recordNum, int[] samps) {
		try {
		//	System.out.println("("+ifnhdr+"+"+recordNum+"-1)*"+frecsz+"="
		//						+(ifnhdr+recordNum-1)*frecsz);
			fh.seek((ifnhdr+recordNum-1)*frecsz);
		} catch (IOException e) {
			System.err.println("Seek error (readBspmRecord)\n" + e.getMessage());
			return -1;
		}

		try {
			for (int bytes = 0; bytes < frecsz / 4; bytes++) {
				samps[bytes] = Integer.reverseBytes(fh.readInt());
			}
		} catch (EOFException e) {
		//	System.err.println("EOF");
			return -1;
		} catch (IOException e) {
			System.err.println("Read error (readBspmRecord)\n" + e.getMessage());
			return -1;
		}

		return 0;
	}
}

