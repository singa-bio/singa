package bio.singa.mathematics.algorithms.matrix;

import bio.singa.mathematics.matrices.FastMatrices;
import bio.singa.mathematics.matrices.Matrix;

/**
 * This is the Jama SVD implementation adapted to work with SINGA.
 * TODO: provide static method calculateSVDecomposition()
 * @author fk
 */
public class SVDecomposition {

    /**
     * the matrix U
     */
    private final Matrix matrixU;
    private final Matrix matrixV;
    /**
     * copy of the input matrix elements
     */
    private double[][] arrayInput;
    /**
     * array for the internal storage of the values of matrix U
     */
    private double[][] arrayU;
    /**
     * array for the internal storage of the values of matrix V
     */
    private double[][] arrayV;
    /**
     * array for the internal storage of the singular values singularValues
     */
    private double[] singularValues;
    /**
     * storage of for the row dimension of the input matrix
     */
    private int rowDimension;
    /**
     * storage of for the column dimension of the input matrix
     */
    private int columnDimension;
    /**
     * the Eigenvalues
     */
    private double[] e;
    /**
     * whether U should be computed
     */
    private boolean wantu;
    /**
     * whether V should be computed
     */
    private boolean wantv;
    private int nu;
    /**
     * temporary data storage
     */
    private double[] work;

