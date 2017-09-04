/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.bioforscher.singa.javafx.viewer;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class XForm extends Group {

    public enum RotateOrder {
        XYZ, XZY, YXZ, YZX, ZXY, ZYX
    }

    public Translate translate = new Translate();
    public Translate p = new Translate();
    public Translate ip = new Translate();

    public Rotate rx = new Rotate();

    {
        this.rx.setAxis(Rotate.X_AXIS);
    }

    public Rotate ry = new Rotate();

    {
        this.ry.setAxis(Rotate.Y_AXIS);
    }

    public Rotate rz = new Rotate();

    {
        this.rz.setAxis(Rotate.Z_AXIS);
    }

    public Scale s = new Scale();

    public XForm() {
        super();
        getTransforms().addAll(this.translate, this.rz, this.ry, this.rx, this.s);
    }

    public XForm(RotateOrder rotateOrder) {
        super();
        // choose the order of rotations based on the rotateOrder
        switch (rotateOrder) {
            case XYZ:
                getTransforms().addAll(this.translate, this.p, this.rz, this.ry, this.rx, this.s, this.ip);
                break;
            case YXZ:
                getTransforms().addAll(this.translate, this.p, this.rz, this.rx, this.ry, this.s, this.ip);
                break;
            case YZX:
                getTransforms().addAll(this.translate, this.p, this.rx, this.rz, this.ry, this.s, this.ip);  // For Camera
                break;
            case ZXY:
                getTransforms().addAll(this.translate, this.p, this.ry, this.rx, this.rz, this.s, this.ip);
                break;
            case ZYX:
                getTransforms().addAll(this.translate, this.p, this.rx, this.ry, this.rz, this.s, this.ip);
                break;
        }
    }

    public void setTranslate(double x, double y, double z) {
        this.translate.setX(x);
        this.translate.setY(y);
        this.translate.setZ(z);
    }

    public void setTranslate(double x, double y) {
        this.translate.setX(x);
        this.translate.setY(y);
    }

    public void setTx(double x) {
        this.translate.setX(x);
    }

    public void setTy(double y) {
        this.translate.setY(y);
    }

    public void setTz(double z) {
        this.translate.setZ(z);
    }

    public void setRotate(double x, double y, double z) {
        this.rx.setAngle(x);
        this.ry.setAngle(y);
        this.rz.setAngle(z);
    }

    public void setRotateX(double x) {
        this.rx.setAngle(x);
    }

    public void setRotateY(double y) {
        this.ry.setAngle(y);
    }

    public void setRotateZ(double z) {
        this.rz.setAngle(z);
    }

    public void setRy(double y) {
        this.ry.setAngle(y);
    }

    public void setRz(double z) {
        this.rz.setAngle(z);
    }

    public void setScale(double scaleFactor) {
        this.s.setX(scaleFactor);
        this.s.setY(scaleFactor);
        this.s.setZ(scaleFactor);
    }

    public void setSx(double x) {
        this.s.setX(x);
    }

    public void setSy(double y) {
        this.s.setY(y);
    }

    public void setSz(double z) {
        this.s.setZ(z);
    }

    public void setPivot(double x, double y, double z) {
        this.p.setX(x);
        this.p.setY(y);
        this.p.setZ(z);
        this.ip.setX(-x);
        this.ip.setY(-y);
        this.ip.setZ(-z);
    }

    public void reset() {
        this.translate.setX(0.0);
        this.translate.setY(0.0);
        this.translate.setZ(0.0);
        this.rx.setAngle(0.0);
        this.ry.setAngle(0.0);
        this.rz.setAngle(0.0);
        this.s.setX(1.0);
        this.s.setY(1.0);
        this.s.setZ(1.0);
        this.p.setX(0.0);
        this.p.setY(0.0);
        this.p.setZ(0.0);
        this.ip.setX(0.0);
        this.ip.setY(0.0);
        this.ip.setZ(0.0);
    }

    public void resetTSP() {
        this.translate.setX(0.0);
        this.translate.setY(0.0);
        this.translate.setZ(0.0);
        this.s.setX(1.0);
        this.s.setY(1.0);
        this.s.setZ(1.0);
        this.p.setX(0.0);
        this.p.setY(0.0);
        this.p.setZ(0.0);
        this.ip.setX(0.0);
        this.ip.setY(0.0);
        this.ip.setZ(0.0);
    }

}