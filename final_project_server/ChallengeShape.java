package com.tetris;

import java.util.Random;

public class ChallengeShape {

    protected enum Pentominoe {NoShape, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Eleven, Twelve, Thirteen, Fourteen, Fifteen, Sixteen, Seventeen, Eighteen}

    private Pentominoe pieceShape;
    private int coords[][];
    private int[][][] coordsTable;

    public ChallengeShape() {
        initShape();
    }

    private void initShape() {
        coords = new int[5][2];
        coordsTable = new int[][][] {
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}, {1, 0}},
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}, {-1, 0}},
                {{0, -2}, {0, -1}, {0, 0}, {0, 1}, {0, 2}},
                {{0, 2}, {-1, 0}, {0, 0}, {1, 0}, {0, 1}},
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}},
				{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {1, 2}},
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}, {0, 2}},
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}, {0, 2}},
				{{-1, -1}, {0, -1}, {0, 0}, {0, 1}, {-1, -2}},
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}, {1, -2}},
				{{-1, 1}, {-1, 0}, {0, 0}, {1, 0}, {1, 1}},
				{{-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}},
				{{-1, -1}, {0, -1}, {0, 0}, {1, 0}, {1, 1}},
				{{-1, 0}, {0, 0}, {0, 1}, {1, 0}, {0, -1}},
				{{-1, 1}, {0, 1}, {0, 2}, {0, 0}, {0, -1}},
				{{1, 1}, {0, 1}, {0, 2}, {0, 0}, {0, -1}},
				{{-1, -1}, {0, -1}, {0, 0}, {0, 1}, {1, 1}},
				{{-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}},
        };
        setShape(Pentominoe.NoShape);
    }

    protected void setShape(Pentominoe shape) {
        for (int i = 0; i < 5 ; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }

    private void setX(int index, int x) { 
		coords[index][0] = x; 
	}
    
	private void setY(int index, int y) { 
		coords[index][1] = y; 
	}
    
	public int x(int index) { 
		return coords[index][0]; 
	}
	
	public int y(int index) {
		return coords[index][1]; 
	}
    
	public Pentominoe getShape() { 
		return pieceShape;
	}

    public void setRandomShape() {
        var r = new Random();
        int x = Math.abs(r.nextInt()) % 18 + 1;

        Pentominoe[] values = Pentominoe.values();
        setShape(values[x]);
    }

    public int minX() {
        int m = coords[0][0];

        for (int i = 0; i < 5; i++) {
            m = Math.min(m, coords[i][0]);
        }

        return m;
    }


    public int minY() {
        int m = coords[0][1];

        for (int i = 0; i < 5; i++) {
            m = Math.min(m, coords[i][1]);
        }

        return m;
    }

    public ChallengeShape rotateLeft() {

        var result = new ChallengeShape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 5; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }

        return result;
    }

    public ChallengeShape rotateRight() {

        var result = new ChallengeShape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 5; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }

        return result;
    }
}