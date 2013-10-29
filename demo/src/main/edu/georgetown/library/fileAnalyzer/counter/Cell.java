package edu.georgetown.library.fileAnalyzer.counter;

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
	
	public String getCellname() {
		if (!valid) return "Invalid Cell Name";
		String rowstr = ""+(row+1);
		String colstr = "";
		double dfact = (col == 0) ? 1 : (Math.log(col) / Math.log(26)) + 1;
		int fact = (int)dfact;
		
		int val = col;
		for(int pow = fact - 1; pow >= 0; pow--) {
			int rem = ((val + 1) / (int)Math.pow(26, pow)) - 1;
			colstr += ((char)('A' + rem));
			val = val % (int)Math.pow(26, pow);
		}
		return colstr + rowstr;
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
		test("B2");
		test("Z1");
		test("Z2");
		test("AA1");
		test("AA2");
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
	}
}