    public SVDecomposition(Matrix matrix) {

        initialize(matrix);

        // reduce arrayInput to bidiagonal form, storing the diagonal elements
        // in singularValues and the super-diagonal elements in e.
        int nct = Math.min(rowDimension - 1, columnDimension);
        int nrt = Math.max(0, Math.min(columnDimension - 2, rowDimension));
        for (int k = 0; k < Math.max(nct, nrt); k++) {
            if (k < nct) {

                // compute the transformation for the k-th column and place the k-th diagonal in singularValues[k]
                // compute 2-norm of k-th column without under/overflow
                singularValues[k] = 0;
                for (int i = k; i < rowDimension; i++) {
                    singularValues[k] = hypot(singularValues[k], arrayInput[i][k]);
                }
                if (singularValues[k] != 0.0) {
                    if (arrayInput[k][k] < 0.0) {
                        singularValues[k] = -singularValues[k];
                    }
                    for (int i = k; i < rowDimension; i++) {
                        arrayInput[i][k] /= singularValues[k];
                    }
                    arrayInput[k][k] += 1.0;
                }
                singularValues[k] = -singularValues[k];
            }
            for (int j = k + 1; j < columnDimension; j++) {
                if ((k < nct) & (singularValues[k] != 0.0)) {

                    // apply the transformation
                    double t = 0;
                    for (int i = k; i < rowDimension; i++) {
                        t += arrayInput[i][k] * arrayInput[i][j];
                    }
                    t = -t / arrayInput[k][k];
                    for (int i = k; i < rowDimension; i++) {
                        arrayInput[i][j] += t * arrayInput[i][k];
                    }
                }

                // place the k-th row of arrayInput into e for the subsequent calculation of the row transformation
                e[j] = arrayInput[k][j];
            }
            if (wantu & (k < nct)) {

                // place the transformation in arrayU for subsequent back multiplication.
                for (int i = k; i < rowDimension; i++) {
                    arrayU[i][k] = arrayInput[i][k];
                }
            }
            if (k < nrt) {

                // compute the k-th row transformation and place the k-th super-diagonal in e[k]
                // compute 2-norm without under/overflow
                e[k] = 0;
                for (int i = k + 1; i < columnDimension; i++) {
                    e[k] = hypot(e[k], e[i]);
                }
                if (e[k] != 0.0) {
                    if (e[k + 1] < 0.0) {
                        e[k] = -e[k];
                    }
                    for (int i = k + 1; i < columnDimension; i++) {
                        e[i] /= e[k];
                    }
                    e[k + 1] += 1.0;
                }
                e[k] = -e[k];
                if ((k + 1 < rowDimension) & (e[k] != 0.0)) {

                    // Apply the transformation.

                    for (int i = k + 1; i < rowDimension; i++) {
                        work[i] = 0.0;
                    }
                    for (int j = k + 1; j < columnDimension; j++) {
                        for (int i = k + 1; i < rowDimension; i++) {
                            work[i] += e[j] * arrayInput[i][j];
                        }
                    }
                    for (int j = k + 1; j < columnDimension; j++) {
                        double t = -e[j] / e[k + 1];
                        for (int i = k + 1; i < rowDimension; i++) {
                            arrayInput[i][j] += t * work[i];
                        }
                    }
                }
                if (wantv) {

                    // Place the transformation in arrayV for subsequent
                    // back multiplication.

                    for (int i = k + 1; i < columnDimension; i++) {
                        arrayV[i][k] = e[i];
                    }
                }
            }
        }

        // set up the final bidiagonal matrix or order p
        int p = Math.min(columnDimension, rowDimension + 1);
        if (nct < columnDimension) {
            singularValues[nct] = arrayInput[nct][nct];
        }
        if (rowDimension < p) {
            singularValues[p - 1] = 0.0;
        }
        if (nrt + 1 < p) {
            e[nrt] = arrayInput[nrt][p - 1];
        }
        e[p - 1] = 0.0;

        // if required, generate arrayU
        if (wantu) {
            for (int j = nct; j < nu; j++) {
                for (int i = 0; i < rowDimension; i++) {
                    arrayU[i][j] = 0.0;
                }
                arrayU[j][j] = 1.0;
            }
            for (int k = nct - 1; k >= 0; k--) {
                if (singularValues[k] != 0.0) {
                    for (int j = k + 1; j < nu; j++) {
                        double t = 0;
                        for (int i = k; i < rowDimension; i++) {
                            t += arrayU[i][k] * arrayU[i][j];
                        }
                        t = -t / arrayU[k][k];
                        for (int i = k; i < rowDimension; i++) {
                            arrayU[i][j] += t * arrayU[i][k];
                        }
                    }
                    for (int i = k; i < rowDimension; i++) {
                        arrayU[i][k] = -arrayU[i][k];
                    }
                    arrayU[k][k] = 1.0 + arrayU[k][k];
                    for (int i = 0; i < k - 1; i++) {
                        arrayU[i][k] = 0.0;
                    }
                } else {
                    for (int i = 0; i < rowDimension; i++) {
                        arrayU[i][k] = 0.0;
                    }
                    arrayU[k][k] = 1.0;
                }
            }
        }

        // if required, generate arrayV
        if (wantv) {
            for (int k = columnDimension - 1; k >= 0; k--) {
                if ((k < nrt) & (e[k] != 0.0)) {
                    for (int j = k + 1; j < nu; j++) {
                        double t = 0;
                        for (int i = k + 1; i < columnDimension; i++) {
                            t += arrayV[i][k] * arrayV[i][j];
                        }
                        t = -t / arrayV[k + 1][k];
                        for (int i = k + 1; i < columnDimension; i++) {
                            arrayV[i][j] += t * arrayV[i][k];
                        }
                    }
                }
                for (int i = 0; i < columnDimension; i++) {
                    arrayV[i][k] = 0.0;
                }
                arrayV[k][k] = 1.0;
            }
        }

        // main iteration loop for the singular values
        int pp = p - 1;
        int iter = 0;
        double eps = Math.pow(2.0, -52.0);
        double tiny = Math.pow(2.0, -966.0);
        while (p > 0) {
            int k;
            int kase;
            // Here is where a test for too many iterations would go.
            // This section of the program inspects for
            // negligible elements in the singularValues and e arrays.  On
            // completion the variables kase and k are set as follows.
            // kase = 1     if singularValues(p) and e[k-1] are negligible and k<p
            // kase = 2     if singularValues(k) is negligible and k<p
            // kase = 3     if e[k-1] is negligible, k<p, and
            //              singularValues(k), ..., singularValues(p) are not negligible (qr step).
            // kase = 4     if e(p-1) is negligible (convergence).
            for (k = p - 2; k >= -1; k--) {
                if (k == -1) {
                    break;
                }
                if (Math.abs(e[k]) <=
                        tiny + eps * (Math.abs(singularValues[k]) + Math.abs(singularValues[k + 1]))) {
                    e[k] = 0.0;
                    break;
                }
            }
            if (k == p - 2) {
                kase = 4;
            } else {
                int ks;
                for (ks = p - 1; ks >= k; ks--) {
                    if (ks == k) {
                        break;
                    }
                    double t = (ks != p ? Math.abs(e[ks]) : 0.) +
                            (ks != k + 1 ? Math.abs(e[ks - 1]) : 0.);
                    if (Math.abs(singularValues[ks]) <= tiny + eps * t) {
                        singularValues[ks] = 0.0;
                        break;
                    }
                }
                if (ks == k) {
                    kase = 3;
                } else if (ks == p - 1) {
                    kase = 1;
                } else {
                    kase = 2;
                    k = ks;
                }
            }
            k++;

            // perform the task indicated by kase
            switch (kase) {

                // deflate negligible singularValues(p).
                case 1: {
                    double f = e[p - 2];
                    e[p - 2] = 0.0;
                    for (int j = p - 2; j >= k; j--) {
                        double t = hypot(singularValues[j], f);
                        double cs = singularValues[j] / t;
                        double sn = f / t;
                        singularValues[j] = t;
                        if (j != k) {
                            f = -sn * e[j - 1];
                            e[j - 1] = cs * e[j - 1];
                        }
                        if (wantv) {
                            for (int i = 0; i < columnDimension; i++) {
                                t = cs * arrayV[i][j] + sn * arrayV[i][p - 1];
                                arrayV[i][p - 1] = -sn * arrayV[i][j] + cs * arrayV[i][p - 1];
                                arrayV[i][j] = t;
                            }
                        }
                    }
                }
                break;

                // split at negligible singularValues(k).
                case 2: {
                    double f = e[k - 1];
                    e[k - 1] = 0.0;
                    for (int j = k; j < p; j++) {
                        double t = hypot(singularValues[j], f);
                        double cs = singularValues[j] / t;
                        double sn = f / t;
                        singularValues[j] = t;
                        f = -sn * e[j];
                        e[j] = cs * e[j];
                        if (wantu) {
                            for (int i = 0; i < rowDimension; i++) {
                                t = cs * arrayU[i][j] + sn * arrayU[i][k - 1];
                                arrayU[i][k - 1] = -sn * arrayU[i][j] + cs * arrayU[i][k - 1];
                                arrayU[i][j] = t;
                            }
                        }
                    }
                }
                break;

                // perform one qr step.
                case 3: {

                    // calculate the shift
                    double scale = Math.max(Math.max(Math.max(Math.max(
                            Math.abs(singularValues[p - 1]), Math.abs(singularValues[p - 2])), Math.abs(e[p - 2])),
                            Math.abs(singularValues[k])), Math.abs(e[k]));
                    double sp = singularValues[p - 1] / scale;
                    double spm1 = singularValues[p - 2] / scale;
                    double epm1 = e[p - 2] / scale;
                    double sk = singularValues[k] / scale;
                    double ek = e[k] / scale;
                    double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
                    double c = (sp * epm1) * (sp * epm1);
                    double shift = 0.0;
                    if ((b != 0.0) | (c != 0.0)) {
                        shift = Math.sqrt(b * b + c);
                        if (b < 0.0) {
                            shift = -shift;
                        }
                        shift = c / (b + shift);
                    }
                    double f = (sk + sp) * (sk - sp) + shift;
                    double g = sk * ek;

                    // chase zeros
                    for (int j = k; j < p - 1; j++) {
                        double t = hypot(f, g);
                        double cs = f / t;
                        double sn = g / t;
                        if (j != k) {
                            e[j - 1] = t;
                        }
                        f = cs * singularValues[j] + sn * e[j];
                        e[j] = cs * e[j] - sn * singularValues[j];
                        g = sn * singularValues[j + 1];
                        singularValues[j + 1] = cs * singularValues[j + 1];
                        if (wantv) {
                            for (int i = 0; i < columnDimension; i++) {
                                t = cs * arrayV[i][j] + sn * arrayV[i][j + 1];
                                arrayV[i][j + 1] = -sn * arrayV[i][j] + cs * arrayV[i][j + 1];
                                arrayV[i][j] = t;
                            }
                        }
                        t = hypot(f, g);
                        cs = f / t;
                        sn = g / t;
                        singularValues[j] = t;
                        f = cs * e[j] + sn * singularValues[j + 1];
                        singularValues[j + 1] = -sn * e[j] + cs * singularValues[j + 1];
                        g = sn * e[j + 1];
                        e[j + 1] = cs * e[j + 1];
                        if (wantu && (j < rowDimension - 1)) {
                            for (int i = 0; i < rowDimension; i++) {
                                t = cs * arrayU[i][j] + sn * arrayU[i][j + 1];
                                arrayU[i][j + 1] = -sn * arrayU[i][j] + cs * arrayU[i][j + 1];
                                arrayU[i][j] = t;
                            }
                        }
                    }
                    e[p - 2] = f;
                    iter = iter + 1;
                }
                break;

                // convergence
                case 4: {

                    // make the singular values positive
                    if (singularValues[k] <= 0.0) {
                        singularValues[k] = (singularValues[k] < 0.0 ? -singularValues[k] : 0.0);
                        if (wantv) {
                            for (int i = 0; i <= pp; i++) {
                                arrayV[i][k] = -arrayV[i][k];
                            }
                        }
                    }

                    // order the singular values
                    while (k < pp) {
                        if (singularValues[k] >= singularValues[k + 1]) {
                            break;
                        }
                        double t = singularValues[k];
                        singularValues[k] = singularValues[k + 1];
                        singularValues[k + 1] = t;
                        if (wantv && (k < columnDimension - 1)) {
                            for (int i = 0; i < columnDimension; i++) {
                                t = arrayV[i][k + 1];
                                arrayV[i][k + 1] = arrayV[i][k];
                                arrayV[i][k] = t;
                            }
                        }
                        if (wantu && (k < rowDimension - 1)) {
                            for (int i = 0; i < rowDimension; i++) {
                                t = arrayU[i][k + 1];
                                arrayU[i][k + 1] = arrayU[i][k];
                                arrayU[i][k] = t;
                            }
                        }
                        k++;
                    }
                    iter = 0;
                    p--;
                }
                break;
            }
        }

        matrixU = FastMatrices.createRegularMatrix(arrayU);
        matrixV = FastMatrices.createRegularMatrix(arrayV);
    }

    private static double hypot(double a, double b) {
        double r;
        if (Math.abs(a) > Math.abs(b)) {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + r * r);
        } else if (b != 0) {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + r * r);
        } else {
            r = 0.0;
        }
        return r;
    }

    public Matrix getMatrixU() {
        return matrixU;
    }

    public Matrix getMatrixV() {
        return matrixV;
    }

    private void initialize(Matrix matrix) {
        arrayInput = matrix.getCopy().getElements();
        rowDimension = matrix.getRowDimension();
        columnDimension = matrix.getColumnDimension();

        nu = Math.min(rowDimension, columnDimension);
        singularValues = new double[Math.min(rowDimension + 1, columnDimension)];
        arrayU = new double[rowDimension][nu];
        arrayV = new double[columnDimension][columnDimension];
        e = new double[columnDimension];
        work = new double[rowDimension];
        wantu = true;
        wantv = true;
    }

    /**
     * Return the right singular vectors
     *
     * @return arrayV
     */

    public double[][] getArrayV() {
        return arrayV;
    }

    /**
     * Return the one-dimensional array of singular values
     *
     * @return diagonal of S.
     */

    public double[] getSingularValues() {
        return singularValues;
    }

}
