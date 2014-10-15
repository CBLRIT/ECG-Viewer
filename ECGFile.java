
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
 * ECGFile - opens and reads a .dat file with ECG data
 * Translation of dumpBspmSamples by Dr Horacek of Halifax, Nova Scotia
 * Translated by Dakota Williams
 */
public class ECGFile {
	private final int MAX_NL = 282;

	public class fileinfo { //struct
		public double sint; // msec between samples in 1 lead
		public long[] ifbuf; // modes 4-9: 72192, 78848, 92160, 118784, 0, 157696
		public short ifbufh; //pointer
		public byte[] hdrbuf; //latest data record header
		
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

		public int[] mcalib;	//counts per millivolt
		public byte[] header_badleads;

		public int ifbps;
		public int ifbpr;
		public int ifsbs;
		public int ifcrsk;
		public int ifbips;
		public int ifnhdr;

		public double secs;
		public double secskip;
		public int numtuples;

		public fileinfo() {
			hdrbuf = new byte[32];
			ifbuf = new long[158000];
			mcalib = new int[MAX_NL];
			header_badleads = new byte[MAX_NL];
		}
	}

	/**
	 * read - opens a file and reads it
	 * @param fileName the file to open
	 * @param numLeads the number of leads, can be less than 0 (assumes default value of 8)
	 * @param points (mutable) a place for data to be read into
	 * @return 0 on success, failure otherwise 
	 */
	public int read(String fileName, 
					int numLeads, 
					ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Integer>>> points) {
		fileinfo finfo = new fileinfo();
		
		if(numLeads < 0) {
			numLeads = 8;
		}
		 
		try {
			finfo.fh = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException e) {
			System.err.println("Open error on " + fileName + "\n" + e.getMessage());
			return -1;
		}

		int numMSecs;
		try {
			numMSecs = (int)((double)finfo.fh.length()/(double)1024/1.115);
			//System.out.println(finfo.fh.length());
		} catch (IOException e) {
			System.out.println("Length error\n" + e.getMessage());
			return -3;
		}
		if(numMSecs < 0) {
			numMSecs = 100;
		}

		if(readBspmHeaders(finfo) != 0) {
			return -2;
		}

		if(numLeads > finfo.nch) {
			numLeads = finfo.nch;
		}

		int tuplesPerRecord = finfo.nspcpr;

		int[] samps = new int[finfo.frecsz-32];
		int tupleNum = 0;

		int i;
		for(int recordNum = finfo.ifnhdr+1; ; recordNum++) {
			if(readBspmRecord(finfo, recordNum, samps) < 0) {
				break;
			}

			for(i = 0; i < tuplesPerRecord; i++) {
				//printing stuff here
				//System.out.printf("%10d %10.3f ms:", tupleNum, (double)tupleNum*finfo.sint);
				points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Integer>>(
					(double)tupleNum*finfo.sint, new ArrayList<Integer>()));

				for(int j = 0; j < numLeads; j++) {
					if (j < 2) {
						//System.out.printf(" %8x", samps[8+i*finfo.nch+j]);
						points.get(tupleNum).getValue().add(samps[8+i*finfo.nch+j]);
					} else {
						//System.out.printf(" %8d", samps[8+i*finfo.nch+j]>>8);
						points.get(tupleNum).getValue().add(samps[8+i*finfo.nch+j]>>8);
					}
				}

				//System.out.println("");

				tupleNum++;
				if(tupleNum*finfo.sint > numMSecs) {
					break;
				}
			}

			if (i < tuplesPerRecord) {
				break;
			}
		}

		try { 
			finfo.fh.close();
		} catch (IOException e) {
			System.err.println("Error on close");
		}

	//	System.out.println(Arrays.toString(finfo.header_badleads));

