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

package net.openhft.jpsg.collect;


import net.openhft.jpsg.*;


public class MethodContext {

    static final SimpleOption IMMUTABLE = new SimpleOption("Immutable");
    static final SimpleOption MUTABLE = new SimpleOption("Mutable");

    static final SimpleOption NULL = new SimpleOption("null");

    private static SimpleOption keyView = new SimpleOption("key");
    private static SimpleOption valueView = new SimpleOption("value");
    private static SimpleOption mapView = new SimpleOption("map");
    private static SimpleOption entryView = new SimpleOption("entry");

    static final SimpleOption GENERIC = new SimpleOption("generic");
    static final SimpleOption INTERNAL = new SimpleOption("internal");

    private final SimpleOption view;

    private Context context;

    public MethodContext(Context context) {
        this.context = context;

        Option vt = context.getOption("view");
        if ( keyView.equals(vt)) view = keyView;
        else if ( valueView.equals(vt)) view = valueView;
        else if ( mapView.equals(vt)) view = mapView;
        else if ( entryView.equals(vt)) view = entryView;
        else {
            if (context.getOption("value") != null) {
                view = mapView;
            } else {
                view = keyView;
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
        switch (type) {
            case FLOAT: return "int";
            case DOUBLE: return "long";
            default: return type.standalone;
        }
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
        return view == keyView;
    }

    public final boolean isValueView() {
        return view == valueView;
    }

    public final boolean isEntryView() {
        return view == entryView;
    }

    public final boolean isMapView() {
        return view == mapView;
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

    public boolean mutable() {
        return getOption("mutability").equals(MUTABLE);
    }

    public boolean genericVersion() {
        return GENERIC.equals(getOption("version"));
    }

    public boolean internalVersion() {
        return INTERNAL.equals(getOption("version"));
    }
}
