/* 
 * Copyright (c) 2002 by Tibor Gyalog, Raoul Schneider, Dino Keller, 
 * Christian Wattinger, Martin Guggisberg and The Regents of the University of 
 * Basel. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF BASEL BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * BASEL HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF BASEL SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF BASEL HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * Authors: Tibor Gyalog, Raoul Schneider, Dino Keller, 
 * Christian Wattinger, Martin Guggisberg <vexp@nano-world.net>
 * 
 * 
 */

package nano.awt;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
* Super-Class of FloatHalbKreis and FloatVollKreis.
*
*
* @author Raoul Schneider
* @version 2.0 11.09.2002 (2.0: Completely New Concept 1.2: support for labels, 1.2.1: documentated, 1.2.2: getMinSize abstrahiert)
* @see FloatHalbKreis
* @see FloatVollKreis
*/

abstract public class FloatControl extends Component implements MouseListener, MouseMotionListener, ComponentListener, FloatListener{
    FloatListener MyListener;
    Properties Representation;
    String Label;
    double value;
    double minValue = 0;
    double maxValue = 200;

/**
* Constructor receives parameters from calling class
*
* @see FloatHalbKreis
* @see FloatVollKreis
* @param label          label of this control (used for identify)
* @param min            The smallest value on the scala
* @param max            The biggest value on the scala
* @param init           The value which is set and shown at the beginning (min <= init <= max)
* @param Representation A Properties object to specify the look & feel.
*/


    public FloatControl(String NewLabel, double min, double max, double initValue, Properties NewRepresentation){
        super();
        Label = NewLabel;
        value = initValue;
        minValue = min;
        maxValue = max;
        Representation=NewRepresentation;
}

    //============== Methoden =====================
    public void addFloatListener(FloatListener newListener){
        MyListener = newListener;
    }

/**
* gets the objects prefered size, which has been defined before in class FloatDesign
* @see nano.awt.FloatDesign
*/
    public Dimension getPreferredSize(){
    int width=100,height=100;
        try{
        width=Integer.parseInt(Representation.getProperty("Width","90"));
        height=Integer.parseInt(Representation.getProperty("Height","90"));
        }catch(Exception e){}
        return new Dimension(width, height);
    }

/**
* sets the objects minimum size (x, y)
*/
    public Dimension getMinimumSize(){
        return getPreferredSize();
    }


    public String getLabel(){
        return Label;
    }

    public void setLabel(String NewLabel){
        Label=NewLabel;
    }


/**
* returns the value
* @return value
*/
    public double getValue(){
        return value;
    }

/**
* sets the value
* @param wert new value
*/
    public void setValue(double wert){
        value = wert;
        if(value > maxValue){
            value = maxValue;}
        if(value < minValue){
            value = minValue;
        }
        repaint();
    }


    public void setRepresentation(java.util.Properties NewProperties){
        Representation=NewProperties;
    }

    public Properties getRepresentation(){
        return Representation;
    }

/**
* sets the minimum value of this control
* @param minVal     minimum value
*/
    public void setMinValue(int minVal){
        minValue = minVal;
    }


/**
* returns the minimum value of this control
* @param minVal     minimum value
*/
    public double getMinValue(){ return minValue; }

/**
* sets the maximum value of this control
* @param minVal     maximum value
*/
    public void setMaxValue(int maxVal){
        maxValue = maxVal;
    }

/**
* returns the maximum value of this control
* @param maxVal     maximum value
*/
    public double getMaxValue(){ return maxValue; }

/**
* sets the init value of this control
* @param initVal    init value
*/

    //==================== FloatEventPerformed  18.7.2001 ========================
/**
* checks if the performed FloatEvent is for this Control and if so it performs it
* @param label      label of a Control
* @param zahl       new value of this label
*/
    public void FloatEventPerformed(String TheLabel, double zahl){
        if(TheLabel.equals(Label)){
            if(!Double.isNaN(zahl)){
                setValue(zahl);
            }
        }
    }
    
    abstract public void stop_thread();

}
