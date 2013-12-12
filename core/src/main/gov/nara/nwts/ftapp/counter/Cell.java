package gov.nara.nwts.ftapp.counter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cell {
	int row = 0;
	int col = 0;
	boolean valid = false;
	
	static Pattern pCell = Pattern.compile("^([A-Z]+)(\\d+)$");
	
	Cell(int row, int col) {
		this.row = row;
		this.col = col;
		valid = true;
	}
	
	Cell(String cellname) {
		Matcher m = pCell.matcher(cellname);
		if (!m.matches()) return;
		String colstr=m.group(1);
		int row = Integer.parseInt(m.group(2)) - 1;
		int col = -1; //offset -1 for vector ref
		for(int i=0;i<colstr.length();i++) {
			int ii = colstr.length() - i - 1;
			int x = (colstr.charAt(ii) - 'A') + 1;
			col += Math.pow(26,i)*x;
		}
		this.row = row;
		this.col = col;
		valid = true;
	}
	
	static Cell at(String cellname) {
		return new Cell(cellname);
	}
	
	static Cell at(int row, int col) {
		return new Cell(row, col);
	}
	
	public String getCellname() {
		if (!valid) return "Invalid Cell Name";
		String rowstr = ""+(row+1);
		StringBuffer colstr = new StringBuffer();
		
		int val = col; //A=0 for last digit
		int rem = val % 26;
		
		colstr.append((char)('A'+rem));
		
		for(val = val / 26; val > 0; val = val / 26) {
			val--;  //A=1 if not last digit
			rem = val % 26;
			colstr.append((char)('A'+rem));			
		}
		return colstr.reverse().toString() + rowstr;
	}

	public static final NumberFormat nf = NumberFormat.getIntegerInstance();
	static {
		nf.setMinimumIntegerDigits(6);
		nf.setGroupingUsed(false);
	}
	
	public String getCellSort() {
		return nf.format(row) + "," + nf.format(col);
	}
	
	public static List<Cell> makeRange(int srow, int scol, int endrow, int endcol) {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		for(int r=srow; r<=endrow; r++) {
			for(int c=scol; c<=endcol; c++) {
				cells.add(new Cell(r, c));
			}			
		}
		return cells;
	}
	
	public static void test(String s) {
		Cell c = new Cell(s);
		System.out.println(s+" "+c.getCellname()+" "+c.row+","+c.col);
	}
	
	public static void main(String[] argv) {
		test("A1");
		test("A2");
		test("B1");
		test("B10");
		test("Z1");
		test("Z99");
		test("AA1");
		test("AA1000");
		test("AZ1");
		test("AZ2");
		test("BA1");
		test("BA2");
		test("ZA1");
		test("ZA2");
		test("ZZ1");
		test("ZZ2");
		test("AAA1");
		test("AAA2");
		test("YYY1");
		test("YYY2");
		test("YZZ1");
		test("YZZ2");
		test("ZYZ1");
		test("ZYZ2");
		test("ZZZ1");
		test("ZZZ2");
		test("AAAA1");
		test("AAAA2");
	}
}