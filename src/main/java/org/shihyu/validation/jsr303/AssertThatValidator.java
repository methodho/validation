package org.shihyu.validation.jsr303;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.shihyu.validation.jsr303.constraints.AssertThat;

/**
 * Implementation using JEXL3
 * 
 * @author Matt S.Y. Ho
 *
 */
public class AssertThatValidator implements ConstraintValidator<AssertThat, Object> {

  private String alias;
  private JexlEngine jexl;
  private String expression;
  private String message;
  private String propertyNode;

  @Override
  public void initialize(AssertThat constraintAnnotation) {
    Map<String, Object> namespaces = new HashMap<>();
    namespaces.put(null, Class.class);
    Stream.of(constraintAnnotation.namespaces())
        .forEach(ns -> namespaces.put(ns.prefix(), ns.clazz()));
    jexl = new JexlBuilder().cache(512).strict(true).silent(false).namespaces(namespaces).create();
    expression = constraintAnnotation.value();
    alias = constraintAnnotation.alias();
    message = constraintAnnotation.message();
    propertyNode = constraintAnnotation.propertyNode();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    JexlContext ctx = new MapContext();
    ctx.set(alias, value);
    Object evaluated = jexl.createExpression(expression).evaluate(ctx);
    if (!(evaluated instanceof Boolean)) {
      throw new IllegalArgumentException(
          String.format("The expression [%s] should evaluate to boolean, but was '%s'", expression,
              evaluated.getClass().getName()));
    }

    boolean valid = (boolean) evaluated;
    if (!valid && propertyNode != null && !propertyNode.isEmpty()) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNode)
          .addConstraintViolation();
    }
    return valid;
  }

}
