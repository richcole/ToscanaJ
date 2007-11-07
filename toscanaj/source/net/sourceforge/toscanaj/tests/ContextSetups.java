/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;


public class ContextSetups {
    private static final FCAElement[] OBJECTS = new FCAElement[]{
        new FCAElementImplementation("dove"), 
        new FCAElementImplementation("hen"),
        new FCAElementImplementation("duck"),
        new FCAElementImplementation("goose"),
        new FCAElementImplementation("owl"), // 4
        new FCAElementImplementation("hawk"),
        new FCAElementImplementation("eagle"),
        new FCAElementImplementation("fox"),
        new FCAElementImplementation("dog"),
        new FCAElementImplementation("wolf"), // 9
        new FCAElementImplementation("cat"),
        new FCAElementImplementation("tiger"),
        new FCAElementImplementation("lion"),
        new FCAElementImplementation("horse"),
        new FCAElementImplementation("zebra"), // 14
        new FCAElementImplementation("cow")
    };

    private static final FCAElement[] ATTRIBUTES = new FCAElement[]{
        new FCAElementImplementation("small"),
        new FCAElementImplementation("medium"),
        new FCAElementImplementation("big"),
        new FCAElementImplementation("twolegs"),
        new FCAElementImplementation("fourlegs"), // 4
        new FCAElementImplementation("feathers"),
        new FCAElementImplementation("hair"),
        new FCAElementImplementation("fly"),
        new FCAElementImplementation("hunt"),
        new FCAElementImplementation("run"), // 9
        new FCAElementImplementation("swim"),
        new FCAElementImplementation("mane"),
        new FCAElementImplementation("hooves")
    };

