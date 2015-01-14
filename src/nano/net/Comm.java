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

package nano.net;
import nano.awt.*;


/**
* Listener for FloatVoll- and FloatHalbPanel inside NetSimPanel
*
* @author Raoul Schneider
* @version 1.1 14.08.2001 (documentated)
* @see NetSimPanel
*/


public class Comm implements FloatListener{
    int listeners;
    FloatListener[] MyListener = new FloatListener[50];

/**
* Constructor sets integer "listeners" to zero
*/
    public Comm(){
        listeners=0;
    }

/**
* adds a FloatListener and counts the "listeners" upwards
*/
    public void addFloatListener(FloatListener newListener){
        MyListener[listeners] = newListener;
        listeners++;
    }

    
    
    /**
     * release a FloatListener and counts the "listeners" upwards
     */
         public void releaseFloatListener(FloatListener listener){
            for (int i = 0; i < listeners; i++) {
                 if(listener.equals(MyListener[i])){
                     MyListener[i] = MyListener[listeners-1];
                     MyListener[listeners-1]=null;
                     listeners--;
                     break;
                 }
                
         } 
             
             
         }

/**
* if a FloatEvent is Performed, this method is called and sends the received
* parameters to all added FloatListeners (which have been added with "addFloatListener")
*
* @param label      label of Float-*-Kreis (or Controll in general) which has changed its value
* @param zahl       value of the label
*/
    public void FloatEventPerformed(String label, double zahl){
        for (int i=0; i<listeners; i++){
            if(MyListener[i]!=null){
                MyListener[i].FloatEventPerformed(label, zahl);
            }
        }

    }

}
