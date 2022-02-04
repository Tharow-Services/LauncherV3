package net.tharow.tantalum.launchercore.logging;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.*;
import java.util.function.Predicate;

public class Level extends java.util.logging.Level implements MutableAttributeSet {
    final SimpleAttributeSet attributeSet;

    public static final Level FATAL = new Level("FATAL", 1100, Color.ORANGE);
    public static final Level SEVERE = new Level("SEVERE", 1000, Color.RED);
    public static final Level WARNING = new Level("WARNING",900,Color.YELLOW);
    public static final Level INFO = new Level("INFO", 800, Color.WHITE);
    public static final Level INFO_BLUE = new Level("INFO_BLUE", 801, Color.BLUE);
    public static final Level INFO_RED = new Level("INFO_RED", 802, Color.RED);
    public static final Level INFO_PINK = new Level("INFO_PINK", 803, Color.PINK);
    public static final Level INFO_MAGENTA = new Level("INFO_MAGENTA", 804, Color.MAGENTA);
    public static final Level INFO_ORANGE = new Level("INFO_ORANGE", 805, Color.ORANGE);
    public static final Level INFO_YELLOW = new Level("INFO_YELLOW", 806, Color.YELLOW);
    public static final Level INFO_CYAN = new Level("INFO_CYAN", 807, Color.CYAN);
    public static final Level INFO_WHITE = new Level("INFO_WHITE", 808, Color.WHITE);
    public static final Level INFO_GREEN = new Level("INFO_GREEN", 809, Color.GREEN);
    public static final Level INFO_GRAY = new Level("INFO_GRAY", 810, Color.GRAY);
    public static final Level INFO_LIGHT_GRAY = new Level("INFO_LIGHT_GRAY", 811, Color.LIGHT_GRAY);
    public static final Level INFO_DARK_GRAY = new Level("INFO_DARK_GRAY", 812, Color.DARK_GRAY);
    public static final Level INFO_HIGHLIGHTED = new Level("INFO_HIGHLIGHTED", 813, Color.BLACK, Color.YELLOW);
    public static final Level INFO_UNDERSCORE = new Level("INFO_UNDERSCORE", 814);
    public static final Level INFO_ITALICS = new Level("INFO_ITALICS", 815);
    public static final Level INFO_BOLD = new Level("INFO_BOLD", 816);
    public static final Level INFO_CUSTOM = new Level("INFO_CUSTOM", 817);
    static {
        StyleConstants.setItalic(INFO_ITALICS, true);
        StyleConstants.setUnderline(INFO_UNDERSCORE, true);
        StyleConstants.setBold(INFO_BOLD, true);

        StyleConstants.setStrikeThrough(INFO_CUSTOM, true);
        StyleConstants.setItalic(INFO_CUSTOM, true);

    }
    public static final Level CONFIG = new Level("CONFIG", 700, Color.BLUE);
    public static final Level CONSTRUCTOR = new Level("CONSTRUCTOR", 600, Color.CYAN);
    public static final Level FINE = new Level("FINE", 500, Color.LIGHT_GRAY);
    public static final Level FINER = new Level("FINER", 400, Color.GRAY);
    public static final Level FINEST = new Level("FINEST", 300 , Color.DARK_GRAY);
    public static final Level TRACKING = new Level("TRACKING",200, Color.MAGENTA);
    public static final Level DEBUG = new Level("DEBUG",100,Color.GREEN);

    public static final Level[] AllLevels = new Level[]{
            SEVERE, WARNING, INFO, INFO_BLUE, INFO_RED, INFO_PINK, INFO_MAGENTA, INFO_ORANGE, INFO_YELLOW,
            INFO_CYAN, INFO_WHITE, INFO_GREEN, INFO_GRAY, INFO_LIGHT_GRAY, INFO_DARK_GRAY, INFO_HIGHLIGHTED,
            INFO_UNDERSCORE, INFO_ITALICS, INFO_BOLD, INFO_CUSTOM, CONFIG, CONSTRUCTOR, FINE, FINER, FINEST,
            TRACKING, DEBUG, FATAL
    };

    public static Map<String, Level> stringLevelMap;
    public static Map<Integer, Level> integerLevelMap;