    public static Context<FCAElement,FCAElement> createCompleteAnimalContext() {
        ContextImplementation<FCAElement,FCAElement> context = new ContextImplementation<FCAElement,FCAElement>();
    
        // taken from Bastian Wormuth's example
    	context.setName("Animals"); 
        context.getObjects().addAll(Arrays.asList(OBJECTS));
        context.getAttributes().addAll(Arrays.asList(ATTRIBUTES));
    
    	BinaryRelationImplementation<FCAElement,FCAElement> relation = context.getRelationImplementation();
    	
        relation.insert(OBJECTS[0], ATTRIBUTES[0]);
        relation.insert(OBJECTS[0], ATTRIBUTES[3]);
        relation.insert(OBJECTS[0], ATTRIBUTES[5]);
        relation.insert(OBJECTS[0], ATTRIBUTES[7]);
    
        relation.insert(OBJECTS[1], ATTRIBUTES[0]);
        relation.insert(OBJECTS[1], ATTRIBUTES[3]);
        relation.insert(OBJECTS[1], ATTRIBUTES[5]);
    
        relation.insert(OBJECTS[2], ATTRIBUTES[0]);
        relation.insert(OBJECTS[2], ATTRIBUTES[3]);
        relation.insert(OBJECTS[2], ATTRIBUTES[5]);
        relation.insert(OBJECTS[2], ATTRIBUTES[7]);
        relation.insert(OBJECTS[2], ATTRIBUTES[10]);
    
        relation.insert(OBJECTS[3], ATTRIBUTES[0]);
        relation.insert(OBJECTS[3], ATTRIBUTES[3]);
        relation.insert(OBJECTS[3], ATTRIBUTES[5]);
        relation.insert(OBJECTS[3], ATTRIBUTES[7]);
        relation.insert(OBJECTS[3], ATTRIBUTES[10]);
    
        relation.insert(OBJECTS[4], ATTRIBUTES[0]);
        relation.insert(OBJECTS[4], ATTRIBUTES[3]);
        relation.insert(OBJECTS[4], ATTRIBUTES[5]);
        relation.insert(OBJECTS[4], ATTRIBUTES[7]);
        relation.insert(OBJECTS[4], ATTRIBUTES[8]);
    
        relation.insert(OBJECTS[5], ATTRIBUTES[0]);
        relation.insert(OBJECTS[5], ATTRIBUTES[3]);
        relation.insert(OBJECTS[5], ATTRIBUTES[5]);
        relation.insert(OBJECTS[5], ATTRIBUTES[7]);
        relation.insert(OBJECTS[5], ATTRIBUTES[8]);
    
        relation.insert(OBJECTS[6], ATTRIBUTES[1]);
        relation.insert(OBJECTS[6], ATTRIBUTES[3]);
        relation.insert(OBJECTS[6], ATTRIBUTES[5]);
        relation.insert(OBJECTS[6], ATTRIBUTES[7]);
        relation.insert(OBJECTS[6], ATTRIBUTES[8]);
    
        relation.insert(OBJECTS[7], ATTRIBUTES[1]);
        relation.insert(OBJECTS[7], ATTRIBUTES[4]);
        relation.insert(OBJECTS[7], ATTRIBUTES[6]);
        relation.insert(OBJECTS[7], ATTRIBUTES[8]);
        relation.insert(OBJECTS[7], ATTRIBUTES[9]);
    
        relation.insert(OBJECTS[8], ATTRIBUTES[1]);
        relation.insert(OBJECTS[8], ATTRIBUTES[4]);
        relation.insert(OBJECTS[8], ATTRIBUTES[6]);
        relation.insert(OBJECTS[8], ATTRIBUTES[9]);
    
        relation.insert(OBJECTS[9], ATTRIBUTES[1]);
        relation.insert(OBJECTS[9], ATTRIBUTES[4]);
        relation.insert(OBJECTS[9], ATTRIBUTES[6]);
        relation.insert(OBJECTS[9], ATTRIBUTES[8]);
        relation.insert(OBJECTS[9], ATTRIBUTES[9]);
        relation.insert(OBJECTS[9], ATTRIBUTES[11]);
    
        relation.insert(OBJECTS[10], ATTRIBUTES[0]);
        relation.insert(OBJECTS[10], ATTRIBUTES[4]);
        relation.insert(OBJECTS[10], ATTRIBUTES[6]);
        relation.insert(OBJECTS[10], ATTRIBUTES[8]);
        relation.insert(OBJECTS[10], ATTRIBUTES[9]);
    
        relation.insert(OBJECTS[11], ATTRIBUTES[2]);
        relation.insert(OBJECTS[11], ATTRIBUTES[4]);
        relation.insert(OBJECTS[11], ATTRIBUTES[6]);
        relation.insert(OBJECTS[11], ATTRIBUTES[8]);
        relation.insert(OBJECTS[11], ATTRIBUTES[9]);
    
        relation.insert(OBJECTS[12], ATTRIBUTES[2]);
        relation.insert(OBJECTS[12], ATTRIBUTES[4]);
        relation.insert(OBJECTS[12], ATTRIBUTES[6]);
        relation.insert(OBJECTS[12], ATTRIBUTES[8]);
        relation.insert(OBJECTS[12], ATTRIBUTES[9]);
        relation.insert(OBJECTS[12], ATTRIBUTES[11]);
    
        relation.insert(OBJECTS[13], ATTRIBUTES[2]);
        relation.insert(OBJECTS[13], ATTRIBUTES[4]);
        relation.insert(OBJECTS[13], ATTRIBUTES[6]);
        relation.insert(OBJECTS[13], ATTRIBUTES[9]);
        relation.insert(OBJECTS[13], ATTRIBUTES[11]);
        relation.insert(OBJECTS[13], ATTRIBUTES[12]);
    
        relation.insert(OBJECTS[14], ATTRIBUTES[2]);
        relation.insert(OBJECTS[14], ATTRIBUTES[4]);
        relation.insert(OBJECTS[14], ATTRIBUTES[6]);
        relation.insert(OBJECTS[14], ATTRIBUTES[9]);
        relation.insert(OBJECTS[14], ATTRIBUTES[11]);
        relation.insert(OBJECTS[14], ATTRIBUTES[12]);
    
        relation.insert(OBJECTS[15], ATTRIBUTES[2]);
        relation.insert(OBJECTS[15], ATTRIBUTES[4]);
        relation.insert(OBJECTS[15], ATTRIBUTES[6]);
        relation.insert(OBJECTS[15], ATTRIBUTES[12]);
        
        return context;
    }
    public static Context<FCAElement,FCAElement> createAnimalMovementContext() {
        ContextImplementation<FCAElement,FCAElement> context = new ContextImplementation<FCAElement,FCAElement>();
    
        context.setName("Animal Movement"); 
        context.getObjects().addAll(Arrays.asList(OBJECTS));
        List<FCAElement> attribs = new ArrayList<FCAElement>();
        attribs.add(ATTRIBUTES[7]);
        attribs.add(ATTRIBUTES[8]);
        attribs.add(ATTRIBUTES[9]);
        attribs.add(ATTRIBUTES[10]);
        context.getAttributes().addAll(attribs);
    
        BinaryRelationImplementation<FCAElement,FCAElement> relation = context.getRelationImplementation();
        relation.insert(OBJECTS[0], ATTRIBUTES[7]);
        relation.insert(OBJECTS[2], ATTRIBUTES[7]);
        relation.insert(OBJECTS[2], ATTRIBUTES[10]);
        relation.insert(OBJECTS[3], ATTRIBUTES[7]);
        relation.insert(OBJECTS[3], ATTRIBUTES[10]);
        relation.insert(OBJECTS[4], ATTRIBUTES[7]);
        relation.insert(OBJECTS[4], ATTRIBUTES[8]);
        relation.insert(OBJECTS[5], ATTRIBUTES[7]);
        relation.insert(OBJECTS[5], ATTRIBUTES[8]);
        relation.insert(OBJECTS[6], ATTRIBUTES[7]);
        relation.insert(OBJECTS[6], ATTRIBUTES[8]);
        relation.insert(OBJECTS[7], ATTRIBUTES[8]);
        relation.insert(OBJECTS[7], ATTRIBUTES[9]);
        relation.insert(OBJECTS[8], ATTRIBUTES[9]);
        relation.insert(OBJECTS[9], ATTRIBUTES[8]);
        relation.insert(OBJECTS[9], ATTRIBUTES[9]);
        relation.insert(OBJECTS[10], ATTRIBUTES[8]);
        relation.insert(OBJECTS[10], ATTRIBUTES[9]);
        relation.insert(OBJECTS[11], ATTRIBUTES[8]);
        relation.insert(OBJECTS[11], ATTRIBUTES[9]);
        relation.insert(OBJECTS[12], ATTRIBUTES[8]);
        relation.insert(OBJECTS[12], ATTRIBUTES[9]);
        relation.insert(OBJECTS[13], ATTRIBUTES[9]);
        relation.insert(OBJECTS[14], ATTRIBUTES[9]);
    
        return context;
    }
    
