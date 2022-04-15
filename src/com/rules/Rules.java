package com.rules;

import java.awt.Color;
import java.lang.IllegalArgumentException;

class Rules {

	Screen screen;
	String title;
	byte rn;
	int com = 1;
	final int cs = 3;
	final int fps = 120;
	int w = 1526/cs;
	int h = 864/cs;
	final Boolean fpsLimit = false;

	public Rules(int rn_in, int com_in) {

		rn = (byte)rn_in;
		com = com_in;
		w *= com;
		h *= com;
		title = String.format("Rule %d",rn_in);
		Boolean[] this_gen = new Boolean[w];
		Boolean[][] layer_gens = new Boolean[com][w];

		for (int i=0;i<w;i++) this_gen[i] = false;
		for (int j=0;j<com;j++) for (int i=0;i<w;i++) layer_gens[j][i] = false;
		this_gen[w/2] = true;

		screen = new Screen(w/com*cs,h/com*cs,title);

		for (int g=0;g<h;g++) {
			layer_gens[g%com] = this_gen;
			if (g%com == com-1) show(layer_gens,g);
			this_gen = nextGen(this_gen);

			if (!fpsLimit) continue;
			try {Thread.sleep(0,1000000000/fps/com);} 
			catch (IllegalArgumentException iae) {try {Thread.sleep(1000/fps/com);} catch (InterruptedException me) {}}
			catch (InterruptedException ne) {}
		}
	}


	public void show(Boolean[][] layer_gens, int g) {

		int c;
		int[] avg = new int[w/com];

		for (Boolean[] layer : layer_gens) for (int x=0;x<w/com;x++) {
			for (int i=0;i<com;i++) if (layer[x*com+i]) avg[x]++;
		}

		for (int x=0;x<w/com;x++) {
			c = (255*avg[x])/(com*com);
			screen.fillRect(x*cs,g/com*cs,cs,cs,new Color(c,c,c));
		}

		screen.flip();
	}

	public Boolean[] nextGen(Boolean[] this_gen) {

		Boolean[] next_gen = new Boolean[this_gen.length];
		Boolean next;
		int i;

		for (int x=0;x<this_gen.length;x++) {
			i = 0;
			if (x == 0) {
				if (this_gen[x]) i += 2;
				if (this_gen[x+1]) i++;
			}
			else if (x == this_gen.length-1) {
				if (this_gen[x-1]) i += 4;
				if (this_gen[x]) i += 2;
			}
			else {
				if (this_gen[x-1]) i += 4;
				if (this_gen[x]) i += 2;
				if (this_gen[x+1]) i++;
			}
			if (((rn >> i) & 0x1) == 1) next_gen[x] = true;
			else next_gen[x] = false;
		}

		return next_gen;
	}

	public static void main(String[] args) {
		int rule = 110;
		int com = 1;

		String arg;
		for (int i=0;i<args.length;i++) {
			arg = args[i];

			if ((arg.equals("-r") || arg.equals("--rule")) && i != args.length-1) {
				try {rule = Integer.parseInt(args[i+1]);}
				catch (NumberFormatException e) {}
			}
			if ((arg.equals("-c") || arg.equals("--compression")) && i != args.length-1) {
				try {com = Integer.parseInt(args[i+1]);}
				catch (NumberFormatException e) {}
			}
			if (arg.equals("-h") || arg.equals("--help")) {
				System.out.println("Usage: java -jar <file_name>.jar [options]");
				System.out.println();
				System.out.println("Options:");
				System.out.println("	-r OR --rule:        sets rule");
				System.out.println("	-c OR --compression: sets compression");
				System.out.println("	-h OR --help:        shows this text");
				System.exit(0);
			}
		}

		Rules rules = new Rules(rule,com);
	}
}