    static {
        stringLevelMap = new HashMap<>();
        integerLevelMap = new HashMap<>();
        for (Level level : AllLevels) {
            stringLevelMap.put(level.getName().trim(), level);
            integerLevelMap.put(level.intValue(), level);
        }
    }


    Level(final String name, final int value){
        super(name, value);
        this.attributeSet = new SimpleAttributeSet();
    }
    Level(final String name, final int value, final Color foreground){
        super(name, value);
        this.attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(this, foreground);
    }

    Level(final String name, final int value, final Color foreground, final Color background){
        super(name, value);
        this.attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(this, foreground);
        StyleConstants.setBackground(this, background);
    }

    Level(SimpleAttributeSet attributeSet, final String name, final int value){
        super(name,value);
        this.attributeSet = attributeSet;
    }

    Level(final SimpleAttributeSet attributeSet, final @NotNull java.util.logging.Level level){
        super(level.getName(), level.intValue());
        this.attributeSet = attributeSet;
    }

    SimpleAttributeSet getAttributeSet(){return attributeSet;}
    java.util.logging.Level getJavaLevel(){return this;}

    public static Level convert(int value) throws NoSuchElementException
    {
        if(!integerLevelMap.containsKey(value)){throw new NoSuchElementException("A Level With Value: "+value+" Couldn't Be Found");}
        return integerLevelMap.get(value);
    }
    public static Level convert(String name) throws NoSuchElementException
    {
        if (!stringLevelMap.containsKey(name.toUpperCase().trim())){
            throw new NoSuchElementException("A Level With Name: "+name.toUpperCase().trim()+" Couldn't Be Found");
        }
        return stringLevelMap.getOrDefault(name.toUpperCase().trim(), Level.INFO_ITALICS);
    }

    public static Level convert(MC_LEVELS level){
        switch (level) {
            case FATAL: return FATAL;
            case INFO: return INFO;
            case ERROR: return SEVERE;
            case WARN: return WARNING;
            case TRACE: return FINER;
            case DEBUG: return DEBUG;
            default: throw new RuntimeException("Tried To Convert Unknown Level In Protected Method");
        }
    }

    public static Level convertMC(String name){
        try {
            MC_LEVELS.valueOf(name);
        } catch (Exception ignored){
            Utils.getLogger().warning("A Minecraft Level With Name: "+name+" Couldn't Be Found");
            return Level.SEVERE;
        }
        return convert(MC_LEVELS.valueOf(name));
    }

    public static SimpleAttributeSet toSimpleAttributeSet(@NotNull final Level level){
        return level.attributeSet;
    }

    @Override
    public int getAttributeCount() {
        return attributeSet.getAttributeCount();
    }
    @Override
    public boolean isDefined(Object attrName) {
        return attributeSet.isDefined(attrName);
    }
    @Override
    public boolean isEqual(AttributeSet attr) {
        return attributeSet.isEqual(attr);
    }
    @Override
    public AttributeSet copyAttributes() {
        return attributeSet.copyAttributes();
    }
    @Override
    public Object getAttribute(Object key) {
        return attributeSet.getAttribute(key);
    }
    @Override
    public Enumeration<?> getAttributeNames() {
        return attributeSet.getAttributeNames();
    }
    @Override
    public boolean containsAttribute(Object name, Object value) {
        return attributeSet.containsAttribute(name, value);
    }
    @Override
    public boolean containsAttributes(AttributeSet attributes) {
        return attributeSet.containsAttributes(attributes);
    }
    @Override
    public AttributeSet getResolveParent() {
        return attributeSet.getResolveParent();
    }

    @Override
    public void addAttribute(Object name, Object value) {
        attributeSet.addAttribute(name, value);
    }

    @Override
    public void addAttributes(AttributeSet attributes) {
        attributeSet.addAttributes(attributes);
    }
    @Override
    public void removeAttribute(Object name) {
        attributeSet.removeAttribute(name);
    }
    @Override
    public void removeAttributes(Enumeration<?> names) {
        attributeSet.removeAttributes(names);
    }
    @Override
    public void removeAttributes(AttributeSet attributes) {
        attributeSet.removeAttributes(attributes);
    }
    @Override
    public void setResolveParent(AttributeSet parent) {
        attributeSet.setResolveParent(parent);
    }

    public enum MC_LEVELS {
        INFO,
        WARN,
        ERROR,
        FATAL,
        TRACE,
        DEBUG
    }
}