    public static Context<FCAElement,FCAElement> createAnimalSizeContext() {
        ContextImplementation<FCAElement,FCAElement> context = new ContextImplementation<FCAElement,FCAElement>();
    
        context.setName("Animal Sizes"); 
        context.getObjects().addAll(Arrays.asList(OBJECTS));
        List<FCAElement> attribs = new ArrayList<FCAElement>();
        attribs.add(ATTRIBUTES[0]);
        attribs.add(ATTRIBUTES[1]);
        attribs.add(ATTRIBUTES[2]);
        context.getAttributes().addAll(attribs);
    
        BinaryRelationImplementation<FCAElement,FCAElement> relation = context.getRelationImplementation();
        relation.insert(OBJECTS[0], ATTRIBUTES[0]);
        relation.insert(OBJECTS[1], ATTRIBUTES[0]);
        relation.insert(OBJECTS[2], ATTRIBUTES[0]);
        relation.insert(OBJECTS[3], ATTRIBUTES[0]);
        relation.insert(OBJECTS[4], ATTRIBUTES[0]);
        relation.insert(OBJECTS[5], ATTRIBUTES[0]);
        relation.insert(OBJECTS[6], ATTRIBUTES[1]);
        relation.insert(OBJECTS[7], ATTRIBUTES[1]);
        relation.insert(OBJECTS[8], ATTRIBUTES[1]);
        relation.insert(OBJECTS[9], ATTRIBUTES[1]);
        relation.insert(OBJECTS[10], ATTRIBUTES[0]);
        relation.insert(OBJECTS[11], ATTRIBUTES[2]);
        relation.insert(OBJECTS[12], ATTRIBUTES[2]);
        relation.insert(OBJECTS[13], ATTRIBUTES[2]);
        relation.insert(OBJECTS[14], ATTRIBUTES[2]);
        relation.insert(OBJECTS[15], ATTRIBUTES[2]);
        
        return context;
    }
}
