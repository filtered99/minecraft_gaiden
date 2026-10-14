package io.github.filtered99.gaiden.server.block;

/**
 * Log direction, stored in the log's metadata like modern Minecraft: 0 upright, 4 east-west (X), 8 north-south (Z).
 * Logs placed before Gaiden 1.2.8 have metadata 0, so they stay upright.
 */
public final class LogAxis {
	public static final int Y = 0;
	public static final int X = 4;
	public static final int Z = 8;
	private static final int MASK = 12;

	private LogAxis() {
	}

	public static int of(int metadata) {
		return metadata & MASK;
	}

	/** The axis for a log placed against a block face (0 bottom, 1 top, 2/3 north/south, 4/5 west/east). */
	public static int forPlacedFace(int face) {
		if (face == 2 || face == 3) {
			return Z;
		}
		if (face == 4 || face == 5) {
			return X;
		}
		return Y;
	}

	/** Whether this face shows the rings (the log's ends). */
	public static boolean isEndFace(int axis, int face) {
		if (axis == X) {
			return face == 4 || face == 5;
		}
		if (axis == Z) {
			return face == 2 || face == 3;
		}
		return face == 0 || face == 1;
	}

	/**
	 * Whether this face's bark texture must be turned 90 degrees so the bark runs along the log.
	 * Alpha draws every side face with the texture upright and top/bottom faces with it running north-south.
	 */
	public static boolean isRotatedFace(int axis, int face) {
		if (axis == X) {
			return face == 0 || face == 1 || face == 2 || face == 3;
		}
		if (axis == Z) {
			return face == 4 || face == 5;
		}
		return false;
	}
}