		return 0;
	}

	private ByteBuffer longArrToBytes(long[] arr) {
		ByteBuffer bb = ByteBuffer.allocate(arr.length * 8);
		for(int i = 0; i < arr.length; i++) {
			bb.putLong(arr[i]);
		}
		return bb;
	}

	private int readBspmHeaders(fileinfo finfo) {
		//need to write this back later - also USE java.nio.Buffer SUBCLASSES
		//not really, not used anywhere else, not even sure why it's in the structure
		//ByteBuffer ibuf = longArrToBytes(finfo.ifbuf);
		ByteBuffer ibuf = ByteBuffer.allocate(158000 * 8);
		byte[] wbuf;
		int nbread;
		try {
			finfo.fh.seek(0);
		} catch (IOException e) {
			System.err.println("Seek error (readBspmHeaders)\n" + e.getMessage());
			return -1;
		}

		try {
			nbread = (int)finfo.fh.read(ibuf.array(), 0, 32760);
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
		finfo.ifendn = ((shortp & 255) == (short)'H') ? 0 : 1;
		try {
			finfo.ifholo = "IBM2".equals(new String(ibuf.array(), 550, 4, "US-ASCII").trim()) ? 1 : 0;

			finfo.ir3sz = Integer.parseInt(new String(ibuf.array(), 568, 6, "US-ASCII").trim());
			finfo.icdsz = Integer.parseInt(new String(ibuf.array(), 566, 2, "US-ASCII").trim());

			finfo.ir2sz = Integer.parseInt(new String(ibuf.array(), 560, 6, "US-ASCII").trim());
			finfo.idrsz = Integer.parseInt(new String(ibuf.array(), 554, 6, "US-ASCII").trim());
			int is2s = Integer.parseInt(new String(ibuf.array(), 412, 6, "US-ASCII").trim());
		
			switch(is2s) {
				case 488:
					finfo.jsamp = 2048;
					break;
				case 244:
					finfo.jsamp = 4096;
					break;
				case 122:
					finfo.jsamp = 8192;
					break;
				case 61:
					finfo.jsamp = 16384;
					break;
				default:
					finfo.jsamp = 10000000/is2s;
					break;
			}

			finfo.nbits = Integer.parseInt(new String(ibuf.array(), 410, 2, "US-ASCII").trim());
			finfo.nspcpr = Integer.parseInt(new String(ibuf.array(), 406, 4, "US-ASCII").trim());
			finfo.nch = Integer.parseInt(new String(ibuf.array(), 402, 4, "US-ASCII").trim());
			finfo.sint = 1000.0/(double)finfo.jsamp;
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
					finfo.fh.seek(h2off);
				} catch (IOException e) {
					System.err.println("Seek error 2 (readBspmHeaders)\n" + e.getMessage());
					return -1;
				}

				try {
					nbread = (int)finfo.fh.read(ibuf.array(), 0, 32760);
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
			finfo.fh.seek(finfo.frecsz=i);
		} catch(IOException e) {
			System.err.println("Seek error 3 (readBspmHeaders)");
			return -1;
		}

		try {
			nbread = (int)finfo.fh.read(ibuf.array(), 0, 32+finfo.icdsz*finfo.nch);
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
		int icdoff = 32 - finfo.icdsz;
		for(int j = 0; j < finfo.nch; j++) {
			icdoff += finfo.icdsz;
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
			finfo.mcalib[j] = (int)i1mv;
			finfo.header_badleads[j] = ibuf.get(icdoff+31);

			if((finfo.header_badleads[j] == 'U') && (j < firstu)) {
				firstu = j;
			}
		}

		finfo.ifbps = 4;
		finfo.ifbpr = finfo.nspcpr * finfo.nch * finfo.ifbps + 32;
		finfo.ifbips = 24;
		finfo.ifnhdr = 2;
		if(finfo.ir3sz > 0) {
			finfo.ifnhdr = 3;
		}

		long fs3;
		try {
			fs3 = finfo.fh.length();
		} catch (IOException e) {
			System.err.println("Length error\n" + e.getMessage());
			return -1;
		}

		finfo.numtuples = (int)(fs3 / finfo.frecsz - finfo.ifnhdr) * finfo.nspcpr;
		finfo.secs = (double)(finfo.numtuples) / (double)(finfo.jsamp);
		
		return 0;
	}

	private int readBspmRecord(fileinfo finfo, int recordNum, int[] samps) {
		try {
		//	System.out.println("("+finfo.ifnhdr+"+"+recordNum+"-1)*"+finfo.frecsz+"="
		//						+(finfo.ifnhdr+recordNum-1)*finfo.frecsz);
			finfo.fh.seek((finfo.ifnhdr+recordNum-1)*finfo.frecsz);
		} catch (IOException e) {
			System.err.println("Seek error (readBspmRecord)\n" + e.getMessage());
			return -1;
		}

		try {
			for (int bytes = 0; bytes < finfo.frecsz / 4; bytes++) {
				samps[bytes] = Integer.reverseBytes(finfo.fh.readInt());
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

