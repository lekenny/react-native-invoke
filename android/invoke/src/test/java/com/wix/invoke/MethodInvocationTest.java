package com.wix.invoke;

import com.wix.invoke.exceptions.EmptyInvocationInstructionException;
import com.wix.invoke.types.ClassTarget;
import com.wix.invoke.types.Invocation;
import com.wix.invoke.types.InvocationTarget;
import com.wix.invoke.types.ObjectInstanceTarget;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by rotemm on 10/10/2016.
 */
public class MethodInvocationTest {
    @Test(expected = EmptyInvocationInstructionException.class)
    public void invokeEmptyInvocation() {
        assertThat(MethodInvocation.invoke(new Invocation(null, null))).isNull();
    }

    @Test
    public void invokeStaticStringValueOfInteger() {
        Invocation invocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", 0);
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo("0");
    }

    @Test
    public void invokeStaticStringValueOfFloat() {
        Invocation invocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", 1.0f);
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo("1.0");
    }

    @Test
    public void invokeStaticStringValeOfBoolean() {
        Invocation invocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", true);
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo("true");
    }

    @Test
    public void invokeStaticStringValueOfChar() {
        Invocation invocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", 'c');
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo("c");
    }

    @Test
    public void invokeStaticMathMin1_2() {
        Invocation invocation = new Invocation(new ClassTarget("java.lang.Math"), "min", 1, 2);
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo(1);
    }

    @Test
    public void invokeStaticArraysBinarySearchFindExisting() {
        Invocation invocation = new Invocation(new ClassTarget("java.util.Arrays"), "binarySearch", new Object[]{1, 2, 3, 4, 5}, 3);
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo(2);
    }

    @Test
    public void invokeStaticArraysBinarySearchFindInRange() {
        Invocation invocation = new Invocation(new ClassTarget("java.util.Arrays"), "binarySearch", new Object[]{1, 2, 3, 4, 5}, 0, 1, 3);
        assertThat(MethodInvocation.invoke(invocation)).isEqualTo(-2);
    }

    @Test
    public void invokeMethodOnReturnValueOfStaticInvocation() {
        Invocation innerInvocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", 'c');
        Invocation outerInvocation = new Invocation(new InvocationTarget(innerInvocation), "length");
        assertThat(MethodInvocation.invoke(outerInvocation)).isEqualTo(1);
    }


    @Test
    public void invokeMethodOnReturnValueOfStaticInvocationTwoTimes() {
        Invocation innerInvocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", 'c');
        Invocation intermediateInvocation = new Invocation(new InvocationTarget(innerInvocation), "concat", "c");
        Invocation outerInvocation = new Invocation(new InvocationTarget(intermediateInvocation), "length");
        assertThat(MethodInvocation.invoke(outerInvocation)).isEqualTo(2);
    }

    @Test
    public void invokeMethodOnReturnValueOfStaticInvocationThreeTimes() {
        Invocation innerInvocation = new Invocation(new ClassTarget("java.lang.String"), "valueOf", 'c');
        Invocation intermediateInvocation1 = new Invocation(new InvocationTarget(innerInvocation), "concat", "c");
        Invocation intermediateInvocation2 = new Invocation(new InvocationTarget(intermediateInvocation1), "concat", "b");
        Invocation outerInvocation = new Invocation(new InvocationTarget(intermediateInvocation2), "length");
        assertThat(MethodInvocation.invoke(outerInvocation)).isEqualTo(3);
    }

    @Test
    public void invokeMethodOnObjectInstance() {
        Invocation innerInvocation = new Invocation(new ObjectInstanceTarget(new String("c")), "concat", "c");
        Invocation outerInvocation = new Invocation(new InvocationTarget(innerInvocation), "length");
        assertThat(MethodInvocation.invoke(outerInvocation)).isEqualTo(2);
    }

    @Test
    public void invokeMethodOnObjectInstanceTwice() {
        Invocation innerInvocation = new Invocation(new ObjectInstanceTarget(new String("c")), "concat", "c");
        Invocation intermediateInvocation = new Invocation(new InvocationTarget(innerInvocation), "concat", "c");
        Invocation outerInvocation = new Invocation(new InvocationTarget(intermediateInvocation), "length");
        assertThat(MethodInvocation.invoke(outerInvocation)).isEqualTo(3);
    }

    @Test
    public void fromJsonTargetClassStaticMethodNoParams() {
        assertThat(jsonToInvocation("targetClassStaticMethodNoParams.json")).isEqualTo(System.lineSeparator());
    }

    @Test
    public void fromJsonTargetClassStaticMethodOneParam() {
        assertThat(jsonToInvocation("targetClassStaticMethodOneParam.json")).isEqualTo("1.0");
    }

    @Test
    public void fromJsonTargetInvocationMethodOfClassStaticMethodOneParam() {
        assertThat(jsonToInvocation("targetInvocationMethodOfClassStaticMethodOneParam.json")).isEqualTo(3);
    }

    public Object jsonToInvocation(String filePath) {
        String jsonData = TestUtils.jsonFileToString(filePath);
        return MethodInvocation.invoke(jsonData);
    }
}