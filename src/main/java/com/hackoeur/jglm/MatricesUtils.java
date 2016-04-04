package com.hackoeur.jglm;

public class MatricesUtils {

    public static Vec4 transform(final Mat4 mat, final Vec4 vec) {
        return new Vec4(mat.m00 * vec.x + mat.m10 * vec.y + mat.m20 * vec.z + mat.m30 * vec.w,
                mat.m01 * vec.x + mat.m11 * vec.y + mat.m21 * vec.z + mat.m31 * vec.w,
                mat.m02 * vec.x + mat.m12 * vec.y + mat.m22 * vec.z + mat.m32 * vec.w,
                mat.m03 * vec.x + mat.m13 * vec.y + mat.m23 * vec.z + mat.m33 * vec.w);
    }

    public static Mat4 inverse(final Mat4 matrix) {

        final float a[][] = new float[][]{{matrix.m00, matrix.m10, matrix.m20, matrix.m30},
                {matrix.m01, matrix.m11, matrix.m21, matrix.m31},
                {matrix.m02, matrix.m12, matrix.m22, matrix.m32},
                {matrix.m03, matrix.m13, matrix.m23, matrix.m33}};
        final int n = 4;
        float[][] inverted = invert(a,
                n);
        return new Mat4(inverted[0][0], inverted[1][0], inverted[2][0], inverted[3][0],
                inverted[0][1], inverted[1][1], inverted[2][1], inverted[3][1],
                inverted[0][2], inverted[1][2], inverted[2][2], inverted[3][2],
                inverted[0][3], inverted[1][3], inverted[2][3], inverted[3][3]);
    }

    public static Mat3 inverse(final Mat3 matrix) {

        final float a[][] = new float[][]{{matrix.m00, matrix.m10, matrix.m20},
                {matrix.m01, matrix.m11, matrix.m21},
                {matrix.m02, matrix.m12, matrix.m22}};
        final int n = 3;
        float[][] inverted = invert(a,
                n);
        return new Mat3(inverted[0][0], inverted[1][0], inverted[2][0],
                inverted[0][1], inverted[1][1], inverted[2][1],
                inverted[0][2], inverted[1][2], inverted[2][2]);
    }

    private static float[][] invert(final float[][] a,
                                    final int n) {
        float x[][] = new float[n][n];
        float b[][] = new float[n][n];
        int index[] = new int[n];

        for (int i = 0; i < n; i++) {
            b[i][i] = 1;
        }
        // Transform the a into an upper triangle
        gaussian(a, index);
        // Update the a b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];
                }
            }
        }
        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }


    // Method to carry out the partial-pivoting Gaussian
    // elimination.  Here index[] stores pivoting order.
    private static void gaussian(float a[][],
                                 int index[]) {
        int n = index.length;
        float c[] = new float[n];
        // Initialize the index
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }
        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            float c1 = 0;
            for (int j = 0; j < n; ++j) {
                float c0 = Math.abs(a[i][j]);
                if (c0 > c1) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }
        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            float pi1 = 0;
            for (int i = j; i < n; ++i) {
                float pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {

                    pi1 = pi0;
                    k = i;
                }
            }
            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                float pj = a[index[i]][j] / a[index[j]][j];
                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l) {
                    a[index[i]][l] -= pj * a[index[j]][l];
                }
            }
        }
    }

}
