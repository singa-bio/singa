package de.bioforscher.mathematics.algorithms.matrix;

import de.bioforscher.mathematics.matrices.FastMatrices;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.matrices.RegularMatrix;

/**
 * This is the Jama SVD implementation adapted to work with SINGA.
 * TODO: provide static method calculateSVDecomposition()
 * <p>
 * Created by fkaiser on 23.10.16.
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
        int nct = Math.min(this.rowDimension - 1, this.columnDimension);
        int nrt = Math.max(0, Math.min(this.columnDimension - 2, this.rowDimension));
        for (int k = 0; k < Math.max(nct, nrt); k++) {
            if (k < nct) {

                // compute the transformation for the k-th column and place the k-th diagonal in singularValues[k]
                // compute 2-norm of k-th column without under/overflow
                this.singularValues[k] = 0;
                for (int i = k; i < this.rowDimension; i++) {
                    this.singularValues[k] = hypot(this.singularValues[k], this.arrayInput[i][k]);
                }
                if (this.singularValues[k] != 0.0) {
                    if (this.arrayInput[k][k] < 0.0) {
                        this.singularValues[k] = -this.singularValues[k];
                    }
                    for (int i = k; i < this.rowDimension; i++) {
                        this.arrayInput[i][k] /= this.singularValues[k];
                    }
                    this.arrayInput[k][k] += 1.0;
                }
                this.singularValues[k] = -this.singularValues[k];
            }
            for (int j = k + 1; j < this.columnDimension; j++) {
                if ((k < nct) & (this.singularValues[k] != 0.0)) {

                    // apply the transformation
                    double t = 0;
                    for (int i = k; i < this.rowDimension; i++) {
                        t += this.arrayInput[i][k] * this.arrayInput[i][j];
                    }
                    t = -t / this.arrayInput[k][k];
                    for (int i = k; i < this.rowDimension; i++) {
                        this.arrayInput[i][j] += t * this.arrayInput[i][k];
                    }
                }

                // place the k-th row of arrayInput into e for the subsequent calculation of the row transformation
                this.e[j] = this.arrayInput[k][j];
            }
            if (this.wantu & (k < nct)) {

                // place the transformation in arrayU for subsequent back multiplication.
                for (int i = k; i < this.rowDimension; i++) {
                    this.arrayU[i][k] = this.arrayInput[i][k];
                }
            }
            if (k < nrt) {

                // compute the k-th row transformation and place the k-th super-diagonal in e[k]
                // compute 2-norm without under/overflow
                this.e[k] = 0;
                for (int i = k + 1; i < this.columnDimension; i++) {
                    this.e[k] = hypot(this.e[k], this.e[i]);
                }
                if (this.e[k] != 0.0) {
                    if (this.e[k + 1] < 0.0) {
                        this.e[k] = -this.e[k];
                    }
                    for (int i = k + 1; i < this.columnDimension; i++) {
                        this.e[i] /= this.e[k];
                    }
                    this.e[k + 1] += 1.0;
                }
                this.e[k] = -this.e[k];
                if ((k + 1 < this.rowDimension) & (this.e[k] != 0.0)) {

                    // Apply the transformation.

                    for (int i = k + 1; i < this.rowDimension; i++) {
                        this.work[i] = 0.0;
                    }
                    for (int j = k + 1; j < this.columnDimension; j++) {
                        for (int i = k + 1; i < this.rowDimension; i++) {
                            this.work[i] += this.e[j] * this.arrayInput[i][j];
                        }
                    }
                    for (int j = k + 1; j < this.columnDimension; j++) {
                        double t = -this.e[j] / this.e[k + 1];
                        for (int i = k + 1; i < this.rowDimension; i++) {
                            this.arrayInput[i][j] += t * this.work[i];
                        }
                    }
                }
                if (this.wantv) {

                    // Place the transformation in arrayV for subsequent
                    // back multiplication.

                    for (int i = k + 1; i < this.columnDimension; i++) {
                        this.arrayV[i][k] = this.e[i];
                    }
                }
            }
        }

        // set up the final bidiagonal matrix or order p
        int p = Math.min(this.columnDimension, this.rowDimension + 1);
        if (nct < this.columnDimension) {
            this.singularValues[nct] = this.arrayInput[nct][nct];
        }
        if (this.rowDimension < p) {
            this.singularValues[p - 1] = 0.0;
        }
        if (nrt + 1 < p) {
            this.e[nrt] = this.arrayInput[nrt][p - 1];
        }
        this.e[p - 1] = 0.0;

        // if required, generate arrayU
        if (this.wantu) {
            for (int j = nct; j < this.nu; j++) {
                for (int i = 0; i < this.rowDimension; i++) {
                    this.arrayU[i][j] = 0.0;
                }
                this.arrayU[j][j] = 1.0;
            }
            for (int k = nct - 1; k >= 0; k--) {
                if (this.singularValues[k] != 0.0) {
                    for (int j = k + 1; j < this.nu; j++) {
                        double t = 0;
                        for (int i = k; i < this.rowDimension; i++) {
                            t += this.arrayU[i][k] * this.arrayU[i][j];
                        }
                        t = -t / this.arrayU[k][k];
                        for (int i = k; i < this.rowDimension; i++) {
                            this.arrayU[i][j] += t * this.arrayU[i][k];
                        }
                    }
                    for (int i = k; i < this.rowDimension; i++) {
                        this.arrayU[i][k] = -this.arrayU[i][k];
                    }
                    this.arrayU[k][k] = 1.0 + this.arrayU[k][k];
                    for (int i = 0; i < k - 1; i++) {
                        this.arrayU[i][k] = 0.0;
                    }
                } else {
                    for (int i = 0; i < this.rowDimension; i++) {
                        this.arrayU[i][k] = 0.0;
                    }
                    this.arrayU[k][k] = 1.0;
                }
            }
        }

        // if required, generate arrayV
        if (this.wantv) {
            for (int k = this.columnDimension - 1; k >= 0; k--) {
                if ((k < nrt) & (this.e[k] != 0.0)) {
                    for (int j = k + 1; j < this.nu; j++) {
                        double t = 0;
                        for (int i = k + 1; i < this.columnDimension; i++) {
                            t += this.arrayV[i][k] * this.arrayV[i][j];
                        }
                        t = -t / this.arrayV[k + 1][k];
                        for (int i = k + 1; i < this.columnDimension; i++) {
                            this.arrayV[i][j] += t * this.arrayV[i][k];
                        }
                    }
                }
                for (int i = 0; i < this.columnDimension; i++) {
                    this.arrayV[i][k] = 0.0;
                }
                this.arrayV[k][k] = 1.0;
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
                if (Math.abs(this.e[k]) <=
                        tiny + eps * (Math.abs(this.singularValues[k]) + Math.abs(this.singularValues[k + 1]))) {
                    this.e[k] = 0.0;
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
                    double t = (ks != p ? Math.abs(this.e[ks]) : 0.) +
                            (ks != k + 1 ? Math.abs(this.e[ks - 1]) : 0.);
                    if (Math.abs(this.singularValues[ks]) <= tiny + eps * t) {
                        this.singularValues[ks] = 0.0;
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
                    double f = this.e[p - 2];
                    this.e[p - 2] = 0.0;
                    for (int j = p - 2; j >= k; j--) {
                        double t = hypot(this.singularValues[j], f);
                        double cs = this.singularValues[j] / t;
                        double sn = f / t;
                        this.singularValues[j] = t;
                        if (j != k) {
                            f = -sn * this.e[j - 1];
                            this.e[j - 1] = cs * this.e[j - 1];
                        }
                        if (this.wantv) {
                            for (int i = 0; i < this.columnDimension; i++) {
                                t = cs * this.arrayV[i][j] + sn * this.arrayV[i][p - 1];
                                this.arrayV[i][p - 1] = -sn * this.arrayV[i][j] + cs * this.arrayV[i][p - 1];
                                this.arrayV[i][j] = t;
                            }
                        }
                    }
                }
                break;

                // split at negligible singularValues(k).
                case 2: {
                    double f = this.e[k - 1];
                    this.e[k - 1] = 0.0;
                    for (int j = k; j < p; j++) {
                        double t = hypot(this.singularValues[j], f);
                        double cs = this.singularValues[j] / t;
                        double sn = f / t;
                        this.singularValues[j] = t;
                        f = -sn * this.e[j];
                        this.e[j] = cs * this.e[j];
                        if (this.wantu) {
                            for (int i = 0; i < this.rowDimension; i++) {
                                t = cs * this.arrayU[i][j] + sn * this.arrayU[i][k - 1];
                                this.arrayU[i][k - 1] = -sn * this.arrayU[i][j] + cs * this.arrayU[i][k - 1];
                                this.arrayU[i][j] = t;
                            }
                        }
                    }
                }
                break;

                // perform one qr step.
                case 3: {

                    // calculate the shift
                    double scale = Math.max(Math.max(Math.max(Math.max(
                            Math.abs(this.singularValues[p - 1]), Math.abs(this.singularValues[p - 2])), Math.abs(this.e[p - 2])),
                            Math.abs(this.singularValues[k])), Math.abs(this.e[k]));
                    double sp = this.singularValues[p - 1] / scale;
                    double spm1 = this.singularValues[p - 2] / scale;
                    double epm1 = this.e[p - 2] / scale;
                    double sk = this.singularValues[k] / scale;
                    double ek = this.e[k] / scale;
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
                            this.e[j - 1] = t;
                        }
                        f = cs * this.singularValues[j] + sn * this.e[j];
                        this.e[j] = cs * this.e[j] - sn * this.singularValues[j];
                        g = sn * this.singularValues[j + 1];
                        this.singularValues[j + 1] = cs * this.singularValues[j + 1];
                        if (this.wantv) {
                            for (int i = 0; i < this.columnDimension; i++) {
                                t = cs * this.arrayV[i][j] + sn * this.arrayV[i][j + 1];
                                this.arrayV[i][j + 1] = -sn * this.arrayV[i][j] + cs * this.arrayV[i][j + 1];
                                this.arrayV[i][j] = t;
                            }
                        }
                        t = hypot(f, g);
                        cs = f / t;
                        sn = g / t;
                        this.singularValues[j] = t;
                        f = cs * this.e[j] + sn * this.singularValues[j + 1];
                        this.singularValues[j + 1] = -sn * this.e[j] + cs * this.singularValues[j + 1];
                        g = sn * this.e[j + 1];
                        this.e[j + 1] = cs * this.e[j + 1];
                        if (this.wantu && (j < this.rowDimension - 1)) {
                            for (int i = 0; i < this.rowDimension; i++) {
                                t = cs * this.arrayU[i][j] + sn * this.arrayU[i][j + 1];
                                this.arrayU[i][j + 1] = -sn * this.arrayU[i][j] + cs * this.arrayU[i][j + 1];
                                this.arrayU[i][j] = t;
                            }
                        }
                    }
                    this.e[p - 2] = f;
                    iter = iter + 1;
                }
                break;

                // convergence
                case 4: {

                    // make the singular values positive
                    if (this.singularValues[k] <= 0.0) {
                        this.singularValues[k] = (this.singularValues[k] < 0.0 ? -this.singularValues[k] : 0.0);
                        if (this.wantv) {
                            for (int i = 0; i <= pp; i++) {
                                this.arrayV[i][k] = -this.arrayV[i][k];
                            }
                        }
                    }

                    // order the singular values
                    while (k < pp) {
                        if (this.singularValues[k] >= this.singularValues[k + 1]) {
                            break;
                        }
                        double t = this.singularValues[k];
                        this.singularValues[k] = this.singularValues[k + 1];
                        this.singularValues[k + 1] = t;
                        if (this.wantv && (k < this.columnDimension - 1)) {
                            for (int i = 0; i < this.columnDimension; i++) {
                                t = this.arrayV[i][k + 1];
                                this.arrayV[i][k + 1] = this.arrayV[i][k];
                                this.arrayV[i][k] = t;
                            }
                        }
                        if (this.wantu && (k < this.rowDimension - 1)) {
                            for (int i = 0; i < this.rowDimension; i++) {
                                t = this.arrayU[i][k + 1];
                                this.arrayU[i][k + 1] = this.arrayU[i][k];
                                this.arrayU[i][k] = t;
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

        this.matrixU = FastMatrices.createRegularMatrix(this.arrayU);
        this.matrixV = FastMatrices.createRegularMatrix(this.arrayV);
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
        return this.matrixU;
    }

    public Matrix getMatrixV() {
        return this.matrixV;
    }

    private void initialize(Matrix matrix) {
        this.arrayInput = matrix.getCopy().getElements();
        this.rowDimension = matrix.getRowDimension();
        this.columnDimension = matrix.getColumnDimension();

        this.nu = Math.min(this.rowDimension, this.columnDimension);
        this.singularValues = new double[Math.min(this.rowDimension + 1, this.columnDimension)];
        this.arrayU = new double[this.rowDimension][this.nu];
        this.arrayV = new double[this.columnDimension][this.columnDimension];
        this.e = new double[this.columnDimension];
        this.work = new double[this.rowDimension];
        this.wantu = true;
        this.wantv = true;
    }

    /**
     * Return the right singular vectors
     *
     * @return arrayV
     */

    public double[][] getArrayV() {
        return this.arrayV;
    }

    /**
     * Return the one-dimensional array of singular values
     *
     * @return diagonal of S.
     */

    public double[] getSingularValues() {
        return this.singularValues;
    }

}
