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
        rx.setAxis(Rotate.X_AXIS);
    }

    public Rotate ry = new Rotate();

    {
        ry.setAxis(Rotate.Y_AXIS);
    }

    public Rotate rz = new Rotate();

    {
        rz.setAxis(Rotate.Z_AXIS);
    }

    public Scale s = new Scale();

    public XForm() {
        super();
        getTransforms().addAll(translate, rz, ry, rx, s);
    }

    public XForm(RotateOrder rotateOrder) {
        super();
        // choose the order of rotations based on the rotateOrder
        switch (rotateOrder) {
            case XYZ:
                getTransforms().addAll(translate, p, rz, ry, rx, s, ip);
                break;
            case YXZ:
                getTransforms().addAll(translate, p, rz, rx, ry, s, ip);
                break;
            case YZX:
                getTransforms().addAll(translate, p, rx, rz, ry, s, ip);  // For Camera
                break;
            case ZXY:
                getTransforms().addAll(translate, p, ry, rx, rz, s, ip);
                break;
            case ZYX:
                getTransforms().addAll(translate, p, rx, ry, rz, s, ip);
                break;
        }
    }

    public void setTranslate(double x, double y, double z) {
        translate.setX(x);
        translate.setY(y);
        translate.setZ(z);
    }

    public void setTranslate(double x, double y) {
        translate.setX(x);
        translate.setY(y);
    }

    public void setTx(double x) {
        translate.setX(x);
    }

    public void setTy(double y) {
        translate.setY(y);
    }

    public void setTz(double z) {
        translate.setZ(z);
    }

    public void setRotate(double x, double y, double z) {
        rx.setAngle(x);
        ry.setAngle(y);
        rz.setAngle(z);
    }

    public void setRotateX(double x) {
        rx.setAngle(x);
    }

    public void setRotateY(double y) {
        ry.setAngle(y);
    }

    public void setRotateZ(double z) {
        rz.setAngle(z);
    }

    public void setRy(double y) {
        ry.setAngle(y);
    }

    public void setRz(double z) {
        rz.setAngle(z);
    }

    public void setScale(double scaleFactor) {
        s.setX(scaleFactor);
        s.setY(scaleFactor);
        s.setZ(scaleFactor);
    }

    public void setSx(double x) {
        s.setX(x);
    }

    public void setSy(double y) {
        s.setY(y);
    }

    public void setSz(double z) {
        s.setZ(z);
    }

    public void setPivot(double x, double y, double z) {
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        ip.setX(-x);
        ip.setY(-y);
        ip.setZ(-z);
    }

    public void reset() {
        translate.setX(0.0);
        translate.setY(0.0);
        translate.setZ(0.0);
        rx.setAngle(0.0);
        ry.setAngle(0.0);
        rz.setAngle(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

    public void resetTSP() {
        translate.setX(0.0);
        translate.setY(0.0);
        translate.setZ(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

}