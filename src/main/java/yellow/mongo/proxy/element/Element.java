/*
 * The MIT License
 *
 * Copyright 2018 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package yellow.mongo.proxy.element;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import yellow.mongo.proxy.element.ElementArray;
import yellow.mongo.proxy.utils.Helper;

/**
 * In BSON, an Element is a triplet type, name and value. Each subclass
 * implements the methods related to a particular value type.
 *
 * @author Thibault Debatty
 * @param <T> type of value contained in the element
 */
public abstract class Element<T> {

    private T value;
    private final int type;
    private final String name;
    
    protected static Yaml yaml;
    {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        yaml = new Yaml(options);
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(  Helper.class);

    /**
     *
     * @param type Element type.
     * @param name Element Name.
     */
    protected Element(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    /**
     *
     * @return T value.
     */
    public abstract T value();

    /**
     * Get the name of this element.
     *
     * @return a String.
     */
    public final String getName() {
        return this.name;
    }

    /**
     *
     * @return true if the return class is ElementSting.
     */
    public boolean isString() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementInt.
     */
    public boolean isInt() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementBoolean.
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementDocument.
     */
    public boolean isDocument() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementObjectId.
     */
    public boolean isObjectId() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementDouble.
     */
    public boolean isDouble() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementBinary.
     */
    public boolean isBinary() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementUTCdatetime.
     */
    public boolean isUTCdatetime() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementTimestamp.
     */
    public boolean isTimestamp() {
        return false;
    }

    /**
     *
     * @return true if the return class is ElementInt64.
     */
    public boolean isInt64() {
        return false;
    }

    /**
     *
     * @param msg bytes array from which the boolean will be read.
     * @param start byte position from which the reading will begin.
     * @return a boolean.
     */
    public static boolean readBoolean(final byte[] msg, final int start) {
        byte b = Helper.readByte(msg, start);
        return b == 1;
    }

    /**
     * Read id object of BSON document from a byte array.
     *
     * @param msg bytes array from which the object Id will be read.
     * @param start byte position from which the reading will begin.
     * @return a Byte array.
     */
    private static byte[] readObjectId(final byte[] msg, final int start) {
        return Arrays.copyOfRange(msg, start, start + 12);
    }

    /**
     *
     * @return an integer.
     */
    public int size() {
        return 2 + name.length();
    }

    /**
     *
     * @return a String.
     */
    public String toString() {
        
//        DumperOptions options = new DumperOptions();
//        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        options.setPrettyFlow(true);
//
//        Yaml yaml = new Yaml(options);
//        String output = yaml.dump(this);
//        
//        return output;
        return type + "-" + name;
    }

    /**
     * Parse the Bytes array to extract part of BSON document.
     * 
     * http://bsonspec.org/spec.html
     *
     * @param msg bytes array from which the Element will be extract.
     * @param start byte position from which the reading will begin.
     * @return an Element.
     */
    public static Element parse(final byte[] msg, final int start) {
        int type = Helper.readByte(msg, start);
        String name = Helper.readCString(msg, start + 1);
        
        boolean isValid = true;
        for(int index = start+1; index < start + name.length()+2; index++) {
            int b = msg[index];
            if (b < 0) {
                isValid = false;
                break;
            }
        }

        if (type == 16) {
            LOGGER.info("name is {}, start is {}", name, start);
            int value = Helper.readInt(msg, start + name.length() + 2);
            return new ElementInt(type, name, value);
        }
        if (type == 1) {
            double value = Helper.readDouble(msg, start + name.length() + 2);
            return new ElementDouble(type, name, value);
        }

        if (type == 2) {
            String value = Helper.readString(msg, start + name.length() + 2);
            return new ElementString(type, name, value);
        }

        if (type == 3) {
            Document value = new Document(msg, start + name.length() + 2);
            return new ElementDocument(type, name, value);
        }

        if (type == 4) {
            // wait impl
            if (isValid) {
                Document value = new Document(msg, start + name.length() + 2);
                return new ElementDocument(type, name, value);
            }
            
            return new ElementArray(type, name, 12);
        }
        
        if (type == 5) {
            int value = Helper.readInt(msg, start + name.length() + 2);
            return new ElementBinary(type, name, value);
        }

        if (type == 7) {
            byte[] value = readObjectId(msg, start + name.length() + 2);
            return new ElementObjectId(type, name, value);
        }

        if (type == 8) {
            boolean value = readBoolean(msg, start + name.length() + 2);
            return new ElementBoolean(type, name, value);
        }

        if (type == 9) {
            long value = Helper.readInt64(msg, name.length() + 2);
            return new ElementUTCdatetime(type, name, value);
        }

        if (type == 17) {
            long value = Helper.readInt64(msg, name.length() + 2);
            return new ElementTimestamp(type, name, value);
        }

        if (type == 18) {
            long value = Helper.readInt64(msg, name.length() + 2);
            return new ElementInt64(type, name, value);
        }

        return new DummyElement(type, name);
    }

    /**
     *
     * @return a T value.
     */
    public T getValue() {
        return value;
    }

}
