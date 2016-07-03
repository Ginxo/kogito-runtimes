/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel11;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.lang.ast.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FEELParserTest {

    @Test
    public void testIntegerLiteral() {
        String inputExpression = "10";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( NumberNode.class ) ) );
        assertLocation( inputExpression, number );
    }

    @Test
    public void testNegativeIntegerLiteral() {
        String inputExpression = "-10";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.NEGATIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10" ) );
    }

    @Test
    public void testPositiveIntegerLiteral() {
        String inputExpression = "+10";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.POSITIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10" ) );
    }

    @Test
    public void testFloatLiteral() {
        String inputExpression = "10.5";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( NumberNode.class ) ) );
        assertLocation( inputExpression, number );
    }

    @Test
    public void testNegativeFloatLiteral() {
        String inputExpression = "-10.5";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.NEGATIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10.5" ) );
    }

    @Test
    public void testPositiveFloatLiteral() {
        String inputExpression = "+10.5";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.POSITIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10.5" ) );
    }

    @Test
    public void testBooleanTrueLiteral() {
        String inputExpression = "true";
        BaseNode bool = parse( inputExpression );

        assertThat( bool, is( instanceOf( BooleanNode.class ) ) );
        assertLocation( inputExpression, bool );
    }

    @Test
    public void testBooleanFalseLiteral() {
        String inputExpression = "false";
        BaseNode bool = parse( inputExpression );

        assertThat( bool, is( instanceOf( BooleanNode.class ) ) );
        assertLocation( inputExpression, bool );
    }

    @Test
    public void testNullLiteral() {
        String inputExpression = "null";
        BaseNode nullLit = parse( inputExpression );

        assertThat( nullLit, is( instanceOf( NullNode.class ) ) );
        assertLocation( inputExpression, nullLit );
    }

    @Test
    public void testStringLiteral() {
        String inputExpression = "\"some string\"";
        BaseNode nullLit = parse( inputExpression );

        assertThat( nullLit, is( instanceOf( StringNode.class ) ) );
        assertLocation( inputExpression, nullLit );
    }

    @Test
    public void testNameReference() {
        String inputExpression = "someSimpleName";
        BaseNode nullLit = parse( inputExpression );

        assertThat( nullLit, is( instanceOf( NameRefNode.class ) ) );
        assertLocation( inputExpression, nullLit );
    }

    @Test
    public void testParensWithLiteral() {
        String inputExpression = "(10.5 )";
        BaseNode number = parse( inputExpression );

        assertThat( number, is( instanceOf( NumberNode.class ) ) );
        assertThat( number.getText(), is( "10.5" ) );
    }

    @Test
    public void testLogicalNegation() {
        String inputExpression = "not ( true )";
        BaseNode neg = parse( inputExpression );

        assertThat( neg, is( instanceOf( NotNode.class ) ) );
        assertThat( neg.getText(), is( "not ( true )" ) );

        NotNode not = (NotNode) neg;
        assertThat( not.getExpression(), is( instanceOf( BooleanNode.class ) ) );
        assertThat( not.getExpression().getText(), is( "true" ) );
    }

    @Test
    public void testMultiplication() {
        String inputExpression = "10 * x";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "10" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "x" ) );
    }

    @Test
    public void testDivision() {
        String inputExpression = "y / 5 * ( x )";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y / 5" ) );

        InfixOpNode div = (InfixOpNode) mult.getLeft();
        assertThat( div.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( div.getLeft().getText(), is( "y" ) );

        assertThat( div.getOperator(), is( "/" ) );

        assertThat( div.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( div.getRight().getText(), is( "5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "x" ) );
    }

    @Test
    public void testPower1() {
        String inputExpression = "y * 5 ** 3";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "5 ** 3" ) );

        InfixOpNode exp = (InfixOpNode) mult.getRight();
        assertThat( exp.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "5" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "3" ) );
    }

    @Test
    public void testPower2() {
        String inputExpression = "(y * 5) ** 3";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode exp = (InfixOpNode) infix;
        assertThat( exp.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "y * 5" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "3" ) );

        InfixOpNode mult = (InfixOpNode) exp.getLeft();
        assertThat( mult.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "5" ) );
    }

    @Test
    public void testPower3() {
        String inputExpression = "y ** 5 * 3";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y ** 5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "3" ) );

        InfixOpNode exp = (InfixOpNode) mult.getLeft();
        assertThat( exp.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "y" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "5" ) );
    }

    @Test
    public void testPower4() {
        String inputExpression = "y ** ( 5 * 3 )";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode exp = (InfixOpNode) infix;
        assertThat( exp.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "y" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "5 * 3" ) );

        InfixOpNode mult = (InfixOpNode) exp.getRight();
        assertThat( mult.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "3" ) );
    }

    @Test
    public void testAdd1() {
        String inputExpression = "y + 5 * 3";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode add = (InfixOpNode) infix;
        assertThat( add.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( add.getLeft().getText(), is( "y" ) );

        assertThat( add.getOperator(), is( "+" ) );

        assertThat( add.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( add.getRight().getText(), is( "5 * 3" ) );

        InfixOpNode mult = (InfixOpNode) add.getRight();
        assertThat( mult.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "3" ) );
    }

    @Test
    public void testSub1() {
        String inputExpression = "(y - 5) ** 3";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode sub = (InfixOpNode) infix;
        assertThat( sub.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( sub.getLeft().getText(), is( "y - 5" ) );

        assertThat( sub.getOperator(), is( "**" ) );

        assertThat( sub.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sub.getRight().getText(), is( "3" ) );

        InfixOpNode mult = (InfixOpNode) sub.getLeft();
        assertThat( mult.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y" ) );

        assertThat( mult.getOperator(), is( "-" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "5" ) );
    }

    @Test
    public void testBetween() {
        String inputExpression = "x between 10+y and 3**z";
        BaseNode between = parse( inputExpression );

        assertThat( between, is( instanceOf( BetweenNode.class ) ) );
        assertThat( between.getText(), is( inputExpression ) );

        BetweenNode btw = (BetweenNode) between;
        assertThat( btw.getValue(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( btw.getValue().getText(), is( "x" ) );

        assertThat( btw.getStart(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( btw.getStart().getText(), is( "10+y" ) );

        assertThat( btw.getEnd(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( btw.getEnd().getText(), is( "3**z" ) );
    }

    @Test
    public void testInValueList() {
        String inputExpression = "x / 4 in ( 10+y, true, 80, someVar )";
        BaseNode inNode = parse( inputExpression );

        assertThat( inNode, is( instanceOf( InNode.class ) ) );
        assertThat( inNode.getText(), is( inputExpression ) );

        InNode in = (InNode) inNode;
        assertThat( in.getValue(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( in.getValue().getText(), is( "x / 4" ) );

        assertThat( in.getExprs(), is( instanceOf( ListNode.class ) ) );
        assertThat( in.getExprs().getText(), is( "10+y, true, 80, someVar" ) );

        ListNode list = (ListNode) in.getExprs();
        assertThat( list.getElements().get( 0 ), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( list.getElements().get( 1 ), is( instanceOf( BooleanNode.class ) ) );
        assertThat( list.getElements().get( 2 ), is( instanceOf( NumberNode.class ) ) );
        assertThat( list.getElements().get( 3 ), is( instanceOf( NameRefNode.class ) ) );
    }

    @Test
    public void testInUnaryTestList() {
        String inputExpression = "x ** y in ( <=1000, >t, null, (2000..z[, ]z..2000], [(10+5)..(a*b)) )";
        BaseNode inNode = parse( inputExpression );

        assertThat( inNode, is( instanceOf( InNode.class ) ) );
        assertThat( inNode.getText(), is( inputExpression ) );

        InNode in = (InNode) inNode;
        assertThat( in.getValue(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( in.getValue().getText(), is( "x ** y" ) );

        assertThat( in.getExprs(), is( instanceOf( ListNode.class ) ) );
        assertThat( in.getExprs().getText(), is( "<=1000, >t, null, (2000..z[, ]z..2000], [(10+5)..(a*b))" ) );

        ListNode list = (ListNode) in.getExprs();
        assertThat( list.getElements().get( 0 ), is( instanceOf( UnaryTestNode.class ) ) );
        assertThat( list.getElements().get( 0 ).getText(), is( "<=1000" ) );

        assertThat( list.getElements().get( 1 ), is( instanceOf( UnaryTestNode.class ) ) );
        assertThat( list.getElements().get( 1 ).getText(), is( ">t" ) );

        assertThat( list.getElements().get( 2 ), is( instanceOf( NullNode.class ) ) );
        assertThat( list.getElements().get( 2 ).getText(), is( "null" ) );

        assertThat( list.getElements().get( 3 ), is( instanceOf( IntervalNode.class ) ) );
        IntervalNode interval = (IntervalNode) list.getElements().get( 3 );
        assertThat( interval.getText(), is( "(2000..z[") );
        assertThat( interval.getLowerBound(), is( IntervalNode.IntervalBoundary.OPEN ) );
        assertThat( interval.getUpperBound(), is( IntervalNode.IntervalBoundary.OPEN ) );
        assertThat( interval.getStart(), is( instanceOf( NumberNode.class ) ) );
        assertThat( interval.getEnd(), is( instanceOf( NameRefNode.class ) ) );

        assertThat( list.getElements().get( 4 ), is( instanceOf( IntervalNode.class ) ) );
        interval = (IntervalNode) list.getElements().get( 4 );
        assertThat( interval.getText(), is( "]z..2000]") );
        assertThat( interval.getLowerBound(), is( IntervalNode.IntervalBoundary.OPEN ) );
        assertThat( interval.getUpperBound(), is( IntervalNode.IntervalBoundary.CLOSED ) );
        assertThat( interval.getStart(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( interval.getEnd(), is( instanceOf( NumberNode.class ) ) );

        assertThat( list.getElements().get( 5 ), is( instanceOf( IntervalNode.class ) ) );
        interval = (IntervalNode) list.getElements().get( 5 );
        assertThat( interval.getText(), is( "[(10+5)..(a*b))") );
        assertThat( interval.getLowerBound(), is( IntervalNode.IntervalBoundary.CLOSED ) );
        assertThat( interval.getUpperBound(), is( IntervalNode.IntervalBoundary.OPEN ) );
        assertThat( interval.getStart(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( interval.getEnd(), is( instanceOf( InfixOpNode.class ) ) );

    }

    @Test
    public void testInUnaryTest() {
        String inputExpression = "x - y in [(10+5)..(a*b))";
        BaseNode inNode = parse( inputExpression );

        assertThat( inNode, is( instanceOf( InNode.class ) ) );
        assertThat( inNode.getText(), is( inputExpression ) );

        InNode in = (InNode) inNode;
        assertThat( in.getValue(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( in.getValue().getText(), is( "x - y" ) );

        assertThat( in.getExprs(), is( instanceOf( IntervalNode.class ) ) );
        assertThat( in.getExprs().getText(), is( "[(10+5)..(a*b))" ) );
    }

    @Test
    public void testComparisonInFixOp() {
        String inputExpression = "foo >= bar * 10";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode in = (InfixOpNode) infix;
        assertThat( in.getLeft(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( in.getLeft().getText(), is( "foo" ) );

        assertThat( in.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( in.getRight().getText(), is( "bar * 10" ) );
    }

    @Test
    public void testConditionalLogicalOp() {
        String inputExpression = "foo < 10 and bar = \"x\" or baz";
        BaseNode infix = parse( inputExpression );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( inputExpression ) );

        InfixOpNode or = (InfixOpNode) infix;
        assertThat( or.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( or.getLeft().getText(), is( "foo < 10 and bar = \"x\"" ) );

        assertThat( or.getOperator(), is( "or" ) );

        assertThat( or.getRight(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( or.getRight().getText(), is( "baz" ) );

        InfixOpNode and = (InfixOpNode) or.getLeft();
        assertThat( and.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( and.getLeft().getText(), is( "foo < 10" ) );

        assertThat( and.getOperator(), is( "and" ) );

        assertThat( and.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( and.getRight().getText(), is( "bar = \"x\"" ) );
    }

    @Test
    public void testEmptyList() {
        String inputExpression = "[]";
        BaseNode list = parse( inputExpression );

        assertThat( list, is( instanceOf( ListNode.class ) ) );
        assertThat( list.getText(), is( inputExpression ) );

        ListNode ln = (ListNode) list;
        assertThat( ln.getElements(), is( empty() ));
    }

    @Test
    public void testExpressionList() {
        String inputExpression = "[ 10, foo * bar, true ]";
        BaseNode list = parse( inputExpression );

        assertThat( list, is( instanceOf( ListNode.class ) ) );
        assertThat( list.getText(), is( "10, foo * bar, true" ) );

        ListNode ln = (ListNode) list;
        assertThat( ln.getElements().size(), is( 3 ) );
        assertThat( ln.getElements().get( 0 ), is( instanceOf( NumberNode.class ) ) );
        assertThat( ln.getElements().get( 1 ), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( ln.getElements().get( 2 ), is( instanceOf( BooleanNode.class ) ) );
    }

    @Test
    public void testEmptyContext() {
        String inputExpression = "{}";
        BaseNode list = parse( inputExpression );

        assertThat( list, is( instanceOf( ContextNode.class ) ) );
        assertThat( list.getText(), is( inputExpression ) );

        ContextNode ctx = (ContextNode) list;
        assertThat( ctx.getEntries(), is( empty() ));
    }

    @Test
    public void testContextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10,"
                       + " a non-string key : foo+bar,"
                       + " a key.with + /' odd chars : [10..50] }";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase, is( instanceOf( ContextNode.class ) ) );
        assertThat( ctxbase.getText(), is( inputExpression ) );

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries().size(), is( 3 ) );

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getName(), is( notNullValue() ) );
        assertThat( name.getName(), is("\"a string key\"") );
        assertThat( entry.getValue(), is( instanceOf( NumberNode.class ) ) );
        assertThat( entry.getValue().getText(), is("10") );

        entry = ctx.getEntries().get( 1 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        name = (NameDefNode) entry.getName();
        assertThat( name.getParts(), is( notNullValue() ) );
        assertThat( name.getParts().size(), is( 5 ) );
        assertThat( entry.getName().getText(), is("a non-string key") );
        assertThat( entry.getValue(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( entry.getValue().getText(), is( "foo+bar" ) );

        entry = ctx.getEntries().get( 2 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        name = (NameDefNode) entry.getName();
        assertThat( name.getParts(), is( notNullValue() ) );
        assertThat( name.getParts().size(), is( 9 ) );
        assertThat( entry.getName().getText(), is("a key.with + /' odd chars") );
        assertThat( entry.getValue(), is( instanceOf( IntervalNode.class ) ) );
        assertThat( entry.getValue().getText(), is( "[10..50]" ) );
    }

    @Test
    public void testNestedContexts() {
        String inputExpression = "{ a value : 10,"
                       + " an applicant : { "
                       + "    first name : \"Edson\", "
                       + "    last name : \"Tirelli\", "
                       + "    address : {"
                       + "        street : \"55 broadway st\","
                       + "        city : \"New York\" "
                       + "    }"
                       + " } "
                       + "}";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase, is( instanceOf( ContextNode.class ) ) );
        assertThat( ctxbase.getText(), is( inputExpression ) );

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries().size(), is( 2 ) );

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText(), is("a value") );
        assertThat( entry.getValue(), is( instanceOf( NumberNode.class ) ) );
        assertThat( entry.getValue().getText(), is("10") );

        entry = ctx.getEntries().get( 1 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        name = (NameDefNode) entry.getName();
        assertThat( name.getText(), is( "an applicant" ) );
        assertThat( entry.getValue(), is( instanceOf( ContextNode.class ) ) );

        ContextNode applicant = (ContextNode) entry.getValue();
        assertThat( applicant.getEntries().size(), is( 3 ) );
        assertThat( applicant.getEntries().get( 0 ).getName().getText(), is("first name") );
        assertThat( applicant.getEntries().get( 1 ).getName().getText(), is("last name") );
        assertThat( applicant.getEntries().get( 2 ).getName().getText(), is("address") );
        assertThat( applicant.getEntries().get( 2 ).getValue(), is( instanceOf( ContextNode.class ) ) );

        ContextNode address = (ContextNode) applicant.getEntries().get( 2 ).getValue();
        assertThat( address.getEntries().size(), is( 2 ) );
        assertThat( address.getEntries().get( 0 ).getName().getText(), is("street") );
        assertThat( address.getEntries().get( 1 ).getName().getText(), is("city") );
    }

    @Test @Ignore("requires implementing name ref for multi-inputExpression names")
    public void testFunctionDefinition() {
        String inputExpression = "{ is minor : function( person's age ) person's age < 21 }";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase, is( instanceOf( ContextNode.class ) ) );
        assertThat( ctxbase.getText(), is( inputExpression ) );

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries().size(), is( 1 ) );

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText(), is("is minor") );
        assertThat( entry.getValue(), is( instanceOf( FunctionDefNode.class ) ) );
        assertThat( entry.getValue().getText(), is("function( person's age ) person's age < 21") );

        FunctionDefNode isMinorFunc = (FunctionDefNode) entry.getValue();
        assertThat( isMinorFunc.getFormalParameters().size(), is( 1 ) );
        assertThat( isMinorFunc.getFormalParameters().get( 0 ).getText(), is( "person's age" ) );
        assertThat( isMinorFunc.isExternal(), is( false ) );
        assertThat( isMinorFunc.getBody(), is( instanceOf( InfixOpNode.class ) ) );
    }

    @Test
    public void testExternalFunctionDefinition() {
        String inputExpression = "{ trigonometric cosine : function( angle ) external {"
                       + "    java : {"
                       + "        class : \"java.lang.Math\","
                       + "        method signature : \"cos(double)\""
                       + "    }"
                       + "}}";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase, is( instanceOf( ContextNode.class ) ) );
        assertThat( ctxbase.getText(), is( inputExpression ) );

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries().size(), is( 1 ) );

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName(), is( instanceOf( NameDefNode.class ) ) );
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText(), is("trigonometric cosine") );
        assertThat( entry.getValue(), is( instanceOf( FunctionDefNode.class ) ) );
        assertThat( entry.getValue().getText(), is("function( angle ) external {"
                                                 + "    java : {"
                                                 + "        class : \"java.lang.Math\","
                                                 + "        method signature : \"cos(double)\""
                                                 + "    }"
                                                 + "}" ) );

        FunctionDefNode cos = (FunctionDefNode) entry.getValue();
        assertThat( cos.getFormalParameters().size(), is( 1 ) );
        assertThat( cos.getFormalParameters().get( 0 ).getText(), is( "angle" ) );
        assertThat( cos.isExternal(), is( true ) );
        assertThat( cos.getBody(), is( instanceOf( ContextNode.class ) ) );

        ContextNode body = (ContextNode) cos.getBody();
        assertThat( body.getEntries().size(), is( 1 ) );
        ContextEntryNode java = body.getEntries().get( 0 );
        assertThat( java.getName().getText(), is( "java" ) );
        assertThat( java.getValue(), is( instanceOf( ContextNode.class ) ) );

        ContextNode def = (ContextNode) java.getValue();
        assertThat( def.getEntries().size(), is( 2 ) );
        assertThat( def.getEntries().get( 0 ).getName().getText(), is( "class" ) );
        assertThat( def.getEntries().get( 0 ).getValue(), is( instanceOf( StringNode.class ) ) );
        assertThat( def.getEntries().get( 0 ).getValue().getText(), is( "\"java.lang.Math\"" ) );
        assertThat( def.getEntries().get( 1 ).getName().getText(), is( "method signature" ) );
        assertThat( def.getEntries().get( 1 ).getValue(), is( instanceOf( StringNode.class ) ) );
        assertThat( def.getEntries().get( 1 ).getValue().getText(), is( "\"cos(double)\"" ) );
    }

    @Test
    public void testForExpression() {
        String inputExpression = "for item in order.items return item.price * item.quantity";
        BaseNode forbase = parse( inputExpression );

        assertThat( forbase, is( instanceOf( ForExpressionNode.class ) ) );
        assertThat( forbase.getText(), is( inputExpression ) );

        ForExpressionNode forExpr = (ForExpressionNode) forbase;
        assertThat( forExpr.getIterationContexts().size(), is( 1 ) );
        assertThat( forExpr.getExpression(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( forExpr.getExpression().getText(), is( "item.price * item.quantity" ) );

        IterationContextNode ic = forExpr.getIterationContexts().get( 0 );
        assertThat( ic.getName().getText(), is("item") );
        assertThat( ic.getExpression(), is( instanceOf( QualifiedNameNode.class ) ) );
        assertThat( ic.getExpression().getText(), is("order.items") );
    }

    @Test
    public void testIfExpression() {
        String inputExpression = "if applicant.age < 18 then \"declined\" else \"accepted\"";
        BaseNode ifBase = parse( inputExpression );

        assertThat( ifBase, is( instanceOf( IfExpressionNode.class ) ) );
        assertThat( ifBase.getText(), is( inputExpression ) );

        IfExpressionNode ifExpr = (IfExpressionNode) ifBase;
        assertThat( ifExpr.getCondition().getText(), is( "applicant.age < 18" ) );
        assertThat( ifExpr.getThenExpression().getText(), is( "\"declined\"" ) );
        assertThat( ifExpr.getElseExpression().getText(), is( "\"accepted\"" ) );
    }

    @Test
    public void testQuantifiedExpressionSome() {
        String inputExpression = "some item in order.items satisfies item.price > 100";
        BaseNode someBase = parse( inputExpression );

        assertThat( someBase, is( instanceOf( QuantifiedExpressionNode.class ) ) );
        assertThat( someBase.getText(), is( inputExpression ) );

        QuantifiedExpressionNode someExpr = (QuantifiedExpressionNode) someBase;
        assertThat( someExpr.getQuantifier(), is( QuantifiedExpressionNode.Quantifier.SOME ) );
        assertThat( someExpr.getIterationContexts().size(), is( 1 ) );
        assertThat( someExpr.getIterationContexts().get( 0 ).getText(), is( "item in order.items" ) );
        assertThat( someExpr.getExpression().getText(), is( "item.price > 100" ) );
    }

    @Test
    public void testQuantifiedExpressionEvery() {
        String inputExpression = "every item in order.items satisfies item.price > 100";
        BaseNode someBase = parse( inputExpression );

        assertThat( someBase, is( instanceOf( QuantifiedExpressionNode.class ) ) );
        assertThat( someBase.getText(), is( inputExpression ) );

        QuantifiedExpressionNode someExpr = (QuantifiedExpressionNode) someBase;
        assertThat( someExpr.getQuantifier(), is( QuantifiedExpressionNode.Quantifier.EVERY ) );
        assertThat( someExpr.getIterationContexts().size(), is( 1 ) );
        assertThat( someExpr.getIterationContexts().get( 0 ).getText(), is( "item in order.items" ) );
        assertThat( someExpr.getExpression().getText(), is( "item.price > 100" ) );
    }

    @Test
    public void testInstanceOfExpression() {
        String inputExpression = "order.item.product instance of a.Product";
        BaseNode instanceOfBase = parse( inputExpression );

        assertThat( instanceOfBase, is( instanceOf( InstanceOfNode.class ) ) );
        assertThat( instanceOfBase.getText(), is( inputExpression ) );

        InstanceOfNode ioExpr = (InstanceOfNode) instanceOfBase;
        assertThat( ioExpr.getExpression(), is( instanceOf( QualifiedNameNode.class ) ) );
        assertThat( ioExpr.getExpression().getText(), is( "order.item.product" ) );
        assertThat( ioExpr.getType(), is( instanceOf( QualifiedNameNode.class ) ) );
        assertThat( ioExpr.getType().getText(), is( "a.Product" ) );
    }

    @Test
    public void testPathExpression() {
        String inputExpression = "[ 10, 15 ].size";
        BaseNode pathBase = parse( inputExpression );

        assertThat( pathBase, is( instanceOf( PathExpressionNode.class ) ) );
        assertThat( pathBase.getText(), is( inputExpression ) );

        PathExpressionNode pathExpr = (PathExpressionNode) pathBase;
        assertThat( pathExpr.getExpression(), is( instanceOf( ListNode.class ) ) );
        assertThat( pathExpr.getExpression().getText(), is( "10, 15" ) );
        assertThat( pathExpr.getName(), is( instanceOf( NameRefNode.class ) ) );
        assertThat( pathExpr.getName().getText(), is( "size" ) );
    }

    @Test
    public void testFilterExpression() {
        String inputExpression = "[ {x:1, y:2}, {x:2, y:3} ][ x=1 ]";
        BaseNode filterBase = parse( inputExpression );

        assertThat( filterBase, is( instanceOf( FilterExpressionNode.class ) ) );
        assertThat( filterBase.getText(), is( inputExpression ) );

        FilterExpressionNode filter = (FilterExpressionNode) filterBase;
        assertThat( filter.getExpression(), is( instanceOf( ListNode.class ) ) );
        assertThat( filter.getExpression().getText(), is( "{x:1, y:2}, {x:2, y:3}" ) );
        assertThat( filter.getFilter(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( filter.getFilter().getText(), is( "x=1" ) );
    }

    @Test
    public void testFunctionInvocationNamedParams() {
        String inputExpression = "my.test.Function( named parameter 1 : x+10, named parameter 2 : \"foo\" )";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase, is( instanceOf( FunctionInvocationNode.class ) ) );
        assertThat( functionBase.getText(), is( inputExpression ) );

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName(), is( instanceOf( QualifiedNameNode.class ) ) );
        assertThat( function.getName().getText(), is( "my.test.Function" ) );
        assertThat( function.getParams(), is( instanceOf( ListNode.class ) ) );
        assertThat( function.getParams().getElements().size(), is( 2 ) );
        assertThat( function.getParams().getElements().get( 0 ), is( instanceOf( NamedParameterNode.class ) ) );
        assertThat( function.getParams().getElements().get( 1 ), is( instanceOf( NamedParameterNode.class ) ) );

        NamedParameterNode named = (NamedParameterNode) function.getParams().getElements().get( 0 );
        assertThat( named.getText(), is( "named parameter 1 : x+10" ) );
        assertThat( named.getName().getText(), is( "named parameter 1" ) );
        assertThat( named.getExpression(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( named.getExpression().getText(), is( "x+10" ) );

        named = (NamedParameterNode) function.getParams().getElements().get( 1 );
        assertThat( named.getText(), is( "named parameter 2 : \"foo\"" ) );
        assertThat( named.getName().getText(), is( "named parameter 2" ) );
        assertThat( named.getExpression(), is( instanceOf( StringNode.class ) ) );
        assertThat( named.getExpression().getText(), is( "\"foo\"" ) );
    }

    @Test
    public void testFunctionInvocationPositionalParams() {
        String inputExpression = "my.test.Function( x+10, \"foo\" )";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase, is( instanceOf( FunctionInvocationNode.class ) ) );
        assertThat( functionBase.getText(), is( inputExpression ) );

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName(), is( instanceOf( QualifiedNameNode.class ) ) );
        assertThat( function.getName().getText(), is( "my.test.Function" ) );
        assertThat( function.getParams(), is( instanceOf( ListNode.class ) ) );
        assertThat( function.getParams().getElements().size(), is( 2 ) );
        assertThat( function.getParams().getElements().get( 0 ), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( function.getParams().getElements().get( 1 ), is( instanceOf( StringNode.class ) ) );
    }

    @Test
    public void testFunctionInvocationEmptyParams() {
        String inputExpression = "my.test.Function()";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase, is( instanceOf( FunctionInvocationNode.class ) ) );
        assertThat( functionBase.getText(), is( inputExpression ) );

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName(), is( instanceOf( QualifiedNameNode.class ) ) );
        assertThat( function.getName().getText(), is( "my.test.Function" ) );
        assertThat( function.getParams(), is( instanceOf( ListNode.class ) ) );
        assertThat( function.getParams().getElements(), is( empty() ) );
    }

    private void assertLocation(String inputExpression, BaseNode number) {
        assertThat( number.getText(), is( inputExpression ) );
        assertThat( number.getStartChar(), is( 0 ) );
        assertThat( number.getStartLine(), is( 1 ) );
        assertThat( number.getStartColumn(), is( 0 ) );
        assertThat( number.getEndChar(), is( inputExpression.length() - 1 ) );
        assertThat( number.getEndLine(), is( 1 ) );
        assertThat( number.getEndColumn(), is( inputExpression.length() ) );
    }

    private BaseNode parse(String input) {
        ParseTree tree = FEELParser.parse( input );
        ASTBuilderVisitor v = new ASTBuilderVisitor();
        BaseNode expr = v.visit( tree );
        return expr;
    }

}
