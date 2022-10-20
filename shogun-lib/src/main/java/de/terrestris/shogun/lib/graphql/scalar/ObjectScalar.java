/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.graphql.scalar;

import graphql.Assert;
import graphql.Internal;
import graphql.language.*;
import graphql.schema.*;
import graphql.util.FpKit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static graphql.language.ObjectField.newObjectField;

@Internal
public class ObjectScalar {
    private ObjectScalar() {}

    static final Coercing<Object, Object> OBJECT_COERCING = new Coercing<>() {
        @Override
        public Object serialize(Object input) throws CoercingSerializeException {
            return input;
        }

        @Override
        public Object parseValue(Object input) throws CoercingParseValueException {
            return input;
        }

        @Override
        public Object parseLiteral(Object input) throws CoercingParseLiteralException {
            // on purpose - object scalars can be null
            //noinspection ConstantConditions
            return parseLiteral(input, Collections.emptyMap());
        }

        @Override
        public Object parseLiteral(Object input, Map<String, Object> variables) throws CoercingParseLiteralException {
            if (!(input instanceof Value)) {
                throw new CoercingParseLiteralException(
                    "Expected AST type 'Value' but was '" + typeName(input) + "'."
                );
            }
            if (input instanceof FloatValue) {
                return ((FloatValue) input).getValue();
            }
            if (input instanceof StringValue) {
                return ((StringValue) input).getValue();
            }
            if (input instanceof IntValue) {
                return ((IntValue) input).getValue();
            }
            if (input instanceof BooleanValue) {
                return ((BooleanValue) input).isValue();
            }
            if (input instanceof EnumValue) {
                return ((EnumValue) input).getName();
            }
            if (input instanceof VariableReference) {
                String varName = ((VariableReference) input).getName();
                return variables.get(varName);
            }
            if (input instanceof ArrayValue) {
                List<Value> values = ((ArrayValue) input).getValues();
                return values.stream()
                    .map(v -> parseLiteral(v, variables))
                    .collect(Collectors.toList());
            }
            if (input instanceof ObjectValue) {
                List<ObjectField> values = ((ObjectValue) input).getObjectFields();
                Map<String, Object> parsedValues = new LinkedHashMap<>();
                values.forEach(fld -> {
                    Object parsedValue = parseLiteral(fld.getValue(), variables);
                    parsedValues.put(fld.getName(), parsedValue);
                });
                return parsedValues;
            }
            return Assert.assertShouldNeverHappen("We have covered all Value types");
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            if (input == null) {
                return NullValue.newNullValue().build();
            }
            if (input instanceof String) {
                return new StringValue((String) input);
            }
            if (input instanceof Float) {
                return new FloatValue(BigDecimal.valueOf((Float) input));
            }
            if (input instanceof Double) {
                return new FloatValue(BigDecimal.valueOf((Double) input));
            }
            if (input instanceof BigDecimal) {
                return new FloatValue((BigDecimal) input);
            }
            if (input instanceof BigInteger) {
                return new IntValue((BigInteger) input);
            }
            if (input instanceof Number) {
                long l = ((Number) input).longValue();
                return new IntValue(BigInteger.valueOf(l));
            }
            if (input instanceof Boolean) {
                return new BooleanValue((Boolean) input);
            }
            if (FpKit.isIterable(input)) {
                return handleIterable(FpKit.toIterable(input));
            }
            if (input instanceof Map) {
                return handleMap((Map<?, ?>) input);
            }
            throw new UnsupportedOperationException("The ObjectScalar cant handle values of type : " + input.getClass());
        }

        private Value<?> handleMap(Map<?, ?> map) {
            ObjectValue.Builder builder = ObjectValue.newObjectValue();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String name = String.valueOf(entry.getKey());
                Value<?> value = valueToLiteral(entry.getValue());

                builder.objectField(
                    newObjectField().name(name).value(value).build()
                );
            }
            return builder.build();
        }

        @SuppressWarnings("rawtypes")
        private Value<?> handleIterable(Iterable<?> input) {
            List<Value> values = new ArrayList<>();
            for (Object val : input) {
                values.add(valueToLiteral(val));
            }
            return ArrayValue.newArrayValue().values(values).build();
        }
    };

    public static GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
        .name("Object")
        .description("An object scalar")
        .coercing(OBJECT_COERCING)
        .build();

    public static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }
}
