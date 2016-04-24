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

package net.openhft.jpsg;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public final class Condition {

    /**
     * Don't want to implement shunting yard... any condition could be
     * normalized to the form of conjunction or disjunction of positive or negated dims
     */

    private static final String POSSIBLY_NEGATED_DIMENSIONS =
            format("(%s|!?\\(%s\\))", Generator.Companion.getDIMENSIONS(),
                    Generator.Companion.getDIMENSIONS());

    static final String CONDITION =
            format("((%s\\s*\\|\\|\\s*)*|(%s\\s*&&\\s*)*)\\s*%s",
                    POSSIBLY_NEGATED_DIMENSIONS, POSSIBLY_NEGATED_DIMENSIONS,
                    POSSIBLY_NEGATED_DIMENSIONS)
            .replaceAll("\\?<[a-z]+?>", ""); // remove subgroup names

    private static enum Op {AND, OR}

    static Condition parseCheckedCondition(
            String condition, Dimensions.Parser dimensionsParser, Context context,
            CharSequence input, int pos) {
        try {
            return parse(condition, dimensionsParser, context);
        } catch (NonexistentDimensionException e) {
            throw MalformedTemplateException.near(input, pos,
                    "Nonexistent dimension in condition, context: " + context);
        }
    }
    static Condition parse(String condition, Dimensions.Parser dimensionsParser, Context context)
            throws NonexistentDimensionException {
        Condition cond = new Condition();
        String[] allDims = condition.split("\\|\\|");
        if (allDims.length > 1) {
            cond.op = Op.OR;
        } else {
            allDims = condition.split("&&");
            cond.op = Op.AND;
        }
        for (String dims : allDims) {
            dims = dims.trim();
            if (dims.startsWith("!")) {
                cond.negated.add(true);
                dims = dims.substring(1);
            } else {
                cond.negated.add(false);
            }
            if (dims.startsWith("("))
                dims = dims.substring(1, dims.length() - 1);
            cond.allDims.add(dimensionsParser.parseForCondition(dims, context));
        }
        return cond;
    }

    private Op op;
    private final List<Dimensions> allDims = new ArrayList<Dimensions>();
    private final List<Boolean> negated = new ArrayList<Boolean>();

    private boolean dimsResult(int i, Context target) {
        boolean res = allDims.get(i).checkAsCondition(target);
        if (negated.get(i))
            res = !res;
        return res;
    }

    boolean check(Context target) {
        boolean res = dimsResult(0, target);
        for (int i = 1; i < allDims.size(); i++) {
            boolean dimsRes = dimsResult(i, target);
            res = op == Op.OR ? (res || dimsRes) : (res && dimsRes);
        }
        return res;
    }
}
