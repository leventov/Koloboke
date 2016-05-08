/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.koloboke.jpsg.collect;


import com.koloboke.jpsg.*;


public final class MethodContext {

    private static final SimpleOption IMMUTABLE = new SimpleOption("Immutable");
    private static final SimpleOption UPDATABLE = new SimpleOption("Updatable");
    private static final SimpleOption MUTABLE = new SimpleOption("Mutable");

    private static final SimpleOption NULL = new SimpleOption("null");

    private static final SimpleOption TRUE = new SimpleOption("true");

    private static final SimpleOption KEY_VIEW = new SimpleOption("key");
    private static final SimpleOption VALUE_VIEW = new SimpleOption("value");
    private static final SimpleOption MAP_VIEW = new SimpleOption("map");
    private static final SimpleOption ENTRY_VIEW = new SimpleOption("entry");

    private static final SimpleOption GENERIC = new SimpleOption("generic");
    private static final SimpleOption INTERNAL = new SimpleOption("internal");

    private final SimpleOption view;

    private final Context context;

    public MethodContext(Context context) {
        this.context = context;

        Option vt = context.getOption("view");
        if (KEY_VIEW.equals(vt)) view = KEY_VIEW;
        else if (VALUE_VIEW.equals(vt)) view = VALUE_VIEW;
        else if (MAP_VIEW.equals(vt)) view = MAP_VIEW;
        else if (ENTRY_VIEW.equals(vt)) view = ENTRY_VIEW;
        else {
            if (context.getOption("value") != null) {
                view = MAP_VIEW;
            } else {
                view = KEY_VIEW;
            }
        }
    }

    private String keyDim() {
        return context.getOption("elem") != null ? "elem" : "key";
    }

    public Option keyOption() {
        return getOption(keyDim());
    }

    public boolean isIntegralKey() {
        Option key = keyOption();
        return isPrimitiveKey() && !(key == PrimitiveType.FLOAT) && !(key == PrimitiveType.DOUBLE);
    }

    public boolean isFloatingKey() {
        return isPrimitiveKey() && !isIntegralKey();
    }

    public boolean isObjectKey() {
        return keyOption() instanceof ObjectType;
    }

    public boolean isNullKey() {
        return NULL.equals(keyOption());
    }

    public boolean isObjectOrNullKey() {
        return isObjectKey() || isNullKey();
    }

    public boolean isPrimitiveKey() {
        return keyOption() instanceof PrimitiveType;
    }

    public boolean isObjectValue() {
        return mapValueOption() instanceof ObjectType;
    }

    public boolean isPrimitiveValue() {
        return mapValueOption() instanceof PrimitiveType;
    }

    public boolean isFloatingValue() {
        Option opt = mapValueOption();
        return opt == PrimitiveType.FLOAT || opt == PrimitiveType.DOUBLE;
    }

    public boolean isNullValue() {
        return NULL.equals(mapValueOption());
    }

    public final Option viewOption() {
        if (isKeyView()) return keyOption();
        if (isValueView()) return mapValueOption();
        throw new IllegalStateException();
    }

    /** @return K or char..long, floating bits */
    public String keyUnwrappedType() {
        if (isObjectKey() || isNullKey()) {
            return ObjectType.genericParamName(keyDim());
        } else if (isPrimitiveKey()) {
            return primitiveBitsType(keyOption());
        } else {
            throw new IllegalStateException();
        }
    }

    /** @return K or char..double */
    public String keyType() {
        if (isObjectKey()) {
            return ObjectType.genericParamName(keyDim());
        } else if (isPrimitiveKey()) {
            return ((PrimitiveType) keyOption()).standalone;
        } else if (isNullKey()) {
            return "Object";
        } else {
            throw new IllegalStateException();
        }
    }

    /** @return Object or char..long, floating bits */
    public String keyUnwrappedRawType() {
        if (!isPrimitiveKey()) {
            return "Object";
        } else {
            return primitiveBitsType(keyOption());
        }
    }

    /** @return V or char..long, floating bits */
    public String valueUnwrappedType() {
        if (isPrimitiveValue()) {
            return primitiveBitsType(mapValueOption());
        } else {
            return "V";
        }
    }

    /** @return V or char..double */
    public String valueType() {
        if (isPrimitiveValue()) {
            return ((PrimitiveType) mapValueOption()).standalone;
        } else {
            return "V";
        }
    }

    private String primitiveBitsType(Option opt) {
        PrimitiveType type = (PrimitiveType) opt;
        return type.bitsType().standalone;
    }

    /** @return V or char..double if version=Generic, else Character..Double */
    public String valueGenericType() {
        if (isPrimitiveValue()) {
            PrimitiveType valType = (PrimitiveType) mapValueOption();
            if (genericVersion()) {
                return valType.className;
            } else {
                return valType.standalone;
            }
        } else {
            return "V";
        }
    }

    public String applyValueName() {
        String methodName = "apply";
        if (mapValueOption() instanceof PrimitiveType && !genericVersion()) {
            methodName += "As" + ((PrimitiveType) mapValueOption()).title;
        }
        return methodName;
    }

    public final boolean isKeyView() {
        return view == KEY_VIEW;
    }

    public final boolean isValueView() {
        return view == VALUE_VIEW;
    }

    public final boolean isEntryView() {
        return view == ENTRY_VIEW;
    }

    public final boolean isMapView() {
        return view == MAP_VIEW;
    }

    public final boolean isPrimitiveView() {
        return (isKeyView() && !isObjectKey()) || (isValueView() && !isObjectValue());
    }

    public final boolean isFloatingView() {
        return (isKeyView() && isFloatingKey()) || (isValueView() && isFloatingValue());
    }

    public final boolean isObjectView() {
        return (isKeyView() && isObjectKey()) || (isValueView() && isObjectValue());
    }

    public Option mapValueOption() {
        return getOption("value");
    }

    public boolean hasValues() {
        return mapValueOption() != null;
    }

    public Option getOption(String dim) {
        return context.getOption(dim);
    }

    public boolean immutable() {
        return getOption("mutability").equals(IMMUTABLE);
    }

    public boolean updatable() {
        return getOption("mutability").equals(UPDATABLE);
    }

    public boolean mutable() {
        return getOption("mutability").equals(MUTABLE);
    }

    public boolean genericVersion() {
        return GENERIC.equals(getOption("version"));
    }

    public boolean internalVersion() {
        return INTERNAL.equals(getOption("version"));
    }

    public boolean nullKeyAllowed() {
        return TRUE.equals(getOption("nullKeyAllowed"));
    }

    public String unsafeGetKeyBits(String object, String offset) {
        return unsafeGet(object, offset, ((PrimitiveType) keyOption()).bitsType());
    }

    public void unsafePutKeyBits(MethodGenerator g, String object, String offset, String key) {
        g.lines(unsafePut(object, offset, key, ((PrimitiveType) keyOption()).bitsType()) + ";");
    }

    public String unsafeGetValueBits(String object, String offset) {
        return unsafeGet(object, offset, ((PrimitiveType) mapValueOption()).bitsType());
    }

    public void unsafePutValueBits(MethodGenerator g, String object, String offset, String value) {
        g.lines(unsafePut(object, offset, value, ((PrimitiveType) mapValueOption()).bitsType()) +
                ";");
    }

    private String unsafeGet(String object, String offset, PrimitiveType type) {
        return "U.get" + type.title + "(" + object + ", " + offset + ")";
    }

    private String unsafePut(String object, String offset, String value, PrimitiveType type) {
        return "U.put" + type.title + "(" + object + ", " + offset + ", " + value +")";
    }
}
