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

/**
 * Integer part in BSON structure.
 *
 * @author Thibault Debatty
 */
public class ElementInt extends Element<Integer> {

    private final int value;

    /**
     *
     * @param type Element type.
     * @param name Element name.
     * @param value Element value.
     */
    public ElementInt(final int type, final String name, final int value) {
        super(type, name);
        this.value = value;
    }

    @Override
    public int size() {
        return super.size() + 4;
    }

    /**
     *
     * @return true if the return class is ElementInt.
     */
    @Override
    public boolean isInt() {
        return true;
    }

    /**
     *
     * @return a String.
     */
    public String toString() {
        return super.toString() + ":" + value;
    }

    @Override
    public Integer value() {
        return value;
    }

}
