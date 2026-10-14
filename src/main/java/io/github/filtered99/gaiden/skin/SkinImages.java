package io.github.filtered99.gaiden.skin;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Converts skins to the 64x64 layout with the same rules modern Minecraft uses: legacy 64x32 skins get
 * their left arm and leg copied (mirrored) from the right side, base layers are forced opaque, and a
 * legacy hat layer without any transparency is cleared (Notch's transparency hack, also done by Alpha).
 */
public final class SkinImages {
	private SkinImages() {
	}

	public static boolean hasValidSize(BufferedImage image) {
		return image != null && image.getWidth() == 64 && (image.getHeight() == 64 || image.getHeight() == 32);
	}

	public static BufferedImage toModernLayout(BufferedImage source) {
		if (!hasValidSize(source)) {
			throw new IllegalArgumentException("Skins must be 64x64 or 64x32");
		}

		boolean legacy = source.getHeight() == 32;
		BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.drawImage(source, 0, 0, null);
		graphics.dispose();
		int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		if (legacy) {
			copyRect(data, 4, 16, 16, 32, 4, 4);
			copyRect(data, 8, 16, 16, 32, 4, 4);
			copyRect(data, 0, 20, 24, 32, 4, 12);
			copyRect(data, 4, 20, 16, 32, 4, 12);
			copyRect(data, 8, 20, 8, 32, 4, 12);
			copyRect(data, 12, 20, 16, 32, 4, 12);
			copyRect(data, 44, 16, -8, 32, 4, 4);
			copyRect(data, 48, 16, -8, 32, 4, 4);
			copyRect(data, 40, 20, 0, 32, 4, 12);
			copyRect(data, 44, 20, -8, 32, 4, 12);
			copyRect(data, 48, 20, -16, 32, 4, 12);
			copyRect(data, 52, 20, -8, 32, 4, 12);
		}

		setOpaque(data, 0, 0, 32, 16);
		if (legacy) {
			clearIfFullyOpaque(data, 32, 0, 64, 32);
		}
		setOpaque(data, 0, 16, 64, 32);
		setOpaque(data, 16, 48, 48, 64);
		return image;
	}

	// Copies a w*h block from (x, y) to (x + dx, y + dy), mirrored horizontally
	private static void copyRect(int[] data, int x, int y, int dx, int dy, int w, int h) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				data[(x + dx + w - 1 - i) + (y + dy + j) * 64] = data[(x + i) + (y + j) * 64];
			}
		}
	}

	private static void setOpaque(int[] data, int u1, int v1, int u2, int v2) {
		for (int u = u1; u < u2; u++) {
			for (int v = v1; v < v2; v++) {
				data[u + v * 64] |= 0xFF000000;
			}
		}
	}

	private static void clearIfFullyOpaque(int[] data, int u1, int v1, int u2, int v2) {
		for (int u = u1; u < u2; u++) {
			for (int v = v1; v < v2; v++) {
				if ((data[u + v * 64] >>> 24) < 128) {
					return;
				}
			}
		}

		for (int u = u1; u < u2; u++) {
			for (int v = v1; v < v2; v++) {
				data[u + v * 64] &= 0x00FFFFFF;
			}
		}
	}
